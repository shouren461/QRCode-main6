package com.drojian.qrcode.scanresultlib.handler

import android.app.Activity
import com.drojian.qrcode.scanlib.scan.parse.format.ParseInstagramModel
import com.drojian.qrcode.scanresultlib.BaseResultHandler
import com.drojian.qrcode.scanresultlib.ResultHandlerConfig
import com.drojian.qrcode.scanresultlib.button.ParseAction
import com.drojian.qrcode.scanresultlib.util.HandlerUtils

/**
 * @author yangfengfan 2020-10-22
 */
class InstagramHandler(activity: Activity, val parseModel: ParseInstagramModel, resultHandlerConfig: ResultHandlerConfig) : BaseResultHandler(activity, parseModel,resultHandlerConfig) {
    override val parseActionList: Array<ParseAction> = arrayOf(
        ParseAction.OPEN,
        ParseAction.COPY,
        ParseAction.SHARE,
    )


    override fun handleButtonPress(action: ParseAction) {
        when (action) {
            ParseAction.OPEN-> {
                HandlerUtils.openURL(this, parseModel.uri)
            }
            else -> super.handleButtonPress(action)
        }
    }

    override fun getDisplayList() = arrayListOf(SimpleResult(SimpleResult.CONTENT_TYPE_URL, parseModel.uri))

}