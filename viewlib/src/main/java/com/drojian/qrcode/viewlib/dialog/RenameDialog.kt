package com.drojian.qrcode.viewlib.dialog

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.widget.EditText
import com.drojian.qrcode.baselib.BaseDialog
import com.drojian.qrcode.utillib.extension.clearValueOnEndDrawableClick
import com.drojian.qrcode.utillib.extension.cursorToEnd
import com.drojian.qrcode.utillib.extension.getEditTextString
import com.drojian.qrcode.utillib.listener.SingleListener
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.utillib.utils.SoftInputUtil
import com.drojian.qrcode.viewlib.R
import com.drojian.qrcode.viewlib.toast.ToastUtil


object RenameDialog {

    @JvmStatic
    fun show(activity: Activity?, defaultValue: String = "", listener: SingleListener<String>? = null): Dialog? {
        try {
            activity?.let {
                val dialog = BaseDialog.getDialog(it, R.layout.layout_dialog_rename)
                val editText = dialog.findViewById<EditText>(R.id.et_rename)
                editText?.clearValueOnEndDrawableClick()
                editText?.setText(defaultValue)
                editText?.cursorToEnd()
                dialog.findViewById<View>(R.id.tv_positive)?.setOnClickListener {
                    if (getEditTextString(editText).isBlank()) {
                        ToastUtil.show(activity, activity.getString(R.string.toast_text_null))
                    } else {
                        listener?.onCallBack(getEditTextString(editText))
                        dialog.dismiss()
                    }
                }
                dialog.findViewById<View>(R.id.tv_negative)?.setOnClickListener {
                    dialog.dismiss()
                }
                SoftInputUtil.show(editText)
                dialog.show()
                return dialog
            }
        } catch (e: Exception) {
            e.log()
        }
        return null
    }

}