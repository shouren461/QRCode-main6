package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

data class CreateUrlModel(var url: String = "") : CreateResultModel(CreateFormat.Website) {

    override fun formatResult() {
        super.result = url
    }

}