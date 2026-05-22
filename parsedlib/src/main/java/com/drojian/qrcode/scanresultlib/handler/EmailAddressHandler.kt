package com.drojian.qrcode.scanresultlib.handler

import android.app.Activity
import com.drojian.qrcode.scanlib.scan.parse.format.ParseEmailModel
import com.drojian.qrcode.scanresultlib.BaseResultHandler
import com.drojian.qrcode.scanresultlib.R
import com.drojian.qrcode.scanresultlib.ResultHandlerConfig
import com.drojian.qrcode.scanresultlib.button.ParseAction
import com.drojian.qrcode.scanresultlib.util.HandlerUtils
import com.drojian.qrcode.scanresultlib.util.HandlerUtils.trimStringBuilder

/**
 * @author yangfengfan 2020-10-19
 */
class EmailAddressHandler(activity: Activity, val parsedModel: ParseEmailModel, resultHandlerConfig: ResultHandlerConfig) :
    BaseResultHandler(activity, parsedModel, resultHandlerConfig) {

    override val parseActionList: Array<ParseAction> = arrayOf(
        ParseAction.SEND_EMAIL,
        ParseAction.ADD_CONTACTS,
        ParseAction.COPY,
        ParseAction.SHARE,
    )


    override fun handleButtonPress(action: ParseAction) {
        when (action) {
            ParseAction.SEND_EMAIL -> {
                HandlerUtils.sendEmail(activity, parsedModel.tos, parsedModel.ccs, parsedModel.bccs, parsedModel.subject, parsedModel.body)
            }
            ParseAction.ADD_CONTACTS -> {
                HandlerUtils.addEmailOnlyContact(activity, parsedModel.tos, null)
            }
            else -> super.handleButtonPress(action)
        }
    }

    override fun getDisplayList(): List<SimpleResult> {
        val simpleResultList: MutableList<SimpleResult> = ArrayList()
        parsedModel.tos?.let {
            val stringBuilder = StringBuilder()
            for (item in it) {
                stringBuilder.append(item + "\n")
            }
            trimStringBuilder(stringBuilder)
            simpleResultList.add(SimpleResult(R.string.parse_result_content_to, stringBuilder.toString()))
        }
        parsedModel.ccs?.let {
            val stringBuilder = StringBuilder()
            for (item in it) {
                stringBuilder.append(item + "\n")
            }
            trimStringBuilder(stringBuilder)
            simpleResultList.add(SimpleResult(R.string.parse_result_content_cc, stringBuilder.toString()))
        }
        parsedModel.bccs?.let {
            val stringBuilder = StringBuilder()
            for (item in it) {
                stringBuilder.append(item + "\n")
            }
            trimStringBuilder(stringBuilder)
            simpleResultList.add(SimpleResult(R.string.parse_result_content_bcc, stringBuilder.toString()))
        }
        parsedModel.subject?.let {
            simpleResultList.add(SimpleResult(R.string.parse_result_content_subject, it))
        }
        parsedModel.body?.let {
            simpleResultList.add(SimpleResult(R.string.parse_result_content_body, it))
        }
        return simpleResultList
    }

}