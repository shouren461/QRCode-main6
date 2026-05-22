package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

data class CreateSmsModel(var tel: String = "", var message: String = "") : CreateResultModel(CreateFormat.Sms) {

    override fun formatResult() {
        super.result = "smsto:$tel:$message"
    }
}