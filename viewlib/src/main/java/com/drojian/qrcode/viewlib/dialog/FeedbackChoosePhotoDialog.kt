package com.drojian.qrcode.viewlib.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import com.drojian.qrcode.baselib.BaseDialog
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.viewlib.R

/**
 * 选择文件弹窗
 */
object FeedbackChoosePhotoDialog {

    @JvmStatic
    fun show(context: Context?, navigationBarColor: Int, isDark: Boolean = false, listener: ChoosePhotoListener? = null): Dialog? {
        try {
            context?.let {
                val bottomSheetDialog = BaseDialog.getBottomDialog(context, R.layout.layout_dialog_feedback_choose_photo, navigationBarColor, isDark)

                bottomSheetDialog.findViewById<View>(R.id.tv_capture).setOnClickListener {
                    listener?.onClickCamera()
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.findViewById<View>(R.id.tv_gallery).setOnClickListener {
                    listener?.onClickGallery()
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.findViewById<View>(R.id.tv_cancel).setOnClickListener {
                    bottomSheetDialog.dismiss()
                }

                bottomSheetDialog.show()
                return bottomSheetDialog
            }
        } catch (e: Exception) {
            e.log()
        }
        return null
    }

    interface ChoosePhotoListener {
        fun onClickCamera()
        fun onClickGallery()
    }
}