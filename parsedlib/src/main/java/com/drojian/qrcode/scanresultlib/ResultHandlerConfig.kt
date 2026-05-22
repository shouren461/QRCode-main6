package com.drojian.qrcode.scanresultlib

import com.drojian.qrcode.scanresultlib.util.HandlerUtils
import com.drojian.qrcode.utillib.constant.CountryCode


class ResultHandlerConfig {
    var searchEngine: HandlerUtils.SearchEngine = HandlerUtils.SearchEngine.Google
    var useWebView: Boolean = true
    var countryCode: String = CountryCode.Default
    var webViewClass: Class<*>? = null
    var productName: String? = null
}