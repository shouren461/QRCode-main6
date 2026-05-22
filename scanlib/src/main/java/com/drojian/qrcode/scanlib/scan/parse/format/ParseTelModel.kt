package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

/**
 * @author yangfengfan 2020-08-27
 *
 * Tel解析结果
 */
data class ParseTelModel(
    val number: String? = null,
    val telURI: String? = null,
    val title: String? = null,
) : BaseParseModel(ParsedFormat.TEL) {

    override fun getShowText() = getBestShowContent(number, telURI, title)

}