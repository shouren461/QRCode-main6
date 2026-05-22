package com.drojian.qrcode.scanresultlib.handler

import android.app.Activity
import com.drojian.qrcode.scanlib.scan.parse.format.ParseTextModel
import com.drojian.qrcode.scanresultlib.BaseResultHandler
import com.drojian.qrcode.scanresultlib.ResultHandlerConfig
import com.drojian.qrcode.scanresultlib.button.ParseAction
import com.drojian.qrcode.scanresultlib.util.HandlerUtils
import com.drojian.qrcode.scanresultlib.util.HandlerUtils.shareByEmail
import com.drojian.qrcode.scanresultlib.util.HandlerUtils.shareBySMS

/**
 * @author yangfengfan 2020-10-19
 */
class TextHandler(activity: Activity, val parsedModel: ParseTextModel, resultHandlerConfig: ResultHandlerConfig) :
    BaseResultHandler(activity, parsedModel, resultHandlerConfig) {

    override val parseActionList: Array<ParseAction> = arrayOf(
        ParseAction.WEB_SEARCH,
        ParseAction.SEND_SMS,
        ParseAction.SEND_EMAIL,
        ParseAction.COPY,
        ParseAction.SHARE,
    )

    override fun handleButtonPress(action: ParseAction) {
        when (action) {
            ParseAction.SEND_SMS -> {
                shareBySMS(activity, parsedModel.text)
            }
            ParseAction.SEND_EMAIL -> {
                shareByEmail(activity, parsedModel.text)
            }
            ParseAction.WEB_SEARCH -> {
                HandlerUtils.webSearch(this, parsedModel.text)
            }

            else -> super.handleButtonPress(action)
        }
    }

    override fun getDisplayList() = arrayListOf(SimpleResult(SimpleResult.CONTENT_TYPE_TEXT, parsedModel.text))

}