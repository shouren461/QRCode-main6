package com.drojian.qrcode.gvlib

import com.google.android.gms.vision.barcode.Barcode

object GVFormatUtil {

    fun conversion(format: Int): com.drojian.qrcode.baselib.CodeFormat = when (format) {
        Barcode.CODE_39 -> com.drojian.qrcode.baselib.CodeFormat.CODE_39
        Barcode.CODE_128 -> com.drojian.qrcode.baselib.CodeFormat.CODE_128
        Barcode.CODE_93 -> com.drojian.qrcode.baselib.CodeFormat.CODE_93
        Barcode.CODABAR -> com.drojian.qrcode.baselib.CodeFormat.CODABAR
        Barcode.DATA_MATRIX -> com.drojian.qrcode.baselib.CodeFormat.DATA_MATRIX
        Barcode.EAN_13 -> com.drojian.qrcode.baselib.CodeFormat.EAN_13
        Barcode.EAN_8 -> com.drojian.qrcode.baselib.CodeFormat.EAN_8
        Barcode.ITF -> com.drojian.qrcode.baselib.CodeFormat.ITF
        Barcode.QR_CODE -> com.drojian.qrcode.baselib.CodeFormat.QR_CODE
        Barcode.UPC_A -> com.drojian.qrcode.baselib.CodeFormat.UPC_A
        Barcode.UPC_E -> com.drojian.qrcode.baselib.CodeFormat.UPC_E
        Barcode.PDF417 -> com.drojian.qrcode.baselib.CodeFormat.PDF_417
        Barcode.AZTEC -> com.drojian.qrcode.baselib.CodeFormat.AZTEC
        else -> com.drojian.qrcode.baselib.CodeFormat.QR_CODE
    }
}