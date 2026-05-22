package com.drojian.qrcode.zxinglib

import android.graphics.Bitmap
import com.drojian.qrcode.zxinglib.ZXingUtil.getDecodeHints
import com.drojian.qrcode.zxinglib.ZXingUtil.retrofitResult
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer

class ZXingHelper {

    private val tag = "ZXingHelper"

    private var multiFormatReader: MultiFormatReader? = null

    init {
        try {
            multiFormatReader = MultiFormatReader()
            multiFormatReader?.setHints(getDecodeHints())
        } catch (e: Throwable) {
            throw e
        }
    }

    @Throws(Exception::class)
    fun decode(data: ByteArray, width: Int, height: Int): com.drojian.qrcode.baselib.ScanResultModel? {
        var scanResultModel: com.drojian.qrcode.baselib.ScanResultModel? = null
        try {
            val yuvLuminanceSource = PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false)
            var bitmap = BinaryBitmap(HybridBinarizer(yuvLuminanceSource))
            try {
                val result = multiFormatReader?.decodeWithState(bitmap)
                scanResultModel = retrofitResult(result)
            } catch (e: Throwable) {
                multiFormatReader?.reset()
            }

            if (scanResultModel == null) {
                bitmap = BinaryBitmap(HybridBinarizer(yuvLuminanceSource.invert()))
                try {
                    val result = multiFormatReader?.decodeWithState(bitmap)
                    scanResultModel = retrofitResult(result)
                } catch (e: Throwable) {
                    multiFormatReader?.reset()
                }
            }
        } catch (e: Throwable) {
            throw e
        }
        return scanResultModel
    }

    @Throws(Exception::class)
    fun decodeBitmap(bitmap: Bitmap): com.drojian.qrcode.baselib.ScanResultModel? {
        var scanResultModel: com.drojian.qrcode.baselib.ScanResultModel? = null
        try {
            val lWidth = bitmap.width
            val lHeight = bitmap.height
            val lPixels = IntArray(lWidth * lHeight)
            bitmap.getPixels(lPixels, 0, lWidth, 0, 0, lWidth, lHeight)
            val luminanceSource = RGBLuminanceSource(lWidth, lHeight, lPixels)

            var binaryBitmap = BinaryBitmap(HybridBinarizer(luminanceSource))
            try {
                val result = multiFormatReader?.decodeWithState(binaryBitmap)
                scanResultModel = retrofitResult(result)
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                multiFormatReader?.reset()
            }

            if (scanResultModel == null) {
                binaryBitmap = BinaryBitmap(HybridBinarizer(luminanceSource.invert()))
                try {
                    val result = multiFormatReader?.decodeWithState(binaryBitmap)
                    scanResultModel = retrofitResult(result)
                } catch (e: Throwable) {
                    e.printStackTrace()
                } finally {
                    multiFormatReader?.reset()
                }
            }
        } catch (e: Throwable) {
            throw e
        }
        return scanResultModel
    }

}