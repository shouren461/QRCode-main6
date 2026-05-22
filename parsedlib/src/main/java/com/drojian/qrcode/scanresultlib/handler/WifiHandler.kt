package com.drojian.qrcode.scanresultlib.handler

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.wifi.WifiManager
import android.os.AsyncTask
import com.drojian.qrcode.scanlib.scan.parse.format.ParseWifiModel
import com.drojian.qrcode.scanresultlib.BaseResultHandler
import com.drojian.qrcode.scanresultlib.R
import com.drojian.qrcode.scanresultlib.ResultHandlerConfig
import com.drojian.qrcode.scanresultlib.button.ParseAction
import com.drojian.qrcode.scanresultlib.util.wifi.WifiConfigManager
import com.drojian.qrcode.scanresultlib.util.wifi.dialog.WifiDialogListener

/**
 * @author yangfengfan 2020-10-19
 */
class WifiHandler(activity: Activity, val parsedModel: ParseWifiModel, resultHandlerConfig: ResultHandlerConfig) :
    BaseResultHandler(activity, parsedModel, resultHandlerConfig) {

    override val parseActionList: Array<ParseAction> = if (parsedModel.networkEncryption == "nopass" || parsedModel.password.isNullOrBlank()) {
        arrayOf(
            ParseAction.CONNECT_TO_NETWORK,
            ParseAction.COPY,
            ParseAction.SHARE,
        )
    } else {
        arrayOf(
            ParseAction.CONNECT_TO_NETWORK,
            ParseAction.COPY_PASSWORD,
            ParseAction.COPY,
            ParseAction.SHARE,
        )
    }
    var wifiDialogListener: WifiDialogListener? = null


    override fun handleButtonPress(action: ParseAction) {
        when (action) {
            ParseAction.CONNECT_TO_NETWORK -> {
                try {
                    val wifiManager = activity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    wifiManager.let {
                        WifiConfigManager(it, activity).apply {
                            this.wifiDialogListener = this@WifiHandler.wifiDialogListener
                        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, parsedModel)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            ParseAction.COPY_PASSWORD -> {
                try {
                    val cm = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val mClipData = ClipData.newPlainText("Label", parsedModel.password)
                    cm.setPrimaryClip(mClipData)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }

            else -> super.handleButtonPress(action)
        }
    }

    override fun getDisplayList(): List<SimpleResult> {
        val simpleResultList: MutableList<SimpleResult> = ArrayList()
        parsedModel.ssid?.let {
            if (it.isNotEmpty()) {
                simpleResultList.add(SimpleResult(R.string.parse_result_content_ssid, it))
            }
        }
        parsedModel.networkEncryption?.let {
            if (it.isNotEmpty()) {
                simpleResultList.add(SimpleResult(R.string.parse_result_content_type, it))
            }
        }
        parsedModel.password?.let {
            if (it.isNotEmpty()) {
                simpleResultList.add(SimpleResult(R.string.parse_result_content_password, it))
            }
        }
        return simpleResultList
    }

}