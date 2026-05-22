package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

/**
 * @author yangfengfan 2020-08-27
 *
 * 商品解析结果
 */
data class ParseProductModel(val productID: String, val normalizedProductID: String? = null) :
    BaseParseModel(ParsedFormat.PRODUCT) {

    override fun getShowText() = productID

}