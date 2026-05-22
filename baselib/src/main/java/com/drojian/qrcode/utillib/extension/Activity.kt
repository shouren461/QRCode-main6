package com.drojian.qrcode.utillib.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.drojian.qrcode.utillib.utils.DimensionUtil


@Suppress("DEPRECATION")
@SuppressLint("InlinedApi")
fun Activity.setFullScreen(isFullScreen: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (isFullScreen) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
            window.insetsController?.hide(WindowInsets.Type.navigationBars())
        } else {
            window.insetsController?.show(WindowInsets.Type.statusBars())
            window.insetsController?.show(WindowInsets.Type.navigationBars())
        }
    } else {
        if (isFullScreen) {
            val lp = window.attributes
            lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
            window.attributes = lp
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        } else {
            val lp = window.attributes
            lp.flags = lp.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            window.attributes = lp
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}

fun Activity.setStatusBarColor(@ColorInt color: Int, isLightStatusBar: Boolean = true) {
    // 6.0 以上系统使用沉浸式效果 6.0 以下系统使用设计给定颜色
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.statusBarColor = color
        window.decorView.setLightStatusBar(isLightStatusBar)
        window.navigationBarColor = color
        window.decorView.setLightNavigationBar(isLightStatusBar)
    } else {
        window.statusBarColor = Color.parseColor("#33000000")
    }
}

fun Activity.resetStatusBarColor() {
    window.statusBarColor = Color.parseColor("#33000000")
}

fun Activity.setTranslucentStatusBar(isLightStatusBar: Boolean = true) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window?.let {
            it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            it.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            it.statusBarColor = Color.TRANSPARENT
            it.decorView.setLightStatusBar(isLightStatusBar)
            it.decorView.setLightNavigationBar(isLightStatusBar)
        }
    }
}

fun Activity.setTranslucentStatusBar(view: View, isLightStatusBar: Boolean = true) {
    window?.let {
        it.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        it.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        it.statusBarColor = Color.TRANSPARENT
        it.decorView.setLightStatusBar(isLightStatusBar)
        it.decorView.setLightNavigationBar(isLightStatusBar)
    }

    val statusBarHeight = DimensionUtil.getStatusBarHeight(this)
    view.setPadding(view.paddingRight, statusBarHeight + view.paddingTop, view.paddingLeft, view.paddingBottom)
}

fun Window.hideNavigationBars() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {// Android 11+
        val controller = WindowCompat.getInsetsController(this, decorView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.navigationBars())
    } else {// Android 6～10
        val flags =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = flags
    }
}