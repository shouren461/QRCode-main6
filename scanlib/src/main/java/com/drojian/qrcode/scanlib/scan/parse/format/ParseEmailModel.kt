package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

/**
 * @author yangfengfan 2020-08-27
 *
 * 邮件解析结果
 */
data class ParseEmailModel(
    val tos: Array<String>? = arrayOf(),
    val ccs: Array<String>? = arrayOf(),
    val bccs: Array<String>? = arrayOf(),
    val subject: String? = null,
    val body: String? = null,
) : BaseParseModel(ParsedFormat.EMAIL_ADDRESS) {

    override fun getShowText() = try {
        getBestShowContent(tos?.get(0), subject, body, ccs?.get(0), bccs?.get(0))
    } catch (e: Exception) {
        null
    }

}