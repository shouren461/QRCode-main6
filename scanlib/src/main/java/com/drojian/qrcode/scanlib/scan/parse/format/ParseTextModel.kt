package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

/**
 * @author yangfengfan 2020-08-27
 *
 * 文本解析结果
 */
data class ParseTextModel(val text: String) : BaseParseModel(ParsedFormat.TEXT) {

    override fun getShowText() = text

}