package com.drojian.qrcode.scanresultlib.button

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.drojian.qrcode.scanresultlib.R

/**
 * 扫描结果页功能按钮
 */
enum class ParseAction(@StringRes var buttonTexts: Int, @DrawableRes var buttonIcons: Int) {
    CALL(R.string.parse_action_title_call, R.drawable.parse_action_icon_call),
    ADD_EVENT(R.string.parse_action_title_add_event, R.drawable.parse_action_icon_add_event),
    ADD_CONTACTS(R.string.parse_action_title_add_contacts, R.drawable.parse_action_icon_add_contacts),

    SEND_SMS(R.string.parse_action_title_send_sms, R.drawable.parse_action_icon_send_sms),
    SEND_MMS(R.string.parse_action_title_send_mms, R.drawable.parse_action_icon_send_mms),
    SEND_EMAIL(R.string.parse_action_title_send_email, R.drawable.parse_action_icon_send_email),

    WEB_SEARCH(R.string.parse_action_title_web_search, R.drawable.parse_action_icon_web_search),
    PRODUCT_SEARCH(R.string.parse_action_title_product_search, R.drawable.parse_action_icon_product_search),
    BOOK_SEARCH(R.string.parse_action_title_book_search, R.drawable.parse_action_icon_book_search),

    SHOW_ON_MAP(R.string.parse_action_title_show_on_map, R.drawable.parse_action_icon_show_on_map),
    NAVIGATION(R.string.parse_action_title_navigation, R.drawable.parse_action_icon_navigation),

    HISTORY_PRICE(R.string.parse_action_title_history_price, R.drawable.parse_action_icon_history_price),
    VIEW_SHOP(R.string.parse_action_view_in_store, R.drawable.parse_action_icon_view_shop),
    AMAZON(R.string.parse_action_title_amazon, R.drawable.parse_action_icon_amazon),
    YAHOO(R.string.parse_action_title_yahoo, R.drawable.parse_action_icon_yahoo),
    RAKUTEN(R.string.parse_action_title_rakuten, R.drawable.parse_action_icon_rakuten),
    EBAY(R.string.parse_action_title_ebay, R.drawable.parse_action_icon_ebay),

    CONNECT_TO_NETWORK(R.string.parse_action_title_connect_to_network, R.drawable.parse_action_icon_connect_to_network),
    COPY_PASSWORD(R.string.parse_action_title_copy_password, R.drawable.parse_action_icon_copy_password),

    OPEN(R.string.parse_action_title_open, R.drawable.parse_action_icon_open),
    COPY(R.string.parse_action_title_copy, R.drawable.parse_action_icon_copy),
    SHARE(R.string.parse_action_title_share, R.drawable.parse_action_icon_share),

    //营养分析
    NUTRIENT_ANALYSE(R.string.parse_action_title_nutrient_analyse, R.drawable.parse_action_icon_nutrient_analyse),
    SHOP_NOW(R.string.parse_action_title_shop_now, R.drawable.parse_action_icon_shop_now),

    //QR4 AI扫描
    AI_Amazon(R.string.amazon, R.drawable.ic_result_brand_amazon),
    AI_FLIPKART(R.string.platform_flipkart_gpt, R.drawable.ic_result_brand_flipkart),
    AI_HEPSIBURADA(R.string.platform_hepsiburada_gpt, R.drawable.ic_result_brand_hepsiburada),
    AI_JIJI(R.string.platform_jiji_gpt, R.drawable.ic_result_brand_jiji),
    AI_JUMIA(R.string.platform_jumia_gpt, R.drawable.ic_result_brand_jumia),
    AI_KONGA(R.string.platform_konga_gpt, R.drawable.ic_result_brand_konga),
    AI_LAZADA(R.string.platform_lazada_gpt, R.drawable.ic_result_brand_lazada),
    AI_MEESHO(R.string.platform_meesho_gpt, R.drawable.ic_result_brand_meesho),
    AI_MERCADO(R.string.platform_mercado_libre_gpt, R.drawable.ic_result_brand_mercado),
    AI_MISWAG(R.string.platform_miswag_gpt, R.drawable.ic_result_brand_miswag),
    AI_OTTO(R.string.platform_otto_gpt, R.drawable.ic_result_brand_otto),
    AI_SHOPEE(R.string.shopee, R.drawable.ic_result_brand_shopee),
    AI_TIKTOK(R.string.tiktok, R.drawable.ic_result_brand_tiktok),
    AI_TRENDYOL(R.string.platform_trendyol_gpt, R.drawable.ic_result_brand_trendyol),
    AI_WALMART(R.string.platform_walmart_gpt, R.drawable.ic_result_brand_walmart),
    AI_EBAY(R.string.ebay, R.drawable.ic_result_brand_ebay),
    AI_YAHOO(R.string.platform_yahoo_gpt, R.drawable.ic_result_brand_yahoo),
    AI_RAKUTEN(R.string.platform_rakuten_gpt, R.drawable.ic_result_brand_rakuten),
    AI_OPEN_FOOD(R.string.parse_action_title_nutrient_analyse, R.drawable.ic_result_brand_openfood),
}