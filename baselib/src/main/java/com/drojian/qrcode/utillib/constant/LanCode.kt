package com.drojian.qrcode.utillib.constant

import android.content.Context
import com.drojian.qrcode.utillib.extension.getSystemLocale
import java.util.Locale

enum class LanCode(val localName: String) {
    EN("English"),                 //en        英语
    AR("العربية"),                 //ar        阿拉伯语
    BN("বাংলা"),                    //bn        孟加拉语
    DE("Deutsch"),                 //de        德语
    ES("Español"),                 //es        西班牙语
    FA("فارسی"),                   //fa         波斯语
    FR("Français"),                //fr        法语
    HI("हिंदी"),                     //hi        印地语
    IN("Indonesia"),               //in        印尼语
    IT("Italiano"),                //it        意大利语
    JA("日本語"),                   //ja        日语
    KO("한국어"),                    //ko        韩语
    MS("Melayu"),                  //ms        马来语
    NL("Nederlands"),              //nl        荷兰语
    PL("Polski"),                  //pl         波兰语
    PT("Português"),               //pt        葡萄牙语
    RU("Русский"),                 //ru        俄语
    SV("Svenska"),                 //sv        瑞典语
    TH("ภาษาไทย"),                 //th        泰语
    TR("Türkçe"),                  //tr        土耳其语
    UK("Українська"),             //uk        乌克兰语
    VI("Tiếng Việt"),                    //vi        越南语
    ZH_CN("简体中文"),              //zh-rCN    中文简体
    ZH_TW("繁體中文"),              //zh-rTW    中文繁体
}

fun getSystemLanCode(): LanCode {
    val lan = Locale.getDefault().language.lowercase()
    val country = Locale.getDefault().country.lowercase()
    return when (lan) {
        "en" -> LanCode.EN
        "ar" -> LanCode.AR
        "bn" -> LanCode.BN
        "de" -> LanCode.DE
        "es" -> LanCode.ES
        "fa" -> LanCode.FA
        "fr" -> LanCode.FR
        "hi" -> LanCode.HI
        "in" -> LanCode.IN
        "it" -> LanCode.IT
        "ja" -> LanCode.JA
        "ko" -> LanCode.KO
        "ms" -> LanCode.MS
        "nl" -> LanCode.NL
        "pl" -> LanCode.PL
        "pt" -> LanCode.PT
        "ru" -> LanCode.RU
        "sv" -> LanCode.SV
        "th" -> LanCode.TH
        "tr" -> LanCode.TR
        "uk" -> LanCode.UK
        "vi" -> LanCode.VI
        "zh" -> if (country == "cn") {
            LanCode.ZH_CN
        } else {
            LanCode.ZH_TW
        }
        else -> LanCode.EN
    }
}

fun Context.getLanCode(): LanCode {
    val locale = getSystemLocale()
    val lan = locale.language.lowercase(Locale.ROOT)
    val country = locale.country.lowercase(Locale.ROOT)
    return when (lan) {
        "en" -> LanCode.EN
        "ar" -> LanCode.AR
        "bn" -> LanCode.BN
        "de" -> LanCode.DE
        "es" -> LanCode.ES
        "fa" -> LanCode.FA
        "fr" -> LanCode.FR
        "hi" -> LanCode.HI
        "in" -> LanCode.IN
        "it" -> LanCode.IT
        "ja" -> LanCode.JA
        "ko" -> LanCode.KO
        "ms" -> LanCode.MS
        "nl" -> LanCode.NL
        "pl" -> LanCode.PL
        "pt" -> LanCode.PT
        "ru" -> LanCode.RU
        "sv" -> LanCode.SV
        "th" -> LanCode.TH
        "tr" -> LanCode.TR
        "uk" -> LanCode.UK
        "vi" -> LanCode.VI
        "zh" -> {
            if (country == "cn") {
                LanCode.ZH_CN
            } else {
                LanCode.ZH_TW
            }
        }
        else -> LanCode.EN
    }
}

fun LanCode.toLocal(): Locale = when (this) {
    LanCode.EN -> Locale.ENGLISH
    LanCode.AR -> Locale("ar")
    LanCode.BN -> Locale("bn", "IN")
    LanCode.DE -> Locale.GERMAN
    LanCode.ES -> Locale("es")
    LanCode.FA -> Locale("fa")
    LanCode.FR -> Locale.FRENCH
    LanCode.HI -> Locale("hi")
    LanCode.IN -> Locale("in", "ID")
    LanCode.IT -> Locale.ITALY
    LanCode.JA -> Locale.JAPAN
    LanCode.KO -> Locale.KOREAN
    LanCode.MS -> Locale("ms")
    LanCode.NL -> Locale("nl")
    LanCode.PL -> Locale("pl")
    LanCode.PT -> Locale("pt")
    LanCode.RU -> Locale("ru")
    LanCode.SV -> Locale("sv")
    LanCode.TH -> Locale("th")
    LanCode.TR -> Locale("tr")
    LanCode.UK -> Locale("uk")
    LanCode.VI -> Locale("vi")
    LanCode.ZH_CN -> Locale.SIMPLIFIED_CHINESE
    LanCode.ZH_TW -> Locale.TAIWAN
}