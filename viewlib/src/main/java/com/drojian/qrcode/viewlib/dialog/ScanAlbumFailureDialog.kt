package com.drojian.qrcode.viewlib.dialog

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.widget.TextView
import com.drojian.qrcode.baselib.BaseDialog
import com.drojian.qrcode.utillib.listener.DialogListener
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.viewlib.R


/**
 * 相册扫描失败弹窗弹窗
 */
object ScanAlbumFailureDialog {

    @JvmStatic
    fun show(activity: Activity?, listener: DialogListener? = null, isBottom: Boolean = false, navigationBarColor: Int, isDark: Boolean = false): Dialog? {
        try {
            activity?.let {
                val dialog = if (isBottom) {
                    BaseDialog.getBottomDialog(it, R.layout.layout_dialog_scan_album_failure, navigationBarColor, isDark)
                } else {
                    BaseDialog.getDialog(it, R.layout.layout_dialog_scan_album_failure)
                }

                dialog.findViewById<View>(R.id.tv_positive)?.setOnClickListener {
                    listener?.onPositive()
                    dialog.dismiss()
                }

                dialog.findViewById<TextView>(R.id.tv_explain)?.setOnClickListener {
                    listener?.onPositive()
                    dialog.dismiss()
                }

                dialog.findViewById<TextView>(R.id.tv_negative)?.setOnClickListener {
                    listener?.onNegative()
                    dialog.dismiss()
                }

                dialog.show()
                return dialog
            }
        } catch (e: Exception) {
            e.log()
        }
        return null
    }
}