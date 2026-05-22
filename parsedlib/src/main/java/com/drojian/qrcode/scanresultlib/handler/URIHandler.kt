package com.drojian.qrcode.scanresultlib.handler

import android.app.Activity
import com.drojian.qrcode.scanlib.scan.parse.format.ParseURIModel
import com.drojian.qrcode.scanresultlib.BaseResultHandler
import com.drojian.qrcode.scanresultlib.ResultHandlerConfig
import com.drojian.qrcode.scanresultlib.button.ParseAction
import com.drojian.qrcode.scanresultlib.util.HandlerUtils.openURL

/**
 * @author yangfengfan 2020-10-19
 */
class URIHandler(activity: Activity, val parsedModel: ParseURIModel, resultHandlerConfig: ResultHandlerConfig) :
    BaseResultHandler(activity, parsedModel, resultHandlerConfig) {
    override val parseActionList: Array<ParseAction> = arrayOf(
        ParseAction.OPEN,
        ParseAction.COPY,
        ParseAction.SHARE,
    )

    override fun handleButtonPress(action: ParseAction) {
        when (action) {
            ParseAction.OPEN -> {
                openURL(this, parsedModel.uri)
            }

            else -> super.handleButtonPress(action)
        }
    }

    override fun getDisplayList() = arrayListOf(SimpleResult(SimpleResult.CONTENT_TYPE_URL, parsedModel.uri))

}