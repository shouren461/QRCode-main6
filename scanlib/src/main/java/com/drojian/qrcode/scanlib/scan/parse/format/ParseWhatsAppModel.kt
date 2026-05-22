package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat
import java.util.Locale

/**
 * @author yangfengfan 2020-08-28
 *
 * WhatsApp解析结果
 */
data class ParseWhatsAppModel(val rawUri: String, val title: String? = null) : BaseParseModel(ParsedFormat.WHATSAPP) {

    val uri = massageURI(rawUri)

    override fun getShowText() = getWhatsAppContents(uri)

    private fun getWhatsAppContents(rawText: String?): String? {
        rawText?.let {
            return if (rawText.lowercase(Locale.ROOT).startsWith("whatsapp://send?phone=")) {
                rawText.substring("whatsapp://send?phone=".length)
            } else {
                rawText
            }
        }
        return null
    }

}