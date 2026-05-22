package com.drojian.qrcode.cameralib

import android.graphics.*
import android.hardware.Camera
import com.drojian.qrcode.cameralib.camera.CameraManager
import java.io.ByteArrayOutputStream

object BarCodeBitmapUtil {

    @JvmStatic
    fun getBarcode(data: ByteArray, rect: Rect?, cameraManager: CameraManager): Bitmap? {
        return rotateBitmap(getBarcode(dataToBitmap(data, cameraManager), rect), cameraManager.camera.orientation.toFloat())
    }

    @JvmStatic
    fun getBarcode(cameraBitmap: Bitmap?, rect: Rect?): Bitmap? {
        val i = 0
        var cropBitmap: Bitmap? = null
        if (cameraBitmap != null) {
            if (rect != null) {
                try {
                    val centerX = rect.centerX()
                    val centerY = rect.centerY()
                    val max = rect.width().coerceAtLeast(rect.height())
                    val i2 = max + max / 4
                    var i3 = centerX - i2 / 2
                    var i4 = centerY - i2 / 2
                    var i5 = centerX + i2 / 2
                    var i6 = centerY + i2 / 2
                    val width = cameraBitmap.width - i
                    if (i3 < i) {
                        i5 = (i + i2).coerceAtMost(width)
                        i3 = i
                    } else if (i5 > width) {
                        i3 = (width - i2).coerceAtLeast(i)
                        i5 = width
                    }
                    val height = cameraBitmap.height - i
                    if (i4 < i) {
                        i6 = (i2 + i).coerceAtMost(height)
                        i4 = i
                    } else if (i6 > height) {
                        i4 = (height - i2).coerceAtLeast(i)
                        i6 = height
                    }
                    cropBitmap = Bitmap.createBitmap(cameraBitmap, i3, i4, i5 - i3, i6 - i4)

                } catch (unused: Exception) {
                    cropBitmap = cameraBitmap
                }
            }
        }
        return cropBitmap
    }

    private fun dataToBitmap(data: ByteArray, cameraManager: CameraManager): Bitmap {
        val parameters: Camera.Parameters = cameraManager.camera.camera.parameters
        val width = parameters.previewSize.width
        val height = parameters.previewSize.height
        val yuv = YuvImage(data, parameters.previewFormat, width, height, null)
        val out = ByteArrayOutputStream()
        yuv.compressToJpeg(Rect(0, 0, width, height), 50, out)
        val bytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun rotateBitmap(origin: Bitmap?, degree: Float): Bitmap? {
        if (origin == null) {
            return null
        }
        val width = origin.width
        val height = origin.height
        val matrix = Matrix()
        matrix.setRotate(degree)
        val newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
        if (newBM == origin) {
            return newBM
        }
        origin.recycle()
        return newBM
    }
}