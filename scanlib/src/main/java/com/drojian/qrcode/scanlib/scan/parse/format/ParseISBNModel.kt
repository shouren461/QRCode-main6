package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

/**
 * @author yangfengfan 2020-08-27
 *
 * 图书解析结果
 */
data class ParseISBNModel(val isbn: String) : BaseParseModel(ParsedFormat.ISBN) {

    override fun getShowText() = isbn

}