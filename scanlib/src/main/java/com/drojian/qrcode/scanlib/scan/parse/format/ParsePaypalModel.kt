package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

/**
 * @author yangfengfan 2020-08-28
 *
 * Paypal解析结果
 */
data class ParsePaypalModel(private val rawUri: String) : BaseParseModel(ParsedFormat.PAYPAL) {

    val uri = massageURI(rawUri)

    override fun getShowText() = massageURI(rawUri)
}