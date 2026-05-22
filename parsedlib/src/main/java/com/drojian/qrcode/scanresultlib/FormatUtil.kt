package com.drojian.qrcode.scanresultlib

import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

fun ParsedFormat.getParsedFormatImage() = when (this) {
    ParsedFormat.ADDRESSBOOK -> R.drawable.parse_format_icon_addressbook
    ParsedFormat.BarCode -> R.drawable.parse_format_icon_barcode
    ParsedFormat.CALENDAR -> R.drawable.parse_format_icon_calendar
    ParsedFormat.EMAIL_ADDRESS -> R.drawable.parse_format_icon_emailaddress
    ParsedFormat.FACEBOOK -> R.drawable.parse_format_icon_facebook
    ParsedFormat.GEO -> R.drawable.parse_format_icon_geo
    ParsedFormat.INSTAGRAM -> R.drawable.parse_format_icon_instagram
    ParsedFormat.ISBN -> R.drawable.parse_format_icon_isbn
    ParsedFormat.PAYPAL -> R.drawable.parse_format_icon_paypal
    ParsedFormat.PRODUCT -> R.drawable.parse_format_icon_product
    ParsedFormat.SMS -> R.drawable.parse_format_icon_sms
    ParsedFormat.SPOTIFY -> R.drawable.parse_format_icon_spotify
    ParsedFormat.TEL -> R.drawable.parse_format_icon_tel
    ParsedFormat.TEXT -> R.drawable.parse_format_icon_text
    ParsedFormat.TWITTER -> R.drawable.parse_format_icon_twitter
    ParsedFormat.URI -> R.drawable.parse_format_icon_uri
    ParsedFormat.VIBER -> R.drawable.parse_format_icon_viber
    ParsedFormat.VIN -> R.drawable.parse_format_icon_vin
    ParsedFormat.WHATSAPP -> R.drawable.parse_format_icon_whatsapp
    ParsedFormat.WIFI -> R.drawable.parse_format_icon_wifi
    ParsedFormat.YOUTUBE -> R.drawable.parse_format_icon_youtube
}

fun ParsedFormat.getParsedFormatName() = when (this) {
    ParsedFormat.ADDRESSBOOK -> R.string.parse_format_title_address_book
    ParsedFormat.BarCode -> R.string.parse_format_title_barcode
    ParsedFormat.CALENDAR -> R.string.parse_format_title_calendar
    ParsedFormat.EMAIL_ADDRESS -> R.string.parse_format_title_email_address
    ParsedFormat.GEO -> R.string.parse_format_title_geo
    ParsedFormat.ISBN -> R.string.parse_format_title_isbn
    ParsedFormat.PRODUCT -> R.string.parse_format_title_product
    ParsedFormat.SMS -> R.string.parse_format_title_sms
    ParsedFormat.TEL -> R.string.parse_format_title_tel
    ParsedFormat.TEXT -> R.string.parse_format_title_text
    ParsedFormat.URI -> R.string.parse_format_title_uri
    ParsedFormat.VIN -> R.string.parse_format_title_vin
    ParsedFormat.WIFI -> R.string.parse_format_title_wifi

    ParsedFormat.VIBER -> R.string.parse_format_title_viber
    ParsedFormat.PAYPAL -> R.string.parse_format_title_paypal
    ParsedFormat.SPOTIFY -> R.string.parse_format_title_spotify
    ParsedFormat.TWITTER -> R.string.parse_format_title_twitter
    ParsedFormat.YOUTUBE -> R.string.parse_format_title_youtube
    ParsedFormat.FACEBOOK -> R.string.parse_format_title_facebook
    ParsedFormat.WHATSAPP -> R.string.parse_format_title_whatsapp
    ParsedFormat.INSTAGRAM -> R.string.parse_format_title_instagram
}