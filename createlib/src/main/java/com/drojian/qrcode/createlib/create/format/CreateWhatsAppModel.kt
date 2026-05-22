package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

data class CreateWhatsAppModel(var phone: String = "") : CreateResultModel(CreateFormat.Whatsapp) {

    override fun formatResult() {
        super.result = "whatsapp://send?phone=$phone"
    }
}