package com.drojian.qrcode.scanresultlib.util.wifi.util

import android.content.Context
import com.drojian.qrcode.scanresultlib.util.wifi.util.SharePrefUtil.Companion.getInstance

/**
 * @author yangfengfan 2020-12-28
 */
object HandlerLibConfig {
    var PREF_KEY_IS_FIRST_CONNECT_WIFI = "lh_pref_is_first_connect_wifi"
    var PREF_KEY_WIFI_CONNECT_TIPS_SHOW_TIMES = "lh_pref_wifi_connect_tips_show_times"

    private var isFirstConnectWifi = true
    private var wifiConnectTipsShowTimes = 0

    fun setIsFirstConnectWifi(context: Context, b: Boolean) {
        isFirstConnectWifi = b
        getInstance(context)?.setPref(PREF_KEY_IS_FIRST_CONNECT_WIFI, isFirstConnectWifi)
    }

    fun isFirstConnectWifi(context: Context): Boolean {
        getInstance(context)?.let {
            isFirstConnectWifi = it.getPref(PREF_KEY_IS_FIRST_CONNECT_WIFI, isFirstConnectWifi)
        }
        return isFirstConnectWifi
    }

    fun addWiFiConnectTipsShowTimes(context: Context) {
        wifiConnectTipsShowTimes++
        getInstance(context)?.setPref(
            PREF_KEY_WIFI_CONNECT_TIPS_SHOW_TIMES,
            wifiConnectTipsShowTimes
        )
    }

    // 提示两次
    fun needShowWiFiTipsDialog(context: Context): Boolean {
        getInstance(context)?.let {
            wifiConnectTipsShowTimes =
                it.getPref(PREF_KEY_WIFI_CONNECT_TIPS_SHOW_TIMES, wifiConnectTipsShowTimes)
        }
        return wifiConnectTipsShowTimes < 2
    }

}