package com.drojian.qrcode.utillib.image

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.drojian.qrcode.baselib.R
import com.drojian.qrcode.utillib.log.LogHelper.log

object BitmapUtil {


    /**
     * bitmap存储路径为空，存储bitmap并记录存储路径，然后分享；否则直接分享
     */
    @JvmStatic
    fun shareBitmap(context: Context, bitmap: Bitmap, paramUriStr: String?): String? {
        try {
            val uriStr = paramUriStr ?: MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "share", "share")
            Uri.parse(uriStr).let {
                Intent().apply {
                    this.action = Intent.ACTION_SEND
                    this.type = "image/*"
                    this.putExtra(Intent.EXTRA_STREAM, it)
                    context.startActivity(Intent.createChooser(this, context.getString(com.drojian.qrcode.languagelib.R.string.share)))
                }
            }
            return uriStr
        } catch (e: Exception) {
            e.log("shareBitmap")
        }
        return null
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
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
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
            e.log("saveBitmap")
        }
        return null
    }

    @JvmStatic
    fun rotateBitmap(angle: Int, bitmap: Bitmap): Bitmap? {
        var returnBm: Bitmap? = null
        try {
            val matrix = Matrix()
            matrix.postRotate(angle.toFloat())
            try {
                returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
            }
            if (returnBm == null) {
                returnBm = bitmap
            }
            if (bitmap != returnBm) {
                bitmap.recycle()
            }
        } catch (e: Exception) {
            e.log()
        }
        return returnBm
    }

    @JvmStatic
    fun readPictureDegree(path: String?): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path!!)
            when (exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
                else -> { }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return degree
    }
}