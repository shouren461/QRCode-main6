package com.drojian.qrcode.scanresultlib.util.wifi.dialog

import android.app.Activity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.drojian.qrcode.scanresultlib.R

/**
 * @author yangfengfan 2020-12-28
 *
 * Wifi弹窗相关
 */
object WifiDialogHelper {

    var navigationBarColor: Int? = null
    var isDark: Boolean = false

    fun init(navigationBarColor: Int? = null, isDark: Boolean = false) {
        this.navigationBarColor = navigationBarColor
        this.isDark = isDark
    }

    @JvmStatic
    fun showWifiNotEnableDialog(activity: Activity) {
        try {
            activity.runOnUiThread {
                val fragmentActivity = activity as FragmentActivity
                WiFiNotEnableDialog.Builder(null).show(fragmentActivity.supportFragmentManager)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // WiFi未打开时tips弹窗
    @JvmStatic
    fun showWifiNotEnableTipsNewUser(activity: Activity, noPassword: Boolean) {
        val wiFiDialogParams = CommonWifiTipsDialog.WiFiDialogParams()
        wiFiDialogParams.title = activity.resources.getString(R.string.parse_action_title_connect_to_network)
        wiFiDialogParams.step1Hint = activity.resources.getString(R.string.lh_enable_wifi)
        wiFiDialogParams.noPassword = noPassword
        showWiFiTipsDialog(activity, wiFiDialogParams)
    }

    // WiFi未打开时tips弹窗
    @JvmStatic
    fun showWifiNotEnableTipsOldUser(activity: Activity, noPassword: Boolean) {
        val wiFiDialogParams = CommonWifiTipsDialog.WiFiDialogParams()
        wiFiDialogParams.title = activity.resources.getString(R.string.lh_tip)
        wiFiDialogParams.subTitle = activity.resources.getString(R.string.lh_manually_connect_wifi)
        wiFiDialogParams.step1Hint = activity.resources.getString(R.string.lh_enable_wifi)
        wiFiDialogParams.noPassword = noPassword
        showWiFiTipsDialog(activity, wiFiDialogParams)
    }

    // 新用户链接WiFi提示
    @JvmStatic
    fun showNewUserTips(activity: Activity, noPassword: Boolean, wifiDialogListener: WifiDialogListener? = null) {
        val wiFiDialogParams = CommonWifiTipsDialog.WiFiDialogParams().apply {
            this.wifiDialogListener = wifiDialogListener
        }
        wiFiDialogParams.title = activity.resources.getString(R.string.parse_action_title_connect_to_network)
        wiFiDialogParams.noPassword = noPassword
        val fragmentActivity = activity as FragmentActivity
        showWiFiTipsDialog(fragmentActivity, wiFiDialogParams)
    }

    // 老用户链接WiFi提示
    @JvmStatic
    fun showOldUserTips(activity: Activity, noPassword: Boolean, wifiDialogListener: WifiDialogListener? = null) {
        val wiFiDialogParams = CommonWifiTipsDialog.WiFiDialogParams().apply {
            this.wifiDialogListener = wifiDialogListener
        }
        wiFiDialogParams.title = activity.resources.getString(R.string.lh_tip)
        wiFiDialogParams.subTitle = activity.resources.getString(R.string.lh_manually_connect_wifi)
        wiFiDialogParams.noPassword = noPassword
        showWiFiTipsDialog(activity, wiFiDialogParams)
    }

    private fun showWiFiTipsDialog(activity: Activity, wiFiDialogParams: CommonWifiTipsDialog.WiFiDialogParams) {
        try {
            wiFiDialogParams.apply {
                if (WifiDialogHelper.navigationBarColor == null) {
                    ContextCompat.getColor(activity, R.color.dialog_bg)
                } else {
                    WifiDialogHelper.navigationBarColor?.let {
                        navigationBarColor = it
                    }
                }
                isDark = WifiDialogHelper.isDark
            }
            activity.runOnUiThread {
                val fragmentActivity: FragmentActivity = activity as FragmentActivity
                CommonWifiTipsDialog.Builder(wiFiDialogParams).show(fragmentActivity.supportFragmentManager)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}