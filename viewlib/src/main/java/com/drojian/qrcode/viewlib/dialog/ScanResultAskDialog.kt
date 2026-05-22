package com.drojian.qrcode.viewlib.dialog

import android.app.Activity
import android.app.Dialog
import android.view.View
import com.drojian.qrcode.baselib.BaseDialog
import com.drojian.qrcode.utillib.listener.DialogListener
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.viewlib.R

object ScanResultAskDialog {

    @JvmStatic
    fun show(activity: Activity?, navigationBarColor: Int, isDark: Boolean = false, listener: DialogListener? = null): Dialog? {
        try {
            activity?.let {
                val dialog = BaseDialog.getBottomDialog(it, R.layout.layout_dialog_result_ask, navigationBarColor, isDark)

                dialog.findViewById<View>(R.id.tv_positive).setOnClickListener {
                    listener?.onPositive()
                    dialog.dismiss()
                }
                dialog.findViewById<View>(R.id.tv_negative).setOnClickListener {
                    listener?.onNegative()
                    dialog.dismiss()
                }
                dialog.setOnDismissListener {
                    listener?.onDisMiss()
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