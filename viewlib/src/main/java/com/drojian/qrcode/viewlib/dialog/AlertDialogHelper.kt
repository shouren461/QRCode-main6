package com.drojian.qrcode.viewlib.dialog

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.utillib.utils.AppUtil
import com.drojian.qrcode.viewlib.R

object AlertDialogHelper {

    @JvmStatic
    fun showActivityNotFound(context: Context?) {
        try {
            context?.let {
                AlertDialog.Builder(it).setMessage(R.string.msg_intent_failed).setPositiveButton(R.string.action_ok, null).show()
            }
        } catch (e: Exception) {
            e.log()
        }
    }

    @JvmStatic
    fun showIAPError(context: Context?) {
        try {
            context?.let {
                AlertDialog.Builder(it).setTitle(R.string.purchased_failed_title).setMessage(R.string.purchased_failed).setPositiveButton(R.string.action_ok, null).show()
            }
        } catch (e: Exception) {
            e.log()
        }
    }

    @JvmStatic
    fun showCameraError(activity: Activity?) {
        try {
            activity?.let {
                AlertDialog.Builder(it).setMessage(R.string.camera_problem_restart)
                    .setPositiveButton(R.string.action_ok) { dialog, _ ->
                        dialog.dismiss()
                        AppUtil.finishApp(activity)
                    }
                    .setOnCancelListener { dialog ->
                        dialog.dismiss()
                        AppUtil.finishApp(activity)
                    }.show()
            }
        } catch (e: Exception) {
            e.log()
        }
    }

}