package com.drojian.qrcode.scanresultlib.webview

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.drojian.qrcode.baselib.BaseDialog
import com.drojian.qrcode.scanresultlib.R
import com.drojian.qrcode.utillib.log.LogHelper.log

object OpenAppTipsDialog {

    @JvmStatic
    fun show(activity: Activity, navigationBarColor: Int, isDark: Boolean = false, listener: DialogListener? = null, openAppEnum: OpenAppEnum): Dialog? {
        var dialog: Dialog? = null
        try {
            dialog = BaseDialog.getBottomDialog(activity, R.layout.layout_dialog_web_view_open_app, navigationBarColor, isDark, theme = R.style.WebViewDialogTheme)
            listener?.onDraw(dialog.window?.decorView)

            val iconIV: ImageView? = dialog.findViewById(R.id.iv_logo)
            val tvDes: TextView? = dialog.findViewById(R.id.tv_des)
            when (openAppEnum) {
                OpenAppEnum.Line -> R.drawable.svg_norm_line
                OpenAppEnum.WhatsApp -> R.drawable.svg_whatsapp
                OpenAppEnum.Instagram -> R.drawable.png_ins
                OpenAppEnum.Facebook -> R.drawable.png_facebook
                OpenAppEnum.X -> R.drawable.svg_twitter
                OpenAppEnum.Youtube -> R.drawable.svg_youtube
                OpenAppEnum.GoogleDoc -> R.drawable.lib_result_png_google_doc
                OpenAppEnum.MicrosoftOffice -> R.drawable.lib_result_png_office
                OpenAppEnum.Viber -> R.drawable.svg_viber
                OpenAppEnum.Paypal -> R.drawable.png_paypal
                OpenAppEnum.Spotify -> R.drawable.svg_spotify
                OpenAppEnum.GooglePlay -> R.drawable.svg_google_play
            }.let {
                iconIV?.setImageResource(it)
            }
            tvDes?.text = activity.getString(R.string.x_link_open, openAppEnum.name)
            dialog.findViewById<View>(R.id.tv_negative)?.setOnClickListener {
                dialog.dismiss()
                listener?.onNegative()
            }

            dialog.findViewById<View>(R.id.tv_positive)?.setOnClickListener {
                dialog.dismiss()
                listener?.onPositive()
            }

            dialog.show()
        } catch (e: Exception) {
            e.log()
        }
        return dialog
    }

    interface DialogListener {
        fun onPositive()
        fun onNegative() {}
        fun onDraw(view: View?)
    }

    enum class OpenAppEnum {
        Line,
        WhatsApp,
        Instagram,
        Facebook,
        GoogleDoc,
        MicrosoftOffice,
        X,
        Youtube,
        Viber,
        Paypal,
        Spotify,
        GooglePlay
    }
}