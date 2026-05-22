package com.drojian.qrcode.viewlib.dialog

import android.app.Activity
import android.app.Dialog
import android.text.Html
import android.view.View
import android.widget.TextView
import com.drojian.qrcode.baselib.BaseDialog
import com.drojian.qrcode.utillib.listener.DialogListener
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.viewlib.R

/**
 * 相机扫描引导弹窗
 */
object ScanCameraGuideDialog {

    @JvmStatic
    fun show(activity: Activity?,  navigationBarColor: Int, isDark: Boolean = false, listener: DialogListener? = null): Dialog? {
        try {
            activity?.let {
                val dialog = BaseDialog.getBottomDialog(it, R.layout.layout_dialog_scan_guide_camera, navigationBarColor, isDark)

                val tv1 = dialog.findViewById<TextView>(R.id.tv_subtitle_1)
                val tv2 = dialog.findViewById<TextView>(R.id.tv_subtitle_2)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    tv1.text = Html.fromHtml(activity.getString(R.string.avoid_reflections), Html.FROM_HTML_MODE_LEGACY)
                    tv2.text = Html.fromHtml(activity.getString(R.string.make_phone_face), Html.FROM_HTML_MODE_LEGACY)
                } else {
                    tv1.text = Html.fromHtml(activity.getString(R.string.avoid_reflections))
                    tv2.text = Html.fromHtml(activity.getString(R.string.make_phone_face))
                }

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