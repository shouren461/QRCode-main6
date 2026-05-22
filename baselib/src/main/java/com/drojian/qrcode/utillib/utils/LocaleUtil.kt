package com.drojian.qrcode.utillib.utils

import com.drojian.qrcode.utillib.log.LogHelper.log
import java.util.Locale

object LocaleUtil {

    @JvmStatic
    fun isJapan(): Boolean {
        try {
            val locale = Locale.getDefault()
            val country: String = locale.country
            val language = locale.language

            if (country.isNotEmpty() && country.equals("JP", ignoreCase = true)) {
                return true
            }

            if (language.isNotEmpty() && language.startsWith("ja", ignoreCase = true)) {
                return true
            }

        } catch (e: Exception) {
            e.log()
        }
        return false
    }

    @JvmStatic
    fun isGermany(): Boolean {
        try {
            val locale = Locale.getDefault()
            val country: String = locale.country
            val language = locale.language

            if (country.isNotEmpty() && country.equals("DE", ignoreCase = true)) {
                return true
            }

            if (language.isNotEmpty() && language.startsWith("de", ignoreCase = true)) {
                return true
            }

        } catch (e: Exception) {
            e.log()
        }
        return false
    }

    @JvmStatic
    fun isIndia(): Boolean {
        try {
            val locale = Locale.getDefault()
            val country: String = locale.country
            val language = locale.language

            if (country.isNotEmpty() && country.equals("IN", ignoreCase = true)) {
                return true
            }

            if (language.isNotEmpty() && language.startsWith("hi", ignoreCase = true)) {
                return true
            }

        } catch (e: Exception) {
            e.log()
        }
        return false
    }

    @JvmStatic
    fun isFrance(): Boolean {
        try {
            val locale = Locale.getDefault()
            val country: String = locale.country
            val language = locale.language

            if (country.isNotEmpty() && country.equals("FR", ignoreCase = true)) {
                return true
            }

            if (language.isNotEmpty() && language.startsWith("FR", ignoreCase = true)) {
                return true
            }

        } catch (e: Exception) {
            e.log()
        }
        return false
    }

}