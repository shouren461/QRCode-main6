package com.drojian.qrcode.utillib.image.loader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest

class LocalCacheUtils(context: Context, memoryCache: MemoryCacheUtils) {
    private var cacheDir: File? = null
    private var memoryCacheUtils: MemoryCacheUtils? = memoryCache

    init {
        try {
            cacheDir = context.cacheDir
            if (cacheDir?.exists() == false) {
                cacheDir?.mkdir()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadBitmapFromLocal(url: String): Bitmap? {
        try {
            val fileName = fileMd5(url)
            val file = File(cacheDir, fileName)
            if (file.exists()) {
                val localBitmap = BitmapFactory.decodeStream(FileInputStream(file))
                memoryCacheUtils?.saveBitmapToMemory(url, localBitmap)
                return localBitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun saveBitmapToLocal(url: String, bitmap: Bitmap) {
        try {
            val fileName = fileMd5(url)
            var file = File(cacheDir, fileName.trim())
            if (!file.exists()) {
                file.createNewFile()
                file = File(cacheDir, fileName.trim())
            }
            val fileOutputStream = FileOutputStream(file)
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)) {
                fileOutputStream.flush()
                fileOutputStream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fileMd5(value: String): String {
        var hash: ByteArray? = null
        try {
            hash = MessageDigest.getInstance("MD5").digest(value.toByteArray(charset = Charsets.UTF_8))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        hash?.let {
            val hex = StringBuilder(it.size * 2)
            it.forEach { b ->
                if ((b.toInt() and 0xFF) < 0x10) {
                    hex.append("0")
                }
                hex.append((b.toInt() and 0xFF))
            }
            return hex.toString()
        }
        return ""
    }
}