package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

/**
 * @author yangfengfan 2020-08-28
 *
 * Spotify解析结果
 */
data class ParseSpotifyModel(private val rawUri: String) : BaseParseModel(ParsedFormat.SPOTIFY) {

    val uri = massageURI(rawUri)

    override fun getShowText() = try {
        uri?.substring("spotify:search:".length)
    } catch (e: Exception) {
        uri
    }

}