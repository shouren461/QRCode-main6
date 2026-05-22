package com.drojian.qrcode.utillib.image.loader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.PictureDrawable
import android.text.TextUtils
import com.larvalabs.svgandroid.SVGParser
import java.net.HttpURLConnection
import java.net.URL

class NetworkCacheUtils(localCache: LocalCacheUtils, memoryCache: MemoryCacheUtils) {

    private val localCacheUtils: LocalCacheUtils = localCache
    private val memoryCacheUtils: MemoryCacheUtils = memoryCache

    fun loadBitmapFormNetwork(url: String): Bitmap? {
        var loadUrl = url
        try {
            var httpURLConnection = getHttpURULConnection(loadUrl)
            if (loadUrl.endsWith("svg") || loadUrl.endsWith("SVG")) {
                val inputStream = httpURLConnection?.inputStream
                val svg = SVGParser.getSVGFromInputStream(inputStream)
                val pictureDrawable = svg.createPictureDrawable()
                val bitmap = pictureDrawable2Bitmap(pictureDrawable)
                bitmap?.let {
                    localCacheUtils.saveBitmapToLocal(url, it)
                    memoryCacheUtils.saveBitmapToMemory(url, it)
                }
                return bitmap
            } else {
                httpURLConnection?.let {
                    if (it.responseCode == 301 || it.responseCode == 302) {
                        loadUrl = it.getHeaderField("Location")
                        if (!TextUtils.isEmpty(loadUrl)) {
                            it.disconnect()
                            httpURLConnection = getHttpURULConnection(loadUrl)
                        }
                    }
                }
                if (httpURLConnection?.responseCode == 200) {
                    val bitmap = BitmapFactory.decodeStream(httpURLConnection?.inputStream)
                    bitmap?.let {
                        localCacheUtils.saveBitmapToLocal(url, it)
                        memoryCacheUtils.saveBitmapToMemory(url, it)
                    }
                    return bitmap
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun getHttpURULConnection(urlLoad: String): HttpURLConnection? {
        try {
            val url = URL(urlLoad)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.connectTimeout = 10 * 1000
            httpURLConnection.readTimeout = 10 * 1000
            httpURLConnection.requestMethod = "GET"
            return httpURLConnection
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun pictureDrawable2Bitmap(pictureDrawable: PictureDrawable): Bitmap? {
        val bitmap = Bitmap.createBitmap(pictureDrawable.intrinsicWidth, pictureDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        bitmap?.let {
            val canvas = Canvas(it)
            canvas.drawPicture(pictureDrawable.picture)
            return it
        }
        return null
    }
}