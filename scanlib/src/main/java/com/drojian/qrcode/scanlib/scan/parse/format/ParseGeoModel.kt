package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

/**
 * @author yangfengfan 2020-08-27
 *
 * 位置解析结果
 */
data class ParseGeoModel(val latitude: Double = 0.0, val longitude: Double = 0.0, val altitude: Double = 0.0, val query: String? = null) :
    BaseParseModel(ParsedFormat.GEO) {

    override fun getShowText() = getBestShowContent(query, latitude.toString(), longitude.toString(), altitude.toString())

    fun getGeoURI(): String {
        val result = StringBuilder()
        result.append("geo:")
        result.append(latitude)
        result.append(',')
        result.append(longitude)
        if (altitude > 0) {
            result.append(',')
            result.append(altitude)
        }
        if (query != null) {
            result.append('?')
            result.append(query)
        }
        return result.toString()
    }

}