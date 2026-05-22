package com.drojian.qrcode.scanresultlib

import android.app.Activity
import android.content.Intent
import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanresultlib.button.ParseAction
import com.drojian.qrcode.scanresultlib.handler.SimpleResult
import com.drojian.qrcode.scanresultlib.util.HandlerUtils
import com.drojian.qrcode.scanresultlib.util.HandlerUtils.launchIntent
import com.drojian.qrcode.scanresultlib.util.HandlerUtils.trimStringBuilder

/**
 * @author yangfengfan 2020-09-03
 */
abstract class BaseResultHandler(
    val activity: Activity,
    val baseParseModel: BaseParseModel,
    var resultHandlerConfig: ResultHandlerConfig
) {

    abstract val parseActionList: Array<ParseAction>

    open fun handleButtonPress(action: ParseAction) {
        when (action) {
            ParseAction.SHARE -> {
                shareContent()
            }
            ParseAction.COPY -> {
                HandlerUtils.copyResultText(activity, generateCopyText(getDisplayList()))
            }
            else -> {}
        }
    }

    open fun getActionCount() = parseActionList.size



    open fun getDisplayContent(): String = baseParseModel.getShowText().toString()

    open fun getDisplayList(): List<SimpleResult> = arrayListOf(SimpleResult(SimpleResult.CONTENT_TYPE_TEXT, getDisplayContent()))


    fun generateCopyText(list: List<SimpleResult>?): String {
        val stringBuilder = StringBuilder()
        list?.let {
            for (simpleResult in list) {
                if (simpleResult.titleResId > 0) {
                    stringBuilder.append("${activity.resources.getString(simpleResult.titleResId)} ${simpleResult.content}").append("\n")
                } else {
                    stringBuilder.append(simpleResult.content.trimIndent()).append("\n")
                }
            }
            trimStringBuilder(stringBuilder)
        }
        return stringBuilder.toString()
    }

    protected open fun trim(stringBuilder: StringBuilder?) {
        if (stringBuilder != null && stringBuilder.isNotEmpty()) {
            if (stringBuilder[stringBuilder.length - 1] == '\n') {
                stringBuilder.deleteCharAt(stringBuilder.length - 1)
            }
        }
    }

    protected fun maybeAppend(value: String?, result: StringBuilder) {
        value?.let {
            if (value.isNotEmpty()) {
                // Don't add a newline before the first value
                if (result.isNotEmpty()) {
                    result.append('\n')
                }
                result.append(value)
            }
        }
    }

    protected fun maybeAppend(values: Array<String>?, result: StringBuilder) {
        values?.let {
            for (value in it) {
                maybeAppend(value, result)
            }
        }
    }


    private fun shareContent() {
        getDisplayList().let {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, generateCopyText(it))
            launchIntent(activity, intent)
        }
    }

}