package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat
import java.util.Locale

/**
 * @author yangfengfan 2020-08-28
 *
 * Viber解析结果
 */
data class ParseViberModel(val rawUri: String, val title: String? = null) : BaseParseModel(ParsedFormat.VIBER) {

    val uri = massageURI(rawUri)

    override fun getShowText() = getViberContents(uri)

    private fun getViberContents(rawText: String?): String? {
        rawText?.let {
            return if (rawText.lowercase(Locale.getDefault()).startsWith("viber://add?number=")) {
                rawText.substring("viber://add?number=".length)
            } else {
                rawText
            }
        }
        return null
    }

}