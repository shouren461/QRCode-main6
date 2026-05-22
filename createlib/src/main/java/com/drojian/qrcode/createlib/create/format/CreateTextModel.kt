package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

data class CreateTextModel(var text: String = "") : CreateResultModel(CreateFormat.Text) {

    override fun formatResult() {
        super.result = text
    }
}