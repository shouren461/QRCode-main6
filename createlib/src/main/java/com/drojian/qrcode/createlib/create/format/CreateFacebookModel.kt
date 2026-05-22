package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

data class CreateFacebookModel(var link: String = "") : CreateResultModel(CreateFormat.Facebook) {

    override fun formatResult() {
        super.result = if (CreateUtil.isFacebookLink(link)) {
            link.trim()
        } else {
            "fb://profile/$link"
        }
    }

}