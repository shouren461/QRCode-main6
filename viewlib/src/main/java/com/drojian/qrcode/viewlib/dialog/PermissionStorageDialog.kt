package com.drojian.qrcode.viewlib.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.TextView
import com.drojian.qrcode.baselib.BaseDialog
import com.drojian.qrcode.utillib.listener.DialogListener
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.utillib.utils.AppUtil
import com.drojian.qrcode.viewlib.R

object PermissionStorageDialog {

    @JvmStatic
    fun showOnDeny(context: Context?, isReadGallery: Boolean = false, canceledOnTouchOutside: Boolean = false, listener: DialogListener? = null): Dialog? {
        try {
            context?.let {
                val dialog = BaseDialog.getDialog(context, R.layout.layout_dialog_permission_deny)
                return showDeny(context, dialog, isReadGallery, canceledOnTouchOutside, listener)
            }
        } catch (e: Exception) {
            e.log()
        }
        return null
    }


    @JvmStatic
    fun showOnDenyBottom(context: Context?, isReadGallery: Boolean = false, canceledOnTouchOutside: Boolean = false, navigationBarColor: Int, isDark: Boolean = false, listener: DialogListener? = null): Dialog? {
        try {
            context?.let {
                val dialog = BaseDialog.getBottomDialog(context, R.layout.layout_dialog_permission_deny, navigationBarColor, isDark)
                return showDeny(context, dialog, isReadGallery, canceledOnTouchOutside, listener)
            }
        } catch (e: Exception) {
            e.log()
        }
        return null
    }


    private fun showDeny(context: Context?, dialog: Dialog, isReadGallery: Boolean = false, canceledOnTouchOutside: Boolean = false, listener: DialogListener? = null): Dialog? {
        try {
            context?.let {
                dialog.setCanceledOnTouchOutside(canceledOnTouchOutside)
                if (isReadGallery) {
                    dialog.findViewById<TextView>(R.id.tv_explain)?.text = it.getString(R.string.album_permission_hint)
                } else {
                    dialog.findViewById<TextView>(R.id.tv_explain)?.text = it.getString(R.string.storage_permission_save_images)
                }

                dialog.findViewById<View>(R.id.tv_positive)?.setOnClickListener {
                    listener?.onPositive()
                    dialog.dismiss()
                }
                dialog.findViewById<View>(R.id.tv_negative)?.setOnClickListener {
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

    /**
     * =======================================================================================================================================================================================================================================================
     * =======================================================================================================================================================================================================================================================
     * =======================================================================================================================================================================================================================================================
     * =======================================================================================================================================================================================================================================================
     * =======================================================================================================================================================================================================================================================
     */
    @JvmStatic
    fun showOnForbidden(context: Context?, isReadGallery: Boolean = false, canceledOnTouchOutside: Boolean = false, listener: DialogListener? = null): Dialog? {
        try {
            context?.let {
                val dialog = BaseDialog.getDialog(context, R.layout.layout_dialog_permission_forbidden)
                return showForbidden(context, dialog, isReadGallery, canceledOnTouchOutside, listener)
            }
        } catch (e: Exception) {
            e.log()
        }
        return null
    }


    @JvmStatic
    fun showOnForbiddenBottom(context: Context?, isReadGallery: Boolean = false, canceledOnTouchOutside: Boolean = false, navigationBarColor: Int, isDark: Boolean = false, listener: DialogListener? = null): Dialog? {
        try {
            context?.let {
                val dialog = BaseDialog.getBottomDialog(context, R.layout.layout_dialog_permission_forbidden, navigationBarColor, isDark)
                return showForbidden(context, dialog, isReadGallery, canceledOnTouchOutside, listener)
            }
        } catch (e: Exception) {
            e.log()
        }
        return null
    }


    private fun showForbidden(context: Context?, dialog: Dialog, isReadGallery: Boolean = false, canceledOnTouchOutside: Boolean = false, listener: DialogListener? = null): Dialog? {
        try {
            context?.let {
                dialog.setCanceledOnTouchOutside(canceledOnTouchOutside)

                if (isReadGallery) {
                    dialog.findViewById<TextView>(R.id.tv_explain)?.text = it.getString(R.string.album_permission_hint)
                } else {
                    dialog.findViewById<TextView>(R.id.tv_explain)?.text = it.getString(R.string.storage_permission_save_images)
                }

                dialog.findViewById<TextView>(R.id.tv_step_3)?.text = it.getString(R.string.required_permission_turn_on_storage)

                dialog.findViewById<View>(R.id.tv_positive)?.setOnClickListener {
                    listener?.onPositive()
                    dialog.dismiss()
                    AppUtil.toAppSetting(context)
                }
                dialog.findViewById<View>(R.id.tv_negative)?.setOnClickListener {
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