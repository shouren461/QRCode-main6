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

object PermissionCameraDialog {

    @JvmStatic
    fun showOnDenyBottom(context: Context?, isCanceledOutSide: Boolean = false, navigationBarColor: Int, isDark: Boolean = false, listener: DialogListener? = null): Dialog? {
        try {
            context?.let {
                val dialog = BaseDialog.getBottomDialog(context, R.layout.layout_dialog_permission_deny, navigationBarColor, isDark)
                return showDeny(context, dialog, isCanceledOutSide, listener)
            }
        } catch (e: Exception) {
            e.log()
        }
        return null
    }

    @JvmStatic
    fun showOnDeny(context: Context?, isCanceledOutSide: Boolean = false, listener: DialogListener? = null): Dialog? {
        try {
            context?.let {
                val dialog = BaseDialog.getDialog(context, R.layout.layout_dialog_permission_deny)
                return showDeny(context, dialog, isCanceledOutSide, listener)
            }
        } catch (e: Exception) {
            e.log()
        }
        return null
    }

    private fun showDeny(context: Context?, dialog: Dialog, isCanceledOutSide: Boolean = false, listener: DialogListener? = null): Dialog? {
        try {
            context?.let {
                dialog.setCanceledOnTouchOutside(isCanceledOutSide)
                dialog.findViewById<TextView>(R.id.tv_explain)?.text = it.getString(R.string.camera_permission_des)
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
    fun showOnForbidden(context: Context?, isCanceledOutSide: Boolean = false, listener: DialogListener? = null): Dialog? {
        try {
            context?.let {
                val dialog = BaseDialog.getDialog(context, R.layout.layout_dialog_permission_forbidden)
                return showForbidden(context, dialog, isCanceledOutSide, listener)
            }
        } catch (e: Exception) {
            e.log()
        }
        return null
    }

    @JvmStatic
    fun showOnForbiddenBottom(context: Context?, isCanceledOutSide: Boolean = false, navigationBarColor: Int, isDark: Boolean = false, listener: DialogListener? = null): Dialog? {
        try {
            context?.let {
                val dialog = BaseDialog.getBottomDialog(context, R.layout.layout_dialog_permission_forbidden, navigationBarColor, isDark)
                return showForbidden(context, dialog, isCanceledOutSide, listener)
            }
        } catch (e: Exception) {
            e.log()
        }
        return null
    }

    private fun showForbidden(context: Context?, dialog: Dialog, isCanceledOutSide: Boolean = false, listener: DialogListener? = null): Dialog? {
        try {
            context?.let {
                dialog.setCanceledOnTouchOutside(isCanceledOutSide)

                dialog.findViewById<TextView>(R.id.tv_explain)?.text = it.getString(R.string.camera_permission_des)
                dialog.findViewById<TextView>(R.id.tv_step_3)?.text = it.getString(R.string.required_permission_turn_on_camera)

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