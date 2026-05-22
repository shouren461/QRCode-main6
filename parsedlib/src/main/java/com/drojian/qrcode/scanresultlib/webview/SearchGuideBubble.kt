package com.drojian.qrcode.scanresultlib.webview

import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.drojian.qrcode.scanresultlib.R
import com.drojian.qrcode.utillib.utils.SPUtil

object SearchGuideBubble {

    fun show(anchor: View, topMaskView:View?, bottomMaskView: View?) {
        try {
            if (SPUtil.getInstance().get("lib_result_web_view_is_show_search_bubble", false)) {
                return
            }
            val view: View = LayoutInflater.from(anchor.context).inflate(R.layout.layout_dialog_web_view_search_bubble, null, false)
            val popupWindow = PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
            popupWindow.isOutsideTouchable = false
            popupWindow.setBackgroundDrawable(ColorDrawable(0x00))
            topMaskView?.visibility = View.VISIBLE
            bottomMaskView?.visibility = View.VISIBLE
            popupWindow.showAsDropDown(anchor, 0, 0, Gravity.CENTER)

            anchor.postDelayed({
                try {
                    topMaskView?.visibility = View.GONE
                    bottomMaskView?.visibility = View.GONE
                    popupWindow.dismiss()
                } catch (e: Exception) {

                }
            }, 2000)
            SPUtil.getInstance().set("lib_result_web_view_is_show_search_bubble", true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}