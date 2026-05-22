package com.drojian.qrcode.scanresultlib.webview

import android.webkit.WebView

/**
 * 业务层可在此注入页面级定制逻辑（注入/移除 DOM、滚动、埋点等）。
 * 由 [ResultWebViewActivity.getPagePlugins] 返回；lib 本身不感知具体站点。
 */
interface WebViewPagePlugin {
    fun onPageStarted(view: WebView, url: String) {}
    fun onPageFinished(view: WebView, url: String) {}
}
