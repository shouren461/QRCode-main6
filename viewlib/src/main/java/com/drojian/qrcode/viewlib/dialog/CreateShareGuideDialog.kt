package com.drojian.qrcode.viewlib.dialog

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.widget.TextView
import com.drojian.qrcode.baselib.BaseDialog
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.viewlib.R

object CreateShareGuideDialog {

    @JvmStatic
    fun show(activity: Activity?, navigationBarColor: Int, isDark: Boolean = false): Dialog? {
        activity?.let {
            try {
                val dialog = BaseDialog.getBottomDialog(it, R.layout.layout_dialog_create_share_guide, navigationBarColor, isDark)
                dialog.findViewById<View>(R.id.tv_positive).setOnClickListener {
                    dialog.dismiss()
                }
                val step2TV: TextView = dialog.findViewById(R.id.tv_step_2)
                step2TV.text = activity.getString(R.string.then_select_generate, activity.getString(R.string.generate_qr_code))

                dialog.show()
                return dialog
            } catch (e: Exception) {
                e.log()
            }
        }
        return null
    }
}