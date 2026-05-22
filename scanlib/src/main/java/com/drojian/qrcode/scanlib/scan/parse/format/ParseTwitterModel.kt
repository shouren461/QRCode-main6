package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat
import java.util.Locale

/**
 * @author yangfengfan 2020-08-28
 *
 * Twitter解析结果
 */
data class ParseTwitterModel(val rawUri: String) : BaseParseModel(ParsedFormat.TWITTER) {

    val uri = massageURI(rawUri)

    override fun getShowText() = getTwitterContents(uri)

    private fun getTwitterContents(rawText: String?): String? {
        rawText?.let {
            return if (rawText.lowercase(Locale.getDefault()).startsWith("twitter://user?screen_name=")) {
                rawText.substring("twitter://user?screen_name=".length)
            } else {
                rawText
            }
        }
        return null
    }

}