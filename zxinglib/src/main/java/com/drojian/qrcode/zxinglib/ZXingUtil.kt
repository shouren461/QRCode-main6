package com.drojian.qrcode.zxinglib

import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.Result
import java.util.EnumMap
import java.util.EnumSet

/**
 * @author yangfengfan 2020-08-26
 */
object ZXingUtil {

    private val productFormats: Set<com.google.zxing.BarcodeFormat> = EnumSet.of(
        BarcodeFormat.UPC_A,
        BarcodeFormat.UPC_E,
        BarcodeFormat.EAN_13,
        BarcodeFormat.EAN_8,
        BarcodeFormat.RSS_14,
        BarcodeFormat.RSS_EXPANDED
    )

    private val industrialFormats: Set<BarcodeFormat> = EnumSet.of(
        BarcodeFormat.CODE_39,
        BarcodeFormat.CODE_93,
        BarcodeFormat.CODE_128,
        BarcodeFormat.ITF,
        BarcodeFormat.CODABAR
    )

    private val qrCodeFormat: Set<BarcodeFormat> = EnumSet.of(BarcodeFormat.QR_CODE)

    private val oneDFormats: Set<BarcodeFormat> = EnumSet.copyOf(productFormats)

    private val dateMatrixFormat: Set<BarcodeFormat> = EnumSet.of(BarcodeFormat.DATA_MATRIX)

    private val aztecFormat: Set<BarcodeFormat> = EnumSet.of(BarcodeFormat.AZTEC)

    private val pdf417Format: Set<BarcodeFormat> = EnumSet.of(BarcodeFormat.PDF_417)

    fun getDecodeHints(): Map<DecodeHintType, Any> {
        val hints: MutableMap<DecodeHintType, Any> = EnumMap(DecodeHintType::class.java)
        val decodeFormats: MutableCollection<BarcodeFormat> = EnumSet.noneOf(BarcodeFormat::class.java)
        decodeFormats.addAll(productFormats)
        decodeFormats.addAll(industrialFormats)
        decodeFormats.addAll(qrCodeFormat)
        decodeFormats.addAll(oneDFormats)
        decodeFormats.addAll(dateMatrixFormat)
        decodeFormats.addAll(aztecFormat)
        decodeFormats.addAll(pdf417Format)
        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        return hints
    }

    /**
     * Result 转 ResultModel
     */
    @JvmStatic
    fun retrofitResult(result: Result?) = if (result == null) result else com.drojian.qrcode.baselib.ScanResultModel(
        System.currentTimeMillis(),
        ZXingFormatUtil.conversion(result.barcodeFormat),
        result.text
    )
}