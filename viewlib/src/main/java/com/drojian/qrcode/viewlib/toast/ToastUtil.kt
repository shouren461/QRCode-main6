package com.drojian.qrcode.viewlib.toast

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import com.drojian.qrcode.viewlib.R

object ToastUtil {
    private var mToast: Toast? = null

    @JvmStatic
    fun show(context: Context, text: CharSequence) {
        show(context, R.layout.layout_toast, text)
    }

    @JvmStatic
    fun show(context: Context, @LayoutRes layout: Int, text: CharSequence) {
        try {
            updateToast(context, layout, text, Toast.LENGTH_SHORT)
            mToast?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateToast(context: Context, layout: Int, text: CharSequence, duration: Int) {
        val view = LayoutInflater.from(context).inflate(layout, null)
        try {
            val textView = view.findViewById<TextView>(R.id.textView)
            textView.text = text
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (mToast == null) {
            mToast = Toast(context)
        }
        mToast?.setDuration(duration)
        mToast?.view = view
        mToast?.setGravity(Gravity.TOP, 0, context.resources.getDimensionPixelSize(R.dimen.qr_dp_120))
    }
}