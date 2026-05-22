package com.drojian.qrcode.utillib.utils

import android.content.Context
import com.drojian.qrcode.utillib.log.LogHelper.log

object DimensionUtil {
    private var statusBarHeight: Int? = null

    @JvmStatic
    fun screenWidthDps(context: Context): Int {
        return pxTodp(context, screenWidthPixels(context).toFloat())
    }

    @JvmStatic
    fun screenHeightDps(context: Context): Int {
        return pxTodp(context, screenHeightPixels(context).toFloat())
    }

    @JvmStatic
    fun screenWidthPixels(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    @JvmStatic
    fun screenHeightPixels(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    @JvmStatic
    fun dpTopx(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5).toInt()
    }

    @JvmStatic
    fun pxTodp(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5).toInt()
    }

    @JvmStatic
    fun getStatusBarHeight(context: Context): Int {
        statusBarHeight?.let { return it }
        statusBarHeight = dpTopx(context, 25f)
        try {
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId != 0) {
                statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
            }
        } catch (e: Exception) {
            e.log()
        }
        return statusBarHeight as Int
    }
}