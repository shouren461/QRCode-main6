package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat
import java.util.Locale

/**
 * @author yangfengfan 2020-08-28
 *
 * Instagram解析结果
 */
data class ParseInstagramModel(private val rawUri: String) : BaseParseModel(ParsedFormat.INSTAGRAM) {

    val uri = massageURI(rawUri)

    override fun getShowText() = getInstagramContents(uri)

    private fun getInstagramContents(rawText: String?): String? {
        rawText?.let {
            return if (rawText.lowercase(Locale.getDefault()).startsWith("instagram://user?username=")) {
                rawText.substring("instagram://user?username=".length)
            } else {
                rawText
            }
        }
        return null
    }

}