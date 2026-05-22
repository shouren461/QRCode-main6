package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat
import java.util.Locale

/**
 * @author yangfengfan 2020-08-28
 *
 * Facebook解析结果
 */
data class ParseFacebookModel(private val rawUri: String) : BaseParseModel(ParsedFormat.FACEBOOK) {

    val uri = massageURI(rawUri)

    override fun getShowText() = getFacebookContents(uri)

    private fun getFacebookContents(rawText: String?): String? {
        rawText?.let {
            return if (rawText.lowercase(Locale.getDefault()).startsWith("fb://profile/")) {
                rawText.substring("fb://profile/".length)
            } else {
                rawText
            }
        }
        return null
    }

}