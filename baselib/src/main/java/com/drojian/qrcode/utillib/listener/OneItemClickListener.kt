package com.drojian.qrcode.utillib.listener

import android.view.View
import android.widget.AdapterView

/**
 * @author yangfengfan@drojian.dev
 */
abstract class OneItemClickListener(private val intervalMillis: Long = 1000L) : AdapterView.OnItemClickListener {

    private var mLastClickTime = 0L

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val currentClickTime = System.currentTimeMillis()
        val elapsedTime = currentClickTime - mLastClickTime
        mLastClickTime = currentClickTime
        if (elapsedTime <= intervalMillis) return
        oneItemClickListener(parent, view, position, id)
    }

    abstract fun oneItemClickListener(parent: AdapterView<*>?, view: View?, position: Int, id: Long)

}