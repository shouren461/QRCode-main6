package com.drojian.qrcode.gvlib

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import com.drojian.qrcode.baselib.ScanResultModel
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.nio.ByteBuffer

class GVHelper(context: Context) {
    private val barcodeDetector: BarcodeDetector = BarcodeDetector.Builder(context).build()


    fun decode(data: ByteArray, width: Int, height: Int): ArrayList<ScanResultModel> {
        val scanResultList: ArrayList<ScanResultModel> = arrayListOf()
        try {
            val frame = Frame.Builder().setImageData(ByteBuffer.wrap(data), width, height, ImageFormat.NV16).build()
            val barCodes = barcodeDetector.detect(frame)
            if (barCodes.size() > 0) {
                val barcode = barCodes.valueAt(0)
                val barcodeFormat = GVFormatUtil.conversion(barcode.format)
                scanResultList.add(ScanResultModel(System.currentTimeMillis(), barcodeFormat, barcode.rawValue).also {
                    it.boundingBox = barcode.boundingBox
                    it.data = data
                })
            }
        } catch (e: Exception) {
            throw e
        }
        return scanResultList
    }

    @Throws(Exception::class)
    fun decodeImage(bitmap: Bitmap): ArrayList<ScanResultModel> {
        val scanResultList: ArrayList<ScanResultModel> = arrayListOf()
        try {
            val frame = Frame.Builder().setBitmap(bitmap).build()
            val barCodes = barcodeDetector.detect(frame)
            if (barCodes.size() > 0) {
                for (i in 0 until barCodes.size()) {
                    val barcode = barCodes.valueAt(i)
                    val barcodeFormat = GVFormatUtil.conversion(barcode.format)
                    scanResultList.add(ScanResultModel(System.currentTimeMillis(), barcodeFormat, barcode.rawValue).also { it.boundingBox = barcode.boundingBox })
                }
            }
        } catch (e: Exception) {
            throw e
        }
        return scanResultList
    }

}