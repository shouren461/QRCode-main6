package com.drojian.qrcode.utillib.log

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics

object LogHelper {

    fun Throwable.log(tag: String = "") {
        this.printStackTrace()
        try {
            when (this) {
                is Error -> Logcat.e("Error : $tag ${this.localizedMessage}")
                is Exception -> Logcat.e("Exception : $tag ${this.localizedMessage}")
                else -> Logcat.e("Throwable : $tag ${this.localizedMessage}")
            }
            firebaseRecord(this)
          //  logFile(.context, this, tag)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    private fun logFile(context: Context, e: Throwable, tag: String) {
        try {
            LogFile.logToFile(context, "$tag ${e.localizedMessage}")
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun firebaseRecord(e: Throwable) {
        try {
            FirebaseCrashlytics.getInstance().recordException(e)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}