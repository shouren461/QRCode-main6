package com.drojian.qrcode.utillib.utils

import android.util.Patterns

object URLUtil {

    @JvmStatic
    fun isViberLink(value: String?): Boolean {
        return value?.startsWith("viber://add?number=+", true) == true
    }

    @JvmStatic
    fun isSpotifyLink(value: String?): Boolean {
        return value?.startsWith("spotify:search", true) == true
    }

    @JvmStatic
    fun isHttpUrl(urls: String): Boolean {
        return Patterns.WEB_URL.matcher(urls).matches()
    }

    @JvmStatic
    fun isGoogleDocUrl(url: String?): Boolean {
        return url?.startsWith("https://docs.google.com/", true) == true
    }

    @JvmStatic
    fun isGooglePlayUrl(url: String?): Boolean {
        if (url?.startsWith("http://play.google.com/store/apps/details?id=", true) == true) {
            return true
        }
        return url?.startsWith("https://play.google.com/store/apps/details?id=", true) == true
    }

    @JvmStatic
    fun isOfficeUrl(url: String?): Boolean {
        return url?.startsWith("https://onedrive.live.com/", true) == true || url?.startsWith("https://1drv.ms/", true) == true
    }


    @JvmStatic
    fun isLineUrl(url: String?): Boolean {
        if (url?.startsWith("http://line.naver.jp/", true) == true) {
            return true
        }
        if (url?.startsWith("http://line.me/", true) == true) {
            return true
        }
        return url?.startsWith("https://line.me/", true) == true
    }

    @JvmStatic
    fun isWhatsappUrl(url: String?): Boolean {
        if (url?.startsWith("whatsapp://send?", true) == true) {
            return true
        }
        return url?.startsWith("https://wa.me/", true) == true
    }

    @JvmStatic
    fun isYoutubeLink(values: String?): Boolean {
        return values?.contains("youtube.com") == true || values?.contains("youtu.be") == true
    }

    @JvmStatic
    fun isTwitterLink(value: String?): Boolean {
        if (value?.startsWith("twitter://user?", true) == true) {
            return true
        }
        return value?.contains("twitter.com", true) == true
    }

    @JvmStatic
    fun isPaypalLink(value: String?): Boolean {
        return value?.contains("paypal.me", true) == true
    }

    @JvmStatic
    fun isInstagramLink(value: String?): Boolean {
        if (value?.startsWith("instagram://user?username=", true) == true) {
            return true
        }
        return value?.contains("instagram.com", true) == true
    }

    @JvmStatic
    fun isFacebookLink(value: String?): Boolean {
        if (value?.startsWith("fb://profile/") == true) {
            return true
        }
        return value?.contains("facebook.com", true) == true
    }
}