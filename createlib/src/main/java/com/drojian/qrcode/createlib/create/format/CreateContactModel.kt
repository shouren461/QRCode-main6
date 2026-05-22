package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

data class CreateContactModel(var name: String = "", var tel: String = "", var email: String = "") : CreateResultModel(CreateFormat.Contact) {

    override fun formatResult() {
        super.result = "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "N:" + CreateUtil.removeNewLine(name) + "\n" +
                "FN:" + CreateUtil.removeNewLine(name) + "\n" +
                "TEL;HOME;VOICE:" + CreateUtil.removeNewLine(tel) + "\n" +
                "EMAIL;PREF;INTERNET:" + CreateUtil.removeNewLine(email) + "\n" +
                "END:VCARD"
    }

}