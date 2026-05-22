package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

/**
 * @author yangfengfan 2020-08-27
 *
 * 信息解析结果
 */
data class ParseSMSModel(
    val numbers: Array<String>? = arrayOf(),
    val vias: Array<String>? = arrayOf(),
    val subject: String? = null,
    val body: String? = null,
) : BaseParseModel(ParsedFormat.SMS) {

    override fun getShowText() = try {
        getBestShowContent(numbers?.get(0), subject, body, vias?.get(0))
    } catch (e: Exception) {
        null
    }

}