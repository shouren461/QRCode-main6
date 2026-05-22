package com.drojian.qrcode.utillib.extension

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun EditText.getTextString(): String = this.text.toString()

fun getEditTextString(editText: EditText?) = if (editText == null || editText.text.isNullOrEmpty()) {
    ""
} else {
    editText.text.toString()
}

fun EditText.cursorToEnd() {
    try {
        this.setSelection(this.text.length)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun EditText.selectContent() {
    try {
        this.postDelayed({
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
            val obj = this.context.getSystemService(Context.INPUT_METHOD_SERVICE)
            if (obj is InputMethodManager) {
                obj.showSoftInput(this, 0)
            }
            if (this.text != null && this.text.toString().isNotEmpty()) {
                this.setSelection(0, this.text.length)
            }
        }, 120)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun EditText.get11() : String{
    return text.toString()
}