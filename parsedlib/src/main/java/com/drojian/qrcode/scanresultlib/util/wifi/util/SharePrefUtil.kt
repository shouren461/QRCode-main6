package com.drojian.qrcode.scanresultlib.util.wifi.util

import android.content.Context
import android.content.SharedPreferences

/**
 * @author yangfengfan 2020-12-28
 */
internal class SharePrefUtil private constructor(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("qr_handler_lib_prefs", Context.MODE_PRIVATE)

    fun setPref(key: String?, value: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun setPref(key: String?, value: String?) {
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun setPref(key: String?, value: Float) {
        val editor = prefs.edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun setPref(key: String?, value: Int) {
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun setPref(key: String?, value: Long) {
        val editor = prefs.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getPref(key: String?, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    fun getPref(key: String?, defaultValue: String?): String? {
        return prefs.getString(key, defaultValue)
    }

    fun getPref(key: String?, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    fun getPref(key: String?, defaultValue: Long): Long {
        return prefs.getLong(key, defaultValue)
    }

    fun getPref(key: String?, defaultValue: Float): Float {
        return prefs.getFloat(key, defaultValue)
    }

    fun hasPrefWithKey(key: String?): Boolean {
        return prefs.contains(key)
    }

    fun removePref(key: String?): Boolean {
        val editor = prefs.edit()
        editor.remove(key)
        return editor.commit()
    }

    companion object {
        private var mInstance: SharePrefUtil? = null

        @JvmStatic
        fun getInstance(context: Context): SharePrefUtil? {
            if (mInstance == null) {
                mInstance = SharePrefUtil(context)
            }
            return mInstance
        }

    }

}