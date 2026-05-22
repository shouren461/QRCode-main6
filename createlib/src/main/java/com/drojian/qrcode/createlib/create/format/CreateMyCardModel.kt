package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

data class CreateMyCardModel(
    var name: String = "",
    var organization: String = "",
    var tel: String = "",
    var email: String = "",
    var birthday: String = "",
    var address: String = "",
    var note: String = "",
) : CreateResultModel(CreateFormat.MyCard) {

    override fun formatResult() {
        super.result = "BEGIN:VCARD\n" +
                "VERSION:3.0\n" +
                "N:" + CreateUtil.removeNewLine(name) + "\n" +
                "FN:" + CreateUtil.removeNewLine(name) + "\n" +
                "ORG:" + CreateUtil.removeNewLine(organization) + "\n" +
                "TEL;HOME;VOICE:" + CreateUtil.removeNewLine(tel) + "\n" +
                "ADR;WORK:;;" + CreateUtil.removeNewLine(address) + "\n" +
                "EMAIL;PREF;INTERNET:" + CreateUtil.removeNewLine(email) + "\n" +
                "BDAY:" + CreateUtil.removeNewLine(birthday) + "\n" +
                "NOTE:" + CreateUtil.removeNewLine(note) + "\n" +
                "END:VCARD"
    }

}