package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

data class CreateTelModel(var tel: String = "") : CreateResultModel(CreateFormat.Tel) {

    override fun formatResult() {
        super.result = "tel:$tel"
    }
}