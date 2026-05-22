package com.drojian.qrcode.createlib

import android.graphics.Bitmap
import com.drojian.qrcode.baselib.CodeFormat
import com.drojian.qrcode.createlib.create.CreateResultModel
import com.drojian.qrcode.zxinglib.ZXingFormatUtil.conversion
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import java.nio.charset.StandardCharsets

object CreatorHelper {

    @JvmStatic
    @Throws(Exception::class)
    fun getBitmap(createResultModel: CreateResultModel, createConfig: CreatorConfig): Bitmap? {
        val bitmap: Bitmap?
        try {
            val createString = try {
                String(createResultModel.result.toByteArray(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1)
            } catch (e: Exception) {
                createResultModel.result
            }

            if (createConfig.resizeByCodeFormat) {
                createConfig.height = if (isSquareBarcode(createConfig.codeFormat)) createConfig.width else createConfig.width / 3
            }
            createConfig.hints?.set(EncodeHintType.MARGIN, "0")

            val matrix = MultiFormatWriter().encode(createString, conversion(createConfig.codeFormat), createConfig.width, createConfig.height, createConfig.hints)
            val pixels = IntArray(createConfig.width * createConfig.height)
            if (matrix != null) {
                for (y in 0 until createConfig.height) {
                    for (x in 0 until createConfig.width) {
                        if (matrix[x, y]) {
                            pixels[y * createConfig.width + x] = -0x1000000 //二维码颜色
                        } else {
                            pixels[y * createConfig.width + x] = -0x1 //背景颜色
                        }
                    }
                }
            }
            bitmap = Bitmap.createBitmap(createConfig.width, createConfig.height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, createConfig.width, 0, 0, createConfig.width, createConfig.height)
        } catch (e: Throwable) {
            throw e
        }
        return bitmap
    }

    @JvmStatic
    fun isSquareBarcode(codeFormat: CodeFormat?): Boolean {
        if (codeFormat == null) {
            return false
        }
        return when (codeFormat) {
            CodeFormat.AZTEC, CodeFormat.DATA_MATRIX, CodeFormat.MAXICODE, CodeFormat.QR_CODE -> true
            else -> false
        }
    }
}