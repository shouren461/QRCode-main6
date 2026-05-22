package com.drojian.qrcode.utillib.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import android.webkit.WebView
import com.drojian.qrcode.utillib.log.LogHelper.log
import kotlin.system.exitProcess

object AppUtil {

    @JvmStatic
    fun restartApp(activity: Activity, cls: Class<*>) {
        try {
            activity.finish()
            val intent = Intent(activity, cls)
            activity.startActivity(intent)
            Process.killProcess(Process.myPid())
            exitProcess(0)
        } catch (e: Exception) {
            e.log()
        }
    }

    @JvmStatic
    fun toMarket(context: Context, packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
            if (!TextUtils.isEmpty(packageName)) {
                intent.setPackage(packageName)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.log()
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } catch (e1: Exception) {
                e1.log()
            }
        }
    }

    @JvmStatic
    fun finishApp(activity: Activity?) {
        activity?.let {
            it.finish()
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }
    }

    @JvmStatic
    fun toAppSetting(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", context.packageName, null)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.log()
        }
    }

    @JvmStatic
    fun toWiFiSetting(context: Context?) {
        try {
            context?.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        } catch (e: Exception) {
            e.log()
        }
    }

    /**
     * android9.0以后不可多进程使用同一个目录webView
     */
    @JvmStatic
    fun initPieWebView(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                val processName = getProcessName(context)
                val packageName: String = context.packageName
                if (!TextUtils.isEmpty(processName) && packageName != processName) {
                    WebView.setDataDirectorySuffix(processName!!)
                }
            } catch (e: Exception) {
                e.log()
            }
        }
    }

    /**
     * 获取主进程名字
     */
    private fun getProcessName(context: Context?): String? {
        if (context == null) return null
        try {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (processInfo in manager.runningAppProcesses) {
                if (processInfo.pid == Process.myPid()) {
                    return processInfo.processName
                }
            }
        } catch (e: Exception) {
            e.log()
        }
        return null
    }
}