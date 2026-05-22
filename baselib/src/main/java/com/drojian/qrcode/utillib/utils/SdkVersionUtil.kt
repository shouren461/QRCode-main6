package com.drojian.qrcode.utillib.utils

import android.os.Build

object SdkVersionUtil {

    @JvmStatic
    fun isAndroid13(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.S
    }

    @JvmStatic
    fun isAndroid12(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.R
    }

    @JvmStatic
    fun isAndroid11(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    @JvmStatic
    fun isAndroid10(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    @JvmStatic
    fun isAndroid9(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    @JvmStatic
    fun isAndroid8(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    @JvmStatic
    fun isAndroid7(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    @JvmStatic
    fun isAndroid6(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }
}