package com.drojian.qrcode.utillib.image.loader

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageLoadUtils private constructor(context: Context) {

    companion object {
        private var instance: ImageLoadUtils? = null
        fun getInstance(context: Context): ImageLoadUtils {
            if (null == instance) {
                instance = ImageLoadUtils(context)
            }
            return instance as ImageLoadUtils
        }
    }

    private val memoryCacheUtils by lazy { MemoryCacheUtils() }
    private val localCacheUtils by lazy { LocalCacheUtils(context, memoryCacheUtils) }
    private val networkCacheUtils by lazy { NetworkCacheUtils(localCacheUtils, memoryCacheUtils) }

    fun loadBitmap(scope: CoroutineScope, url: String, listener: OnLoadBitmapListener) {
        if (url.isBlank()) {
            listener.loadBitmap(null)
            return
        }
        var bitmap: Bitmap?
        scope.launch(Dispatchers.IO) {
            val bitmapFormMemory = memoryCacheUtils.loadBitmapFormMemory(url)
            bitmap = if (null != bitmapFormMemory) {
                bitmapFormMemory
            } else {
                val bitmapFormLocal = localCacheUtils.loadBitmapFromLocal(url)
                bitmapFormLocal ?: networkCacheUtils.loadBitmapFormNetwork(url)
            }
            withContext(Dispatchers.Main) {
                listener.loadBitmap(bitmap)
            }
        }
    }

    interface OnLoadBitmapListener {
        fun loadBitmap(bitmap: Bitmap?)
    }

}