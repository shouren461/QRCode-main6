package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

/**
 * @author yangfengfan 2020-08-27
 *
 * Wifi解析结果
 */
data class ParseWifiModel(
    val ssid: String? = null,
    val networkEncryption: String? = null,
    val password: String? = null,
    val hidden: Boolean = false,
    val identity: String? = null,
    val anonymousIdentity: String? = null,
    val eapMethod: String? = null,
    val phase2Method: String? = null,
) : BaseParseModel(ParsedFormat.WIFI) {

    override fun getShowText() = getBestShowContent(ssid, networkEncryption, password)

}