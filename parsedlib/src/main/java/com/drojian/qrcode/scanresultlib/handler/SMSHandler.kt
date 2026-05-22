package com.drojian.qrcode.scanresultlib.handler

import android.app.Activity
import com.drojian.qrcode.scanlib.scan.parse.format.ParseSMSModel
import com.drojian.qrcode.scanresultlib.BaseResultHandler
import com.drojian.qrcode.scanresultlib.R
import com.drojian.qrcode.scanresultlib.ResultHandlerConfig
import com.drojian.qrcode.scanresultlib.button.ParseAction
import com.drojian.qrcode.scanresultlib.util.HandlerUtils
import com.drojian.qrcode.scanresultlib.util.HandlerUtils.formatPhone
import com.drojian.qrcode.scanresultlib.util.HandlerUtils.sendMMS

/**
 * @author yangfengfan 2020-10-19
 */
class SMSHandler(activity: Activity, val parsedModel: ParseSMSModel, resultHandlerConfig: ResultHandlerConfig) :
    BaseResultHandler(activity, parsedModel, resultHandlerConfig) {
    override val parseActionList: Array<ParseAction> = arrayOf(
        ParseAction.SEND_SMS,
        ParseAction.SEND_MMS,
        ParseAction.COPY,
        ParseAction.SHARE,
    )

    override fun handleButtonPress(action: ParseAction) {
        when (action) {
            ParseAction.SEND_SMS -> {
                HandlerUtils.sendSMS(activity, parsedModel.numbers?.get(0), parsedModel.body)
            }
            ParseAction.SEND_MMS -> {
                sendMMS(activity, parsedModel.numbers?.get(0), parsedModel.subject, parsedModel.body)
            }
            else -> super.handleButtonPress(action)
        }
    }

    override fun getDisplayList(): List<SimpleResult> {
        val simpleResultList: MutableList<SimpleResult> = ArrayList()
        parsedModel.numbers?.let {
            val contents = StringBuilder(50)
            for (item in it) {
                maybeAppend(formatPhone(item), contents)
            }
            if (it.isNotEmpty() && contents.isNotEmpty()) {
                simpleResultList.add(SimpleResult(R.string.parse_result_content_tel, contents.toString()))
            }
        }
        parsedModel.subject?.let {
            if (it.isNotEmpty()) {
                simpleResultList.add(SimpleResult(R.string.parse_result_content_subject, it))
            }
        }
        parsedModel.body?.let {
            if (it.isNotEmpty()) {
                simpleResultList.add(SimpleResult(R.string.parse_result_content_body, it))
            }
        }
        return simpleResultList
    }

}