package com.drojian.qrcode.baselib

import android.app.Application
import com.drojian.qrcode.utillib.UtilHelper

abstract class BaseApp: Application() {

    abstract fun isRelease(): Boolean

    override fun onCreate() {
        UtilHelper.init(this, isRelease())
        super.onCreate()
    }
}