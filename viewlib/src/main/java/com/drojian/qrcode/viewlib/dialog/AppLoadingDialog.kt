package com.drojian.qrcode.viewlib.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.ViewGroup
import com.drojian.qrcode.baselib.BaseDialog
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.viewlib.R

object AppLoadingDialog {

    @JvmStatic
    fun create(context: Context?, cancelListener: DialogInterface.OnCancelListener): Dialog? {
        try {
            context?.let {
                val progressDialog = BaseDialog.getDialog(it, R.layout.layout_dialog_app_loading, R.style.App_Dialog_Loading)
                progressDialog.setOnCancelListener(cancelListener)
                progressDialog.setCancelable(false)
                progressDialog.window?.attributes?.let {
                    it.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    it.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    it.gravity = Gravity.CENTER
                }
                return progressDialog
            }
        } catch (e: Exception) {
            e.log()
        }
        return null
    }
}