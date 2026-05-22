package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

data class CreateInstagramModel(var link: String = "") : CreateResultModel(CreateFormat.Instagram) {

    override fun formatResult() {
        super.result = if (CreateUtil.isInstagramLink(link)) {
            link.trim()
        } else {
            "instagram://user?username=$link"
        }
    }
}