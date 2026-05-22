package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

data class CreateClipboardModel(var text: String = "") : CreateResultModel(CreateFormat.Clipboard) {

    override fun formatResult() {
        super.result = text
    }
}