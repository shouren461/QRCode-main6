package com.drojian.qrcode.utillib.utils

import android.content.Context

object RtlUtil {
    @JvmStatic
    fun isRtl(context: Context): Boolean {
        try {
            val locale = context.resources.configuration.locale
            val language = locale.language.lowercase()
            if (language == "ar" || language == "iw" || language == "fa" || language == "ur") {
                return true
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return false
    }
}