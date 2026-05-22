package com.drojian.qrcode.utillib.extension

import com.drojian.qrcode.utillib.log.LogHelper.log

object ThrowableEXT {

    @JvmStatic
    fun log(exception: Throwable) {
        exception.log()
    }

}