package com.drojian.qrcode.scanresultlib.handler

import android.app.Activity
import com.drojian.qrcode.scanlib.scan.parse.format.ParseTelModel
import com.drojian.qrcode.scanresultlib.BaseResultHandler
import com.drojian.qrcode.scanresultlib.ResultHandlerConfig
import com.drojian.qrcode.scanresultlib.button.ParseAction
import com.drojian.qrcode.scanresultlib.util.HandlerUtils
import com.drojian.qrcode.scanresultlib.util.HandlerUtils.formatPhone

/**
 * @author yangfengfan 2020-10-19
 */
class TelHandler(activity: Activity, val parsedModel: ParseTelModel, resultHandlerConfig: ResultHandlerConfig) :
    BaseResultHandler(activity, parsedModel, resultHandlerConfig) {
    override val parseActionList: Array<ParseAction> = arrayOf(
        ParseAction.CALL,
        ParseAction.ADD_CONTACTS,
        ParseAction.COPY,
        ParseAction.SHARE,
    )

    override fun handleButtonPress(action: ParseAction) {
        when (action) {
            ParseAction.CALL -> {
                HandlerUtils.dialPhoneFromUri(activity, parsedModel.telURI)
            }
            ParseAction.ADD_CONTACTS -> {
                parsedModel.number?.let {
                    HandlerUtils.addPhoneOnlyContact(activity, arrayOf(it), null)
                }
            }
            else -> super.handleButtonPress(action)
        }
    }

    override fun getDisplayContent() = formatPhone(parsedModel.getShowText()?.replace("\r", ""))

    override fun getDisplayList() = arrayListOf(SimpleResult(SimpleResult.CONTENT_TYPE_NUM, getDisplayContent()))

}