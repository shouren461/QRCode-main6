package com.drojian.qrcode.utillib.extension

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import java.util.Locale

val Context.isNightMode: Boolean
    get() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }


fun Context.getResColor(@ColorRes id: Int) = ContextCompat.getColor(this, id)


@ColorInt
fun Context.getThemeColor(@AttrRes attribute: Int) = TypedValue().let { theme.resolveAttribute(attribute, it, true); it.data }

@StyleRes
fun Context.getThemeStyle(@AttrRes attribute: Int) = TypedValue().let { theme.resolveAttribute(attribute, it, true); it.resourceId }

fun Context.getSystemLocale(): Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) resources.configuration.locales[0] else resources.configuration.locale
