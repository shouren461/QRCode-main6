package com.drojian.qrcode.utillib.extension

import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.drojian.qrcode.baselib.R
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.utillib.utils.RtlUtil

fun TextView.clearValueOnEndDrawableClick() {
    try {
        if (text.isNullOrBlank()) {
            setCompoundDrawables(null, null, null, null)
        } else {
            if (RtlUtil.isRtl(context)) {
                setCompoundDrawablesWithIntrinsicBounds(getDrawable(context, R.drawable.ic_close), null, null, null)
            } else {
                setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(context, R.drawable.ic_close), null)
            }
        }
        setOnTouchListener(View.OnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                try {
                    val isClickClear = if (RtlUtil.isRtl(context)) {
                        event.rawX >= left - compoundDrawables[0].bounds.width()
                    } else {
                        event.rawX >= right - compoundDrawables[2].bounds.width()
                    }
                    if (isClickClear) {
                        text = ""
                        return@OnTouchListener true
                    }
                } catch (e: Exception) {
                    e.log()
                }
            }
            false
        })
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrBlank()) {
                    setCompoundDrawables(null, null, null, null)
                } else {
                    if (RtlUtil.isRtl(context)) {
                        setCompoundDrawablesWithIntrinsicBounds(getDrawable(context, R.drawable.ic_close), null, null, null)
                    } else {
                        setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(context, R.drawable.ic_close), null)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

    } catch (e: Exception) {
        e.log()
    }
}