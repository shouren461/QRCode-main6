package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

data class CreateEmailModel(var to: String = "", var subject: String = "", var body: String = "") : CreateResultModel(CreateFormat.Email) {

    override fun formatResult() {
        super.result = if (CreateUtil.isOnlySpace(body) && CreateUtil.isOnlySpace(subject)) {
            "mailto:$to"
        } else {
            "MATMSG:TO:$to;SUB:$subject;BODY:$body;;"
        }
    }

}