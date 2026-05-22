package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

data class CreateViberModel(var phone: String = "") : CreateResultModel(CreateFormat.Viber) {

    override fun formatResult() {
        super.result = "viber://add?number=$phone"
    }

}