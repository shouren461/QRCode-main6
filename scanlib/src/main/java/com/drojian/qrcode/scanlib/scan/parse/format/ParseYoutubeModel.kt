package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

/**
 * @author yangfengfan 2020-08-28
 *
 * Youtube解析结果
 */
data class ParseYoutubeModel(private val rawUri: String) : BaseParseModel(ParsedFormat.YOUTUBE) {

    val uri = massageURI(rawUri)

    override fun getShowText() = uri

}