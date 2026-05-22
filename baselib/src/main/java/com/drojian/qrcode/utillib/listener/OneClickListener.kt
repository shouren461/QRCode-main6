package com.drojian.qrcode.utillib.listener

import android.view.View
import kotlin.math.abs

/**
 * @author yangfengfan@drojian.dev
 * 过滤双击的ClickListener
 */
abstract class OneClickListener(private val intervalMillis: Long = 500L) : View.OnClickListener {

    abstract fun onSingleClick(v: View?)


    private var mLastClickTime = 0L

    override fun onClick(view: View?) {
        val currentClickTime = System.currentTimeMillis()
        val elapsedTime = abs(currentClickTime - mLastClickTime)
        if (elapsedTime >= intervalMillis) {
            mLastClickTime = currentClickTime
            onSingleClick(view)
        }
    }
}


fun View.setOneClickListener(handlerClick: () -> Unit) {
    setOnClickListener(object : OneClickListener() {
        override fun onSingleClick(v: View?) {
            handlerClick()
        }
    })
}