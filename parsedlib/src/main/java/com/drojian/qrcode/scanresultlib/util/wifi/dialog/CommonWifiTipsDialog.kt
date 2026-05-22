package com.drojian.qrcode.scanresultlib.util.wifi.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentManager
import com.drojian.qrcode.baselib.QRLibUtil
import com.drojian.qrcode.scanresultlib.R
import com.drojian.qrcode.utillib.extension.hideNavigationBars

/**
 * Create by yangfengfan on 2020-11-06
 *
 * Wifi连接引导弹窗
 */
class CommonWifiTipsDialog : BaseDialogFragment() {

    private var alertParams: WiFiDialogParams? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.layout_dialog_wifi_connect_tips, container)
        try {
            alertParams?.let { alertParams ->
                isCancelable = alertParams.cancelable
                dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                val window = dialog?.window
                val lp = window?.attributes
                lp?.gravity = Gravity.BOTTOM
                lp?.width = WindowManager.LayoutParams.MATCH_PARENT
                window?.attributes = lp

                val title = root.findViewById<TextView>(R.id.tv_title)
                val subTitle = root.findViewById<TextView>(R.id.tv_sub_title)
                val step1 = root.findViewById<TextView>(R.id.tv_step_1)
                val step2 = root.findViewById<TextView>(R.id.tv_step_2)
                val step3 = root.findViewById<TextView>(R.id.tv_step_3)
                val step1Hint = root.findViewById<TextView>(R.id.tv_step_1_hint)
                val step2Hint = root.findViewById<TextView>(R.id.tv_step_2_hint)
                val step3Hint = root.findViewById<TextView>(R.id.tv_step_3_hint)

                title.text = alertParams.title
                alertParams.subTitle?.let {
                    subTitle.visibility = View.VISIBLE
                    subTitle.text = textBold(root.context, it, "<b>", "</b>")
                }
                alertParams.step1Hint?.let {
                    step1.text = "1"
                    step2.text = "2"
                    step3.text = "3"
                    step1Hint.text = it
                    step1.visibility = View.VISIBLE
                    step1Hint.visibility = View.VISIBLE
                }

                if (!alertParams.noPassword) {
                    step3.visibility = View.VISIBLE
                    step3Hint.visibility = View.VISIBLE
                }
                root.findViewById<View>(R.id.tv_open_setting).setOnClickListener {
                    alertParams.wifiDialogListener?.onPositiveClick()
                    goWiFiSetting(context)
                    dismissMe()
                }

                root.findViewById<View>(R.id.iv_close).setOnClickListener {
                    alertParams.wifiDialogListener?.onNegativeClick()
                    dismissMe()
                }
                alertParams.wifiDialogListener?.onDialogShow()

                runCatching {
                    dialog?.apply {
                        WindowCompat.setDecorFitsSystemWindows(window!!, false)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android10以上，导航栏跟弹窗颜色一致
                            window.setNavigationBarContrastEnforced(false)
                            window.navigationBarColor = alertParams.navigationBarColor
                            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = !alertParams.isDark
                            if (QRLibUtil.shouldHideDialogNavigationBar) {
                                window.hideNavigationBars()
                            }
                        } else {  // Android10以下，导航栏固定黑色
                            window.navigationBarColor = Color.BLACK
                            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = false
                            if (QRLibUtil.shouldHideDialogNavigationBar) {
                                window.hideNavigationBars()
                            }
                        }
                    }

                    ViewCompat.setOnApplyWindowInsetsListener(root.findViewById(R.id.main)) { v, insets ->
                        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                        v.setPaddingRelative(v.paddingStart, v.paddingTop, v.paddingRight, systemBars.bottom)
                        insets
                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return root
    }

    private fun textBold(context: Context, sources: CharSequence, start: String?, end: String?): SpannableString {
        try {
            var str = sources.toString()
            var startIndex = str.indexOf(start!!)
            str = str.replace(start, "")
            var endIndex = str.indexOf(end!!)
            str = str.replace(end, "")
            if (startIndex > endIndex) {
                startIndex += endIndex
                endIndex = startIndex - endIndex
                startIndex -= endIndex
            }
            val spannableString = SpannableString(str)
            spannableString.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.dialog_wifi_bold)),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spannableString
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sources as SpannableString
    }


    override fun onStart() {
        super.onStart()
        runCatching {
            dialog?.apply {
                WindowCompat.setDecorFitsSystemWindows(window!!, false)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android10以上，导航栏跟弹窗颜色一致
                    window?.setNavigationBarContrastEnforced(false)
                    window?.navigationBarColor = alertParams?.navigationBarColor!!
                    WindowInsetsControllerCompat(window!!, window!!.decorView).isAppearanceLightNavigationBars = !alertParams!!.isDark
                } else {  // Android10以下，导航栏固定黑色
                    window!!.navigationBarColor = Color.BLACK
                    WindowInsetsControllerCompat(window!!, window!!.decorView).isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    class Builder(private val alertParams: WiFiDialogParams = WiFiDialogParams()) {

        fun show(fragmentManager: FragmentManager): CommonWifiTipsDialog {
            val dialog = CommonWifiTipsDialog()
            dialog.alertParams = alertParams
            dialog.show(fragmentManager)
            return dialog
        }

    }

    class WiFiDialogParams {
        var title: CharSequence = ""
        var subTitle: CharSequence? = null
        var step1Hint: CharSequence? = null
        var step2Hint: CharSequence? = "Choose the Wi-Fi to connect"
        var step3Hint: CharSequence? = "Paste the password (auto copied)"
        var wifiDialogListener: WifiDialogListener? = null
        var cancelable = false

        var noPassword = false


        var navigationBarColor: Int = Color.WHITE
        var isDark = false
    }

    private fun goWiFiSetting(context: Context?) {
        try {
            context?.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}