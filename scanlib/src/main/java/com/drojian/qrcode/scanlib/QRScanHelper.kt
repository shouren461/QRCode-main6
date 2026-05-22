package com.drojian.qrcode.scanlib

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.ArrayMap
import android.util.Log
import com.drojian.qrcode.baselib.QRLibUtil
import com.drojian.qrcode.baselib.ScanResultModel
import com.drojian.qrcode.gvlib.GVHelper
import com.drojian.qrcode.utillib.UtilHelper
import com.drojian.qrcode.utillib.log.Logcat
import com.drojian.qrcode.zxinglib.ZXingHelper
import kotlinx.coroutines.runBlocking

class QRScanHelper(var qrScanConfig: QRScanConfig = QRScanConfig()) {
    private var zxingHelper: ZXingHelper? = null
    private var gvHelper: GVHelper? = null


    @Throws(Exception::class)
    fun decode(context: Context, data: ByteArray, width: Int, height: Int): ArrayList<ScanResultModel> {
        val resultArrayMap = ArrayMap<String, ScanResultModel>()

        try {
            if (qrScanConfig.enableGV && gvHelper == null) {
                gvHelper = GVHelper(context)
            }
            gvHelper?.decode(data, width, height)?.let {
                addResult(resultArrayMap, it)
                if (it.isNotEmpty()) {
                    if (UtilHelper.isRelease && QRLibUtil.isBundle12H(context)) {
                        Log.e("qr_lib", "GV Scan success. $it")
                    } else {
                        Logcat.e("qr_lib", "GV Scan success. $it")
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        try {
            if (qrScanConfig.enableZXING && zxingHelper == null) {
                zxingHelper = ZXingHelper()
            }

            zxingHelper?.decode(data, width, height)?.let {
                addResult(resultArrayMap, it)
                if (UtilHelper.isRelease && QRLibUtil.isBundle12H(context)) {
                    Log.e("qr_lib", "ZXING Scan success. $it")
                } else {
                    Logcat.e("qr_lib", "ZXING Scan success. $it")
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return ArrayList(resultArrayMap.values)
    }

    @Throws(Exception::class)
    fun decodeImage(context: Context, bitmap: Bitmap): ArrayList<ScanResultModel> {
        val resultArrayMap = ArrayMap<String, ScanResultModel>()

        try {
            if (qrScanConfig.enableGV && gvHelper == null) {
                gvHelper = GVHelper(context)
            }
            gvHelper?.decodeImage(bitmap)?.let {
                addResult(resultArrayMap, it)
                if (it.isNotEmpty()) {
                    if (UtilHelper.isRelease && QRLibUtil.isBundle12H(context)) {
                        Log.e("qr_lib", "GV Scan success. $it")
                    } else {
                        Logcat.e("qr_lib", "GV Scan success. $it")
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        try {
            if (qrScanConfig.enableZXING && zxingHelper == null) {
                zxingHelper = ZXingHelper()
            }
            zxingHelper?.decodeBitmap(bitmap)?.let {
                addResult(resultArrayMap, it)
                if (UtilHelper.isRelease && QRLibUtil.isBundle12H(context)) {
                    Log.e("qr_lib", "ZXING Scan success. $it")
                } else {
                    Logcat.e("qr_lib", "ZXING Scan success. $it")
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        try {
            bitmap.recycle()
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return ArrayList(resultArrayMap.values)
    }


    private fun addResult(scanResultArrayMap: ArrayMap<String, ScanResultModel>, newScanResult: ScanResultModel) {
        val key = newScanResult.text
        val existResult = scanResultArrayMap[key]
        if (existResult == null || !isSameResult(existResult, newScanResult)) {
            //不存在此结果
            scanResultArrayMap[key] = newScanResult
        }
    }

    private fun addResult(scanResultArrayMap: ArrayMap<String, ScanResultModel>, scanResultList: ArrayList<ScanResultModel>) {
        for (result in scanResultList) {
            addResult(scanResultArrayMap, result)
        }
    }

    private fun isSameResult(scanResult1: ScanResultModel, scanResult2: ScanResultModel): Boolean {
        return TextUtils.equals(scanResult1.text, scanResult2.text) && scanResult1.codeFormat == scanResult2.codeFormat
    }
}