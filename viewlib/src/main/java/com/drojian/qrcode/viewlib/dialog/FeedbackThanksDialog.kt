package com.drojian.qrcode.viewlib.dialog

import android.app.Activity
import android.app.Dialog
import android.view.View
import com.drojian.qrcode.baselib.BaseDialog
import com.drojian.qrcode.utillib.listener.DialogListener
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.viewlib.R


/**
 * 感谢反馈弹窗
 */
object FeedbackThanksDialog {

    @JvmStatic
    fun show(activity: Activity, navigationBarColor: Int, isDark: Boolean = false, listener: DialogListener? = null): Dialog? {
        try {
            val bottomSheetDialog = BaseDialog.getBottomDialog(activity, R.layout.layout_dialog_feedback_thanks, navigationBarColor, isDark)

            bottomSheetDialog.findViewById<View>(R.id.tv_positive).setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.setOnDismissListener {
                listener?.onDisMiss()
            }

            bottomSheetDialog.show()
            return bottomSheetDialog
        } catch (e: Exception) {
            e.log()
        }
        return null
    }

}
