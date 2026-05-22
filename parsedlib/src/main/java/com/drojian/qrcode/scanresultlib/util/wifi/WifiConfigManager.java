package com.drojian.qrcode.scanresultlib.util.wifi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.drojian.qrcode.scanlib.scan.parse.format.ParseWifiModel;
import com.drojian.qrcode.scanresultlib.util.wifi.dialog.WifiDialogHelper;
import com.drojian.qrcode.scanresultlib.util.wifi.dialog.WifiDialogListener;
import com.drojian.qrcode.scanresultlib.util.wifi.util.HandlerLibConfig;

import java.util.ArrayList;
import java.util.regex.Pattern;

public final class WifiConfigManager extends AsyncTask<ParseWifiModel, Object, Object> {

    private static final String TAG = WifiConfigManager.class.getSimpleName();

    private static final Pattern HEX_DIGITS = Pattern.compile("[0-9A-Fa-f]+");

    private final WifiManager wifiManager;

    @SuppressLint("StaticFieldLeak")
    private final Activity activity;
    private Boolean isIntentToSystemWifi = false;
    private ParseWifiModel theWifiResult;
    @Nullable
    public WifiDialogListener wifiDialogListener;

    public WifiConfigManager(WifiManager wifiManager, Activity activity) {
        this.wifiManager = wifiManager;
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(ParseWifiModel... args) {
        // Android10 链接Wifi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                this.theWifiResult = args[0];
                try {
                    if (!TextUtils.isEmpty(theWifiResult.getPassword())) {
                        ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("Label", theWifiResult.getPassword());
                        cm.setPrimaryClip(mClipData);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                boolean noPassword = theWifiResult.getNetworkEncryption().equals("nopass") || TextUtils.isEmpty(theWifiResult.getPassword());
                addNetworkSuggestions(activity, theWifiResult.getSsid(), theWifiResult.getPassword());
                wifiConnectAndroidQ(activity, noPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ParseWifiModel theWifiResult = args[0];
            // Start WiFi, otherwise nothing will work
            if (!wifiManager.isWifiEnabled()) {
                Log.i(TAG, "Enabling wi-fi...");
                if (wifiManager.setWifiEnabled(true)) {
                    Log.i(TAG, "Wi-fi enabled");
                    // 跳转到Wifi设置页
                    try {
                        if (!isIntentToSystemWifi) {
                            isIntentToSystemWifi = true;
                            activity.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                            IntentFilter filter = new IntentFilter();
                            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                            activity.registerReceiver(new WifiReceiver(), filter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.w(TAG, "Wi-fi could not be enabled!");
                    this.theWifiResult = theWifiResult;
                    // 打开失败后，跳转到Wifi设置页
                    try {
                        if (!isIntentToSystemWifi) {
                            isIntentToSystemWifi = true;
                            activity.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                            IntentFilter filter = new IntentFilter();
                            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                            activity.registerReceiver(new WifiReceiver(), filter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
                // This happens very quickly, but need to wait for it to enable. A little busy wait?
                int count = 0;
                while (!wifiManager.isWifiEnabled()) {
                    if (count >= 10) {
                        Log.i(TAG, "Took too long to enable wi-fi, quitting");
                        return null;
                    }
                    Log.i(TAG, "Still waiting for wi-fi to enable...");
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException ie) {
                        // continue
                    }
                    count++;
                }
            }
            String networkTypeString = theWifiResult.getNetworkEncryption();
            NetworkType networkType;
            try {
                networkType = NetworkType.forIntentValue(networkTypeString);
            } catch (IllegalArgumentException ignored) {
                Log.w(TAG, "Bad network type");
                return null;
            }
            try {
                if (!isIntentToSystemWifi) {
                    isIntentToSystemWifi = true;
                    activity.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                    activity.registerReceiver(new WifiReceiver(), filter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (networkType == NetworkType.NO_PASSWORD) {
                changeNetworkUnEncrypted(wifiManager, theWifiResult);
            } else {
                String password = theWifiResult.getPassword();
                if (password != null && !password.isEmpty()) {
                    switch (networkType) {
                        case WEP:
                            changeNetworkWEP(wifiManager, theWifiResult);
                            break;
                        case WPA:
                            changeNetworkWPA(wifiManager, theWifiResult);
                            break;
                        case WPA2_EAP:
                            changeNetworkWPA2EAP(wifiManager, theWifiResult);
                            break;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Update the network: either create a new network or modify an existing network
     *
     * @param config the new network configuration
     */
    private static void updateNetwork(WifiManager wifiManager, WifiConfiguration config) {
        try {
            Integer foundNetworkID = findNetworkInExistingConfig(wifiManager, config.SSID);
            if (foundNetworkID != null) {
                Log.i(TAG, "Removing old configuration for network " + config.SSID);
                wifiManager.removeNetwork(foundNetworkID);
                wifiManager.saveConfiguration();
            }
            int networkId = wifiManager.addNetwork(config);
            if (networkId >= 0) {
                // Try to disable the current network and start a new one.
                if (wifiManager.enableNetwork(networkId, true)) {
                    Log.i(TAG, "Associating to network " + config.SSID);
                    wifiManager.saveConfiguration();
                } else {
                    Log.w(TAG, "Failed to enable network " + config.SSID);
                }
            } else {
                Log.w(TAG, "Unable to add network " + config.SSID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static WifiConfiguration changeNetworkCommon(ParseWifiModel wifiResult) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        // Android API insists that an ascii SSID must be quoted to be correctly handled.
        config.SSID = quoteNonHex(wifiResult.getSsid());
        config.hiddenSSID = wifiResult.getHidden();
        return config;
    }

    // Adding a WEP network
    private static void changeNetworkWEP(WifiManager wifiManager, ParseWifiModel wifiResult) {
        WifiConfiguration config = changeNetworkCommon(wifiResult);
        config.wepKeys[0] = quoteNonHex(wifiResult.getPassword(), 10, 26, 58);
        config.wepTxKeyIndex = 0;
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        updateNetwork(wifiManager, config);
    }

    // Adding a WPA or WPA2 network
    private static void changeNetworkWPA(WifiManager wifiManager, ParseWifiModel wifiResult) {
        WifiConfiguration config = changeNetworkCommon(wifiResult);
        // Hex passwords that are 64 bits long are not to be quoted.
        config.preSharedKey = quoteNonHex(wifiResult.getPassword(), 64);
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA); // For WPA
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN); // For WPA2
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        updateNetwork(wifiManager, config);
    }

    // Adding a WPA2 enterprise (EAP) network
    private static void changeNetworkWPA2EAP(WifiManager wifiManager, ParseWifiModel wifiResult) {
        WifiConfiguration config = changeNetworkCommon(wifiResult);
        // Hex passwords that are 64 bits long are not to be quoted.
        config.preSharedKey = quoteNonHex(wifiResult.getPassword(), 64);
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN); // For WPA2
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.enterpriseConfig.setIdentity(wifiResult.getIdentity());
        config.enterpriseConfig.setAnonymousIdentity(wifiResult.getAnonymousIdentity());
        config.enterpriseConfig.setPassword(wifiResult.getPassword());
        config.enterpriseConfig.setEapMethod(parseEap(wifiResult.getEapMethod()));
        config.enterpriseConfig.setPhase2Method(parsePhase2(wifiResult.getPhase2Method()));
        updateNetwork(wifiManager, config);
    }

    // Adding an open, unsecured network
    private static void changeNetworkUnEncrypted(WifiManager wifiManager, ParseWifiModel wifiResult) {
        WifiConfiguration config = changeNetworkCommon(wifiResult);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        updateNetwork(wifiManager, config);
    }

    private static Integer findNetworkInExistingConfig(WifiManager wifiManager, String ssid) {
        try {

            @SuppressLint("MissingPermission")
            Iterable<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
            if (existingConfigs != null) {
                for (WifiConfiguration existingConfig : existingConfigs) {
                    String existingSSID = existingConfig.SSID;
                    if (existingSSID != null && existingSSID.equals(ssid)) {
                        return existingConfig.networkId;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String quoteNonHex(String value, int... allowedLengths) {
        return isHexOfLength(value, allowedLengths) ? value : convertToQuotedString(value);
    }

    /**
     * Encloses the incoming string inside double quotes, if it isn't already quoted.
     *
     * @param s the input string
     * @return a quoted string, of the form "input".  If the input string is null, it returns null as well.
     */
    private static String convertToQuotedString(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        // If already quoted, return as-is
        if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
            return s;
        }
        return '\"' + s + '\"';
    }

    /**
     * @param value          input to check
     * @param allowedLengths allowed lengths, if any
     * @return true if value is a non-null, non-empty string of hex digits, and if allowed lengths are given, has an allowed length
     */
    private static boolean isHexOfLength(CharSequence value, int... allowedLengths) {
        if (value == null || !HEX_DIGITS.matcher(value).matches()) {
            return false;
        }
        if (allowedLengths.length == 0) {
            return true;
        }
        for (int length : allowedLengths) {
            if (value.length() == length) {
                return true;
            }
        }
        return false;
    }

    private static int parseEap(String eapString) {
        if (eapString == null) {
            return WifiEnterpriseConfig.Eap.NONE;
        }
        switch (eapString) {
            case "NONE":
                return WifiEnterpriseConfig.Eap.NONE;
            case "PEAP":
                return WifiEnterpriseConfig.Eap.PEAP;
            case "PWD":
                return WifiEnterpriseConfig.Eap.PWD;
            case "TLS":
                return WifiEnterpriseConfig.Eap.TLS;
            case "TTLS":
                return WifiEnterpriseConfig.Eap.TTLS;
            default:
                throw new IllegalArgumentException("Unknown value for EAP method: " + eapString);
        }
    }

    private static int parsePhase2(String phase2String) {
        if (phase2String == null) {
            return WifiEnterpriseConfig.Phase2.NONE;
        }
        switch (phase2String) {
            case "GTC":
                return WifiEnterpriseConfig.Phase2.GTC;
            case "MSCHAP":
                return WifiEnterpriseConfig.Phase2.MSCHAP;
            case "MSCHAPV2":
                return WifiEnterpriseConfig.Phase2.MSCHAPV2;
            case "NONE":
                return WifiEnterpriseConfig.Phase2.NONE;
            case "PAP":
                return WifiEnterpriseConfig.Phase2.PAP;
            default:
                throw new IllegalArgumentException("Unknown value for phase 2 method: " + phase2String);
        }
    }

    /**
     * Android10以后链接Wifi方案
     */
    private void addNetworkSuggestions(Context context, String networkName, String networkPassword) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiNetworkSuggestion.Builder builder = new WifiNetworkSuggestion.Builder().setSsid(networkName);
                ArrayList<WifiNetworkSuggestion> list = new ArrayList<>();
                if (networkPassword != null && !networkPassword.isEmpty()) {
                    builder.setWpa2Passphrase(networkPassword);
                }
                list.add(builder.build());
                int result = wifiManager.addNetworkSuggestions(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class WifiReceiver extends BroadcastReceiver {

        private static final String TAG = "wifiReceiver";
        private boolean isConnecting = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                //wifi打开与否
                if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
                    if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                        if (isConnecting) {
                            return;
                        }
                        isConnecting = true;
                        String networkTypeString = theWifiResult.getNetworkEncryption();
                        NetworkType networkType;
                        try {
                            networkType = NetworkType.forIntentValue(networkTypeString);
                        } catch (Exception ignored) {
                            isConnecting = false;
                            return;
                        }
                        if (networkType == NetworkType.NO_PASSWORD) {
                            changeNetworkUnEncrypted(wifiManager, theWifiResult);
                        } else {
                            String password = theWifiResult.getPassword();
                            if (password != null && !password.isEmpty()) {
                                switch (networkType) {
                                    case WEP:
                                        changeNetworkWEP(wifiManager, theWifiResult);
                                        break;
                                    case WPA:
                                        changeNetworkWPA(wifiManager, theWifiResult);
                                        break;
                                    case WPA2_EAP:
                                        changeNetworkWPA2EAP(wifiManager, theWifiResult);
                                        break;
                                }
                            }
                        }
                        isConnecting = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Android10及以上链接Wifi方案
     */
    private void wifiConnectAndroidQ(Context context, boolean noPassword) {
        if (wifiManager.isWifiEnabled()) {
            if (HandlerLibConfig.INSTANCE.needShowWiFiTipsDialog(context)) {
                HandlerLibConfig.INSTANCE.addWiFiConnectTipsShowTimes(context);
                if (HandlerLibConfig.INSTANCE.isFirstConnectWifi(context)) {
                    WifiDialogHelper.INSTANCE.showNewUserTips(activity, noPassword, wifiDialogListener);
                } else {
                    WifiDialogHelper.INSTANCE.showOldUserTips(activity, noPassword, wifiDialogListener);
                }
                HandlerLibConfig.INSTANCE.setIsFirstConnectWifi(context, false);
            } else {
                goWiFiSetting(context);
            }
        } else {// WiFi未开启
            if (HandlerLibConfig.INSTANCE.needShowWiFiTipsDialog(context)) {
                HandlerLibConfig.INSTANCE.addWiFiConnectTipsShowTimes(context);
                if (HandlerLibConfig.INSTANCE.isFirstConnectWifi(activity)) {
                    WifiDialogHelper.INSTANCE.showWifiNotEnableTipsNewUser(activity, noPassword);
                } else {
                    WifiDialogHelper.INSTANCE.showWifiNotEnableTipsOldUser(activity, noPassword);
                }
                HandlerLibConfig.INSTANCE.setIsFirstConnectWifi(context, false);
            } else {
                WifiDialogHelper.INSTANCE.showWifiNotEnableDialog(activity);
            }
        }
    }

    private void goWiFiSetting(Context context) {
        try {
            context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}