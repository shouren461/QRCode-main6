package com.drojian.qrcode.utillib.image

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.drojian.qrcode.utillib.log.LogHelper.log
import java.io.File

object ImageUtil {

    @JvmStatic
    fun chooseGallery(activity: Activity, requestCode: Int) {
        try {
            val intentToPickPic = Intent(Intent.ACTION_PICK, null)
            intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            activity.startActivityForResult(intentToPickPic, requestCode)
        } catch (e: Exception) {
            e.log("chooseImage")
        }
    }

    // 使用存储访问框架选择图片
    @JvmStatic
    fun openImageSAF(activity: Activity, requestCode: Int) {
        try {
            val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            photoPickerIntent.type = "image/*"
            activity.startActivityForResult(photoPickerIntent, requestCode)
        } catch (e: Exception) {
            e.log("openImageSAF")
        }
    }

    @JvmStatic
    fun imageCapture(activity: Activity, requestCode: Int): Uri? {
        return imageCapture(activity, requestCode, "BaseFileProvider.AUTHORITIES")
    }

    /**
     * 打开相机拍摄照片
     *
     *  @return 拍摄后的保存路径
     */
    @JvmStatic
    fun imageCapture(activity: Activity, requestCode: Int, authorities: String): Uri? {
        var captureImageUri: Uri? = null
        try {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(activity.packageManager)?.also {
                    File.createTempFile("IMG", ".jpg", activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)).apply {
                        captureImageUri = if (Build.VERSION.SDK_INT >= 24) {
                            //todo AUTHORITIES
                            FileProvider.getUriForFile(activity, authorities, this)
                        } else {
                            Uri.fromFile(this)
                        }
                    }

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureImageUri)
                    try {
                        activity.startActivityForResult(takePictureIntent, requestCode)
                    } catch (e: ActivityNotFoundException) {
                        e.log()
                    }
                }
            }
        } catch (e: Exception) {
            e.log("imageCapture")
        }
        return captureImageUri
    }

}