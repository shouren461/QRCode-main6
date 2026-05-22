package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

/**
 * @author yangfengfan 2020-08-27
 *
 * Barcode解析结果
 */
data class ParseBarCodeModel(val text: String) : BaseParseModel(ParsedFormat.BarCode) {

    override fun getShowText() = text

}