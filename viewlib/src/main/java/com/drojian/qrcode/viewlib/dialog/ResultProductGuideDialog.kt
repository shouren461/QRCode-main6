package com.drojian.qrcode.viewlib.dialog

import android.app.Activity
import android.app.Dialog
import android.view.View
import com.drojian.qrcode.baselib.BaseDialog
import com.drojian.qrcode.utillib.listener.DialogListener
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.viewlib.R

/**
 * 相机扫描引导弹窗
 */
object ResultProductGuideDialog {

    @JvmStatic
    fun show(activity: Activity?, listener: DialogListener? = null, navigationBarColor: Int, isDark: Boolean = false): Dialog? {
        try {
            activity?.let {
                val dialog = BaseDialog.getBottomDialog(it, R.layout.layout_dialog_result_product_guide, navigationBarColor, isDark)

                dialog.findViewById<View>(R.id.tv_positive).setOnClickListener {
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