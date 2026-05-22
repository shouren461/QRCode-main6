package com.drojian.qrcode.zxinglib

import com.google.zxing.BarcodeFormat


/**
 * @author yangfengfan 2020-08-26
 */
object ZXingFormatUtil {

    @JvmStatic
    fun conversion(format: BarcodeFormat): com.drojian.qrcode.baselib.CodeFormat =
        when (format) {
            BarcodeFormat.AZTEC -> com.drojian.qrcode.baselib.CodeFormat.AZTEC
            BarcodeFormat.CODABAR -> com.drojian.qrcode.baselib.CodeFormat.CODABAR
            BarcodeFormat.CODE_39 -> com.drojian.qrcode.baselib.CodeFormat.CODE_39
            BarcodeFormat.CODE_93 -> com.drojian.qrcode.baselib.CodeFormat.CODE_93
            BarcodeFormat.CODE_128 -> com.drojian.qrcode.baselib.CodeFormat.CODE_128
            BarcodeFormat.DATA_MATRIX -> com.drojian.qrcode.baselib.CodeFormat.DATA_MATRIX
            BarcodeFormat.EAN_8 -> com.drojian.qrcode.baselib.CodeFormat.EAN_8
            BarcodeFormat.EAN_13 -> com.drojian.qrcode.baselib.CodeFormat.EAN_13
            BarcodeFormat.ITF -> com.drojian.qrcode.baselib.CodeFormat.ITF
            BarcodeFormat.MAXICODE -> com.drojian.qrcode.baselib.CodeFormat.MAXICODE
            BarcodeFormat.PDF_417 -> com.drojian.qrcode.baselib.CodeFormat.PDF_417
            BarcodeFormat.QR_CODE -> com.drojian.qrcode.baselib.CodeFormat.QR_CODE
            BarcodeFormat.RSS_14 -> com.drojian.qrcode.baselib.CodeFormat.RSS_14
            BarcodeFormat.RSS_EXPANDED -> com.drojian.qrcode.baselib.CodeFormat.RSS_EXPANDED
            BarcodeFormat.UPC_A -> com.drojian.qrcode.baselib.CodeFormat.UPC_A
            BarcodeFormat.UPC_E -> com.drojian.qrcode.baselib.CodeFormat.UPC_E
            BarcodeFormat.UPC_EAN_EXTENSION -> com.drojian.qrcode.baselib.CodeFormat.UPC_EAN_EXTENSION
            else -> com.drojian.qrcode.baselib.CodeFormat.QR_CODE
        }

    @JvmStatic
    fun conversion(format: com.drojian.qrcode.baselib.CodeFormat): BarcodeFormat =
        when (format) {
            com.drojian.qrcode.baselib.CodeFormat.AZTEC -> BarcodeFormat.AZTEC
            com.drojian.qrcode.baselib.CodeFormat.CODABAR -> BarcodeFormat.CODABAR
            com.drojian.qrcode.baselib.CodeFormat.CODE_39 -> BarcodeFormat.CODE_39
            com.drojian.qrcode.baselib.CodeFormat.CODE_93 -> BarcodeFormat.CODE_93
            com.drojian.qrcode.baselib.CodeFormat.CODE_128 -> BarcodeFormat.CODE_128
            com.drojian.qrcode.baselib.CodeFormat.DATA_MATRIX -> BarcodeFormat.DATA_MATRIX
            com.drojian.qrcode.baselib.CodeFormat.EAN_8 -> BarcodeFormat.EAN_8
            com.drojian.qrcode.baselib.CodeFormat.EAN_13 -> BarcodeFormat.EAN_13
            com.drojian.qrcode.baselib.CodeFormat.ITF -> BarcodeFormat.ITF
            com.drojian.qrcode.baselib.CodeFormat.MAXICODE -> BarcodeFormat.MAXICODE
            com.drojian.qrcode.baselib.CodeFormat.PDF_417 -> BarcodeFormat.PDF_417
            com.drojian.qrcode.baselib.CodeFormat.QR_CODE -> BarcodeFormat.QR_CODE
            com.drojian.qrcode.baselib.CodeFormat.RSS_14 -> BarcodeFormat.RSS_14
            com.drojian.qrcode.baselib.CodeFormat.RSS_EXPANDED -> BarcodeFormat.RSS_EXPANDED
            com.drojian.qrcode.baselib.CodeFormat.UPC_A -> BarcodeFormat.UPC_A
            com.drojian.qrcode.baselib.CodeFormat.UPC_E -> BarcodeFormat.UPC_E
            com.drojian.qrcode.baselib.CodeFormat.UPC_EAN_EXTENSION -> BarcodeFormat.UPC_EAN_EXTENSION
            else -> BarcodeFormat.QR_CODE
        }



}