package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

/**
 * @author yangfengfan 2020-08-27
 *
 * URI解析结果
 */
data class ParseURIModel(val rawUri: String, val title: String? = null) : BaseParseModel(ParsedFormat.URI) {

    val uri = massageURI(rawUri)

    override fun getShowText() = uri
}