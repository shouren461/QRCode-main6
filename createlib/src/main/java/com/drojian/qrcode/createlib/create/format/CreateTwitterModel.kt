package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

data class CreateTwitterModel(var link: String = "") : CreateResultModel(CreateFormat.Twitter) {

    override fun formatResult() {
        super.result = if (CreateUtil.isTwitterLink(link)) {
            link.trim()
        } else {
            "twitter://user?screen_name=$link"
        }
    }

}