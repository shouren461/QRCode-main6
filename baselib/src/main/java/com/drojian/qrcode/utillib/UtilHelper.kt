package com.drojian.qrcode.utillib

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object UtilHelper {

    var context: Context? = null
    var spName: String = "prefs"
    var isRelease = true

    fun init(mContext: Context, release: Boolean = false) {
        context = mContext
        isRelease = release
    }
}