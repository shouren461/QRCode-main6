package com.drojian.qrcode.utillib.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.text.TextUtils
import com.drojian.qrcode.utillib.log.LogHelper.log

object ClipboardUtil {

    @JvmStatic
    fun set(context: Context, s: String) {
        try {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val mClipData = ClipData.newPlainText("Label", s)
            cm.setPrimaryClip(mClipData)
        } catch (e: Throwable) {
            e.log()
        }
    }

    @JvmStatic
    fun get(activity: Activity, f: Function) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            getCBTextWithRetry(activity, f, 0)
        } else {
            f.invoke(getTextFromClip(activity))
        }
    }
    
    /**
     * 问题：Android11手机上，手机熄屏再唤醒后剪贴板内容不显示。
     * 原因：Android Q开始只有系统默认的输入法应用或者应用处于焦点时才能获取到剪切板的数据。
     * 修复方案：获取失败后重试三次，第一次等待20ms，第二次等待40ms，第三次等待60ms
     */
    private fun getCBTextWithRetry(activity: Activity, f: Function, count: Int) {
        getTextFromClipFromAndroidQ(activity, object : Function {
            override fun invoke(text: String?) {
                text?.let {
                    if (it.isNotEmpty()) {
                        f.invoke(it)
                        return@let
                    }
                    if (count < 3) {
                        getCBTextWithRetry(activity, f, count + 1)
                    } else {
                        f.invoke("")
                        return@let
                    }
                }
            }
        }, (count * 20).toLong())
    }
    
    /**
     * AndroidQ 获取剪贴板的内容
     */
    private fun getTextFromClipFromAndroidQ(activity: Activity, f: Function, delayMillis: Long) {
        activity.window.decorView.postDelayed({
            try {
                val clipboardManager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                if (!clipboardManager.hasPrimaryClip()) {
                    f.invoke("")
                    return@postDelayed
                }
                val clipData = clipboardManager.primaryClip
                if (null == clipData || clipData.itemCount < 1) {
                    f.invoke("")
                    return@postDelayed
                }
                val item = clipData.getItemAt(0)
                if (item == null) {
                    f.invoke("")
                    return@postDelayed
                }
                val clipText = item.text
                if (TextUtils.isEmpty(clipText)) {
                    f.invoke("")
                    return@postDelayed
                }
                f.invoke(clipText.toString())
            } catch (e: Exception) {
                try {
                    f.invoke("")
                } catch (e1: Exception) {
                    e.log()
                }
            }
        }, delayMillis)
    }
    
    /***
     * Android 10以下可直接获取
     */
    private fun getTextFromClip(activity: Activity): String? {
        try {
            val clipboardManager = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (clipboardManager.hasPrimaryClip().not()) {
                return ""
            }
            val clipData = clipboardManager.primaryClip
            if (null == clipData || clipData.itemCount < 1) {
                return ""
            }
            val item = clipData.getItemAt(0) ?: return ""
            val clipText = item.text
            return if (TextUtils.isEmpty(clipText)) "" else clipText.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    interface Function {
        operator fun invoke(text: String?)
    }
}