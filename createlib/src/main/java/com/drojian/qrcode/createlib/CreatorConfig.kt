package com.drojian.qrcode.createlib

import com.drojian.qrcode.baselib.CodeFormat
import com.drojian.qrcode.createlib.create.CreateFormat
import com.google.zxing.EncodeHintType

data class CreatorConfig(
    var width: Int = 600,
    var height: Int = 600,
    var resizeByCodeFormat: Boolean = true, // 是否根据码格式调整生成的码图片尺寸
    var createFormat: CreateFormat = CreateFormat.Text,
    var codeFormat: CodeFormat = CodeFormat.QR_CODE,
    var hints: MutableMap<EncodeHintType?, String>? = null
)