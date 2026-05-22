package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

class CreatePaypalModel(var link: String = "") : CreateResultModel(CreateFormat.Paypal) {

    override fun formatResult() {
        super.result = if (CreateUtil.isPaypalyLink(link)) {
            link.trim()
        } else {
            "https://www.paypal.me/" + link.trim()
        }
    }

}