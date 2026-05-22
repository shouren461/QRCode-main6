package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

data class CreateWifiModel(var ssid: String = "", var pass: String = "", var type: Type = Type.WEP) : CreateResultModel(CreateFormat.WiFi) {
    enum class Type { WEP, WPA, NONE }

    override fun formatResult() {
        super.result = when (type) {
            Type.WEP -> {
                "WIFI:S:$ssid;T:WEP;P:$pass;;"
            }

            Type.WPA -> {
                "WIFI:S:$ssid;T:WPA;P:$pass;;"
            }

            Type.NONE -> {
                "WIFI:S:$ssid;;"
            }
        }
    }

}