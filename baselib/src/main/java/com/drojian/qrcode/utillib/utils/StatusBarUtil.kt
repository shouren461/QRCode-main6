package com.drojian.qrcode.utillib.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager

object StatusBarUtil {
    /**
     * 设置状态栏文字色值为深色调
     * @param isTextBlack 是否使用深色调
     * @param activity
     */
    @JvmStatic
    fun setTransparentBar(isTextBlack: Boolean, activity: Activity) {
        if (isTextBlack) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        } else {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        activity.window.statusBarColor = Color.TRANSPARENT
    }

    @JvmStatic
    fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    @JvmStatic
    fun setStatusBarColor(activity: Activity, color: Int) {
        activity.window?.statusBarColor = color
    }

    @JvmStatic
    fun setStatusBarColor(activity: Activity, color: Int, isTextBlack: Boolean) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        activity.window.statusBarColor = color
        if (isTextBlack) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    @JvmStatic
    fun openFullScreenModel(activity: Activity) {
        try {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                    val lp = activity.window.attributes
                    lp.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                    activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    activity.window.attributes = lp
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    setTransparentBar(false, activity)
                }
                else -> {
                    setStatusBarColor(activity, Color.BLACK)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun hideStatusBarText(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
}