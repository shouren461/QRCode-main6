package com.drojian.qrcode.utillib.analytics

import android.content.Context
import android.os.Bundle
import com.drojian.qrcode.utillib.UtilHelper
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.utillib.log.Logcat
import com.google.firebase.analytics.FirebaseAnalytics

open class BaseAnalytics() {
    private val CUSTOM_DEFINITION = "action"
    private var context: Context? = null

    /**
     * 废弃，后续使用 logCommonEvent(eventName,eventValue)
     */
    @Deprecated(
        "This function is deprecated. Use the logCommonEvent(eventName,eventValue) instead.",
        ReplaceWith("BaseAnalytics().logCommonEvent(eventName,eventValue)")
    )
    fun logSelectContent(content_type: String, item_id: String) = Bundle().also {
        logCommonEvent(content_type, item_id)
        if (!UtilHelper.isRelease) {
            return@also
        }
        try {
            it.putString(FirebaseAnalytics.Param.ITEM_ID, item_id)
            it.putString(FirebaseAnalytics.Param.CONTENT_TYPE, content_type)
            getInstance()?.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, it)
            Logcat.e(TAG, it.toString())
        } catch (e: Throwable) {
            e.log()
        }
    }

    /**
     * bind ApplicationContext
     */
    fun bind(context: Context) {
        this.context = context.applicationContext
    }

    fun logCommonEvent(eventName: String, eventValue: String) {
        runCatching {
            logEvent(eventName, eventValue)
        }.onFailure {
            it.log()
        }
    }

    private fun logEvent(eventName: String, eventValue: String) {
        if (context == null) {
            Logcat.e("warn!,the context must not be null!,please call BaseAnalytics#bind first,Because FirebaseAnalytics.getInstance(context) requires Context instance.")
        }
        if (!UtilHelper.isRelease) {
            //debug version
            Logcat.e(TAG, "$eventName : $eventValue")
        } else {
//            Analytics.sendEvent(
//                context,
//                eventName,
//                CUSTOM_DEFINITION,
//                eventValue
//            )
        }
    }

    fun logFirstEvent(firstLevelName: String, key: String, value: String) = Bundle().also {
        if (!UtilHelper.isRelease) {
            return@also
        }
        try {
            it.putString(key, value)
            getInstance()?.logEvent(firstLevelName, it)
        } catch (e: Throwable) {
            e.log()
        }
    }

    companion object {
        private const val TAG = "BaseAnalytics"

        @Volatile
        private var firebaseAnalytics: FirebaseAnalytics? = null

        @JvmStatic
        fun getInstance() = firebaseAnalytics ?: synchronized(this) {
            firebaseAnalytics ?: UtilHelper.context?.let { context ->
                FirebaseAnalytics.getInstance(context).also { firebaseAnalytics = it }
            }
        }
    }
}