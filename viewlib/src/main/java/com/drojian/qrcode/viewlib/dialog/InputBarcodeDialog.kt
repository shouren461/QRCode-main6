package com.drojian.qrcode.viewlib.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.drojian.qrcode.baselib.BaseDialog
import com.drojian.qrcode.utillib.extension.clearValueOnEndDrawableClick
import com.drojian.qrcode.utillib.listener.DialogListener
import com.drojian.qrcode.utillib.listener.DialogListenerWithResult
import com.drojian.qrcode.utillib.listener.setOneClickListener
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.utillib.utils.SoftInputUtil
import com.drojian.qrcode.viewlib.R

object InputBarcodeDialog {

    @JvmStatic
    fun show(context: Context, dialogListener: DialogListenerWithResult? = null): Dialog? {
        try {
            val dialog = BaseDialog.getDialog(context, R.layout.layout_dialog_input_barcode)
            val inputET = dialog.findViewById<EditText>(R.id.et_input)
            val alertTV = dialog.findViewById<TextView>(R.id.tv_alert)
            val lineTV = dialog.findViewById<TextView>(R.id.tv_line)
            inputET.clearValueOnEndDrawableClick()

            dialog.findViewById<View>(R.id.tv_positive).setOneClickListener {
                val inputValue = inputET.text.toString()
                if (!TextUtils.isEmpty(inputValue)) {
                    SoftInputUtil.hide(inputET)
                    dialogListener?.onPositive(inputValue)
                    dialog.dismiss()
                } else {
                    alertTV.visibility = View.VISIBLE
                    lineTV.setBackgroundColor(Color.parseColor("#EF5454"))
                }
            }

            dialog.findViewById<View>(R.id.tv_negative).setOneClickListener {
                dialogListener?.onNegative()
                dialog.dismiss()
            }

            dialog.setOnDismissListener {
                dialogListener?.onDisMiss()
            }

            inputET.post {
                SoftInputUtil.show(inputET)
            }

            inputET.doAfterTextChanged {
                if (!it.isNullOrBlank()) {
                    alertTV.visibility = View.INVISIBLE
                    context.let { con -> ContextCompat.getColor(con, R.color.themeColor) }
                        .let { color -> lineTV.setBackgroundColor(color) }
                }
            }

            dialog.show()
            return dialog
        } catch (e: Exception) {
            e.log()
        }
        return null
    }

    @JvmStatic
    fun showGuide(context: Context, navigationBarColor: Int, isDark: Boolean = false, dialogListener: DialogListener? = null): Dialog? {
        try {
            val dialog = BaseDialog.getBottomDialog(context, R.layout.layout_dialog_input_barcode_guide, navigationBarColor, isDark)

            dialog.findViewById<View>(R.id.tv_positive).setOneClickListener {
                dialogListener?.onPositive()
                dialog.dismiss()
            }

            dialog.findViewById<View>(R.id.tv_negative).setOneClickListener {
                dialogListener?.onNegative()
                dialog.dismiss()
            }

            dialog.setOnDismissListener {
                dialogListener?.onDisMiss()
            }

            dialog.show()
            return dialog
        } catch (e: Exception) {
            e.log()
        }
        return null
    }
}