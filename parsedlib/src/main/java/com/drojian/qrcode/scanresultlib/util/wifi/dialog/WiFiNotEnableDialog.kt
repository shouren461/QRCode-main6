package com.drojian.qrcode.scanresultlib.util.wifi.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentManager
import com.drojian.qrcode.baselib.QRLibUtil
import com.drojian.qrcode.scanresultlib.R
import com.drojian.qrcode.utillib.extension.hideNavigationBars

/**
 * Wifi连接引导弹窗
 */
class WiFiNotEnableDialog private constructor(private val wifiDialogListener: WifiDialogListener?) : BaseDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.layout_dialog_wifi_not_enable, container)
        try {
            isCancelable = false
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

            root.findViewById<View>(R.id.tv_positive).setOnClickListener {
                wifiDialogListener?.onPositiveClick()
                goWiFiSetting(context)
                dismissMe()
            }

            root.findViewById<View>(R.id.tv_negative).setOnClickListener {
                wifiDialogListener?.onNegativeClick()
                dismissMe()
            }

            context?.let {
                setWidth(((it.resources.displayMetrics.widthPixels) * 0.8).toInt())
            }
            if (QRLibUtil.shouldHideDialogNavigationBar) {
                dialog?.window?.hideNavigationBars()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return root
    }

    class Builder(private val wifiDialogListener: WifiDialogListener?) {

        fun show(fragmentManager: FragmentManager): WiFiNotEnableDialog {
            val dialog = WiFiNotEnableDialog(wifiDialogListener)
            dialog.show(fragmentManager)
            return dialog
        }
    }

    private fun goWiFiSetting(context: Context?) {
        try {
            context?.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}