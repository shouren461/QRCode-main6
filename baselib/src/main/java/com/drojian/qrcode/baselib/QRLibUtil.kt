package com.drojian.qrcode.baselib

import android.content.Context

object QRLibUtil {
    /**
     * 根据配置的 build_time 时间戳，判断是否在发内测12小时内
     */
    fun isBundle12H(context: Context): Boolean {
        var time: Long = 0

        // 自定义的的qr_build_time
        var time2: Long = 0
        try {
            time2 = context.resources.getInteger(R.integer.qr_build_time) * 10000L
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return time > System.currentTimeMillis() || time2 > System.currentTimeMillis()
    }
    //通用 隐藏导航栏
    var shouldHideDialogNavigationBar: Boolean = false
}