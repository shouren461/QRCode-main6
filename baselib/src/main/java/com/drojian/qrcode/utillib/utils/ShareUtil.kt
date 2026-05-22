package com.drojian.qrcode.utillib.utils

import android.app.Activity
import android.content.Intent

object ShareUtil {

    fun shareText(activity: Activity, msg: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, msg)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivityForResult(Intent.createChooser(intent, "Share"), 0)
    }

}