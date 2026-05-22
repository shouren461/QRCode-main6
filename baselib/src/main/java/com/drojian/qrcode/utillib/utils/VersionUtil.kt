package com.drojian.qrcode.utillib.utils

import android.content.Context
import android.os.Build
import com.drojian.qrcode.utillib.log.LogHelper.log
import java.util.Properties

object VersionUtil {

    @JvmStatic
    fun getVersionNameWithSmall(context: Context?): String {
        context?.let {
            try {
                val properties = Properties()
                properties.load(context.assets.open("config.properties"))
                if (properties.containsKey("version")) {
                    return "Version " + context.packageManager.getPackageInfo(context.packageName, 0).versionName + " " +
                            properties.getProperty("version")
                }
            } catch (e: Throwable) {
                e.log()
            }
        }
        return ""
    }

    @JvmStatic
    fun getVersionName(context: Context): String? {
        return context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }

    @JvmStatic
    fun getVersionCode(context: Context): Long {
        var versionCode = 0L
        try {
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode
            } else {
                context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toLong()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return versionCode
    }
}