package com.drojian.qrcode.scanresultlib.webview

import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.drojian.qrcode.scanresultlib.R
import com.drojian.qrcode.utillib.listener.SingleListener

object WebViewMoreDialog {

    @JvmStatic
    fun show(anchor: View, listener: SingleListener<Int>?, isProduct: Boolean = false) {
        try {
            val view: View  =  if (isProduct) {
                LayoutInflater.from(anchor.context).inflate(R.layout.layout_dialog_web_view_more_profuct, null, false)
            } else {
                LayoutInflater.from(anchor.context).inflate(R.layout.layout_dialog_web_view_more_url, null, false)
            }

            val popupWindow = PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)

            view.findViewById<View>(R.id.view_copy_link)?.setOnClickListener {
                listener?.onCallBack(it.id)
                popupWindow.dismiss()
            }

            view.findViewById<View>(R.id.view_copy_content)?.setOnClickListener {
                listener?.onCallBack(it.id)
                popupWindow.dismiss()
            }

            view.findViewById<View>(R.id.view_share)?.setOnClickListener {
                listener?.onCallBack(it.id)
                popupWindow.dismiss()
            }

            view.findViewById<View>(R.id.view_faq)?.setOnClickListener {
                listener?.onCallBack(it.id)
                popupWindow.dismiss()
            }

            popupWindow.isOutsideTouchable = true
            popupWindow.setBackgroundDrawable(ColorDrawable(0x00))
            popupWindow.showAsDropDown(anchor, 0, 0, Gravity.END)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}