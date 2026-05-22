package com.drojian.qrcode.utillib.extension

import android.text.TextUtils
import java.util.regex.Pattern

fun String.isEmptyOrBlack() = isEmpty() || isBlank()


object StringEXT {

    @JvmStatic
    fun isEmptyOrBlack(value: String): Boolean {
        return if (TextUtils.isEmpty(value)) {
            true
        } else Pattern.compile("\\s*|\t|\r|\n").matcher(value).replaceAll("").isEmpty()
    }
}