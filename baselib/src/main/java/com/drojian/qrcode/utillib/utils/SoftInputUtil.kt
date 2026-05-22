package com.drojian.qrcode.utillib.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object SoftInputUtil {

    @JvmStatic
    fun show(view: View?) {
        view?.let {
            it.postDelayed({
                try {
                    view.requestFocus()
                    val imm = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                } catch (e: Exception) {
                   e.printStackTrace()
                }
            }, 200)
        }
    }

    @JvmStatic
    fun hide(view: View?) {
        try {
            view?.let {
                val inputMethodManager = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

