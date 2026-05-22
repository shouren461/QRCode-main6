package com.drojian.qrcode.utillib.image.loader

import android.graphics.Bitmap
import android.util.LruCache

class MemoryCacheUtils {

    private var memoryCache: LruCache<String, Bitmap>? = null

    init {
        val maxMemory = Runtime.getRuntime().maxMemory()
        memoryCache = object : LruCache<String, Bitmap>(maxMemory.toInt() / 8) {
            override fun sizeOf(key: String?, value: Bitmap): Int {
                return value.byteCount
            }
        }
    }

    fun loadBitmapFormMemory(url: String): Bitmap? {
        return memoryCache?.get(url)
    }

    fun saveBitmapToMemory(url: String, bitmap: Bitmap) {
        memoryCache?.put(url, bitmap)
    }
}