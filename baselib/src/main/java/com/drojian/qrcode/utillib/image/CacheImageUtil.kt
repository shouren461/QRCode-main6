package com.drojian.qrcode.utillib.image

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object CacheImageUtil {

    @JvmStatic
    fun cacheBitmap(context: Context, bitmap: Bitmap, fileName: String?): String {
        var filePath = ""
        try {
            val outputStream: FileOutputStream
            var file: File? = null
            try {
                file = File(context.filesDir, fileName)
                outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (file != null) {
                filePath = file.absolutePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return filePath
    }

    @JvmStatic
    fun cacheBitmapIsExist(context: Context, fileName: String): String {
        try {
            val filePath = context.filesDir.toString() + "/" + fileName
            val file = File(filePath)
            if (file.exists()) {
                return filePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @JvmStatic
    fun deleteCacheImage(context: Context, fileName: String) {
        try {
            val filePath = context.filesDir.toString() + "/" + fileName
            if (TextUtils.isEmpty(filePath) || !filePath.endsWith(".jpg")) {
                return
            }
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 保存bitmap,并返回存储路径
     */
    @JvmStatic
    fun saveBitmap(context: Context, bitmap: Bitmap, name: String? = System.currentTimeMillis().toString(), folderName: String): String? {
        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                //保存路径
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/$folderName")
                }
            }
            //返回出一个URI
            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return null

            //这个打开了输出流  直接保存图片就好了
            context.contentResolver.openOutputStream(uri).use {
                it ?: return null
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
            }
            return uri.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}