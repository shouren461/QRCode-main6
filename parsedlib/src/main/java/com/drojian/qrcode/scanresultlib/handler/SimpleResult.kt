package com.drojian.qrcode.scanresultlib.handler

import androidx.annotation.StringRes

/**
 * @author yangfengfan 2020-10-19
 */
class SimpleResult(@param:StringRes var titleResId: Int, var content: String) {
    companion object {
        const val CONTENT_TYPE_COMMON = 0
        const val CONTENT_TYPE_TEXT = -2
        const val CONTENT_TYPE_NUM = -3
        const val CONTENT_TYPE_URL = -4
        const val CONTENT_TYPE_BARCODE = -5
        const val CONTENT_TYPE_Book = -6
    }
}
