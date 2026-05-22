package com.drojian.qrcode.utillib.utils

import com.drojian.qrcode.utillib.constant.LanCode

object EmojiUtil {

    @JvmStatic
    fun getCountryFlag(lanCode: LanCode) = when (lanCode) {
        LanCode.EN -> "\uD83C\uDDFA\uD83C\uDDF8"
        LanCode.AR -> "\uD83C\uDDF8\uD83C\uDDE6"
        LanCode.BN -> "\uD83C\uDDE7\uD83C\uDDE9\u200B"
        LanCode.DE -> "\uD83C\uDDE9\uD83C\uDDEA"
        LanCode.ES -> "\uD83C\uDDEA\uD83C\uDDF8"
        LanCode.FA -> "\uD83C\uDDEE\uD83C\uDDF7 "
        LanCode.FR -> "\uD83C\uDDEB\uD83C\uDDF7"
        LanCode.HI -> "\u200B\uD83C\uDDEE\uD83C\uDDF3\u200B"
        LanCode.IN -> "\uD83C\uDDEE\uD83C\uDDE9"
        LanCode.IT -> "\uD83C\uDDEE\uD83C\uDDF9"
        LanCode.JA -> "\uD83C\uDDEF\uD83C\uDDF5"
        LanCode.KO -> "\uD83C\uDDF0\uD83C\uDDF7"
        LanCode.MS -> "\u200B\uD83C\uDDF2\uD83C\uDDFE\u200B"
        LanCode.NL -> "\uD83C\uDDF3\uD83C\uDDF1"
        LanCode.PL -> "\uD83C\uDDF5\uD83C\uDDF1"
        LanCode.PT -> "\uD83C\uDDF5\uD83C\uDDF9"
        LanCode.RU -> "\uD83C\uDDF7\uD83C\uDDFA"
        LanCode.SV -> "\uD83C\uDDF8\uD83C\uDDEA"
        LanCode.TH -> "\uD83C\uDDF9\uD83C\uDDED\u200B"
        LanCode.TR -> "\uD83C\uDDF9\uD83C\uDDF7"
        LanCode.UK -> "\uD83C\uDDFA\uD83C\uDDE6\u200B"
        LanCode.VI -> "\uD83C\uDDFB\uD83C\uDDF3\u200B"
        LanCode.ZH_CN -> "\uD83C\uDDE8\uD83C\uDDF3"
        LanCode.ZH_TW -> "\uD83C\uDDE8\uD83C\uDDF3"
    }

    /**
     * 国家代码转国旗emoji
     */
    @JvmStatic
    fun countryCodeToEmoji(code: String?): String {
        // offset between uppercase ascii and regional indicator symbols
        val offset = 127397

        // validate code
        if (code == null || code.length != 2) {
            return ""
        }

        val emojiStr = StringBuilder()
        //loop all characters
        for (element in code) {
            emojiStr.appendCodePoint(element.toInt() + offset)
        }
        return emojiStr.toString()
    }
}