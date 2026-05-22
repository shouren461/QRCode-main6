package com.drojian.qrcode.utillib.extension

import android.os.Build
import android.view.View


fun View.setSystemUiVisibility(flag: Int, enabled: Boolean) {
    var flags = systemUiVisibility
    flags = if (enabled) flags or flag else flags and flag.inv()
    systemUiVisibility = flags
}

fun View.setLightStatusBar(enabled: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR, enabled)
    }
}

fun View.setLightNavigationBar(enabled: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR, enabled)
    }
}

fun View.setGone() {
    visibility = View.GONE
}

fun View.setVisible() {
    visibility = View.VISIBLE
}