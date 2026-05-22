package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

/**
 * @author yangfengfan 2020-08-27
 */
data class ParseExpandedProductModel(
    val rawText: String? = null,
    val productID: String? = null,
    val sscc: String? = null,
    val lotNumber: String? = null,
    val productionDate: String? = null,
    val packagingDate: String? = null,
    val bestBeforeDate: String? = null,
    val expirationDate: String? = null,
    val weight: String? = null,
    val weightType: String? = null,
    val weightIncrement: String? = null,
    val price: String? = null,
    val priceIncrement: String? = null,
    val priceCurrency: String? = null,
    val uncommonAIs: Map<String, String>? = null
) : BaseParseModel(ParsedFormat.PRODUCT) {

    override fun getShowText() = rawText

}