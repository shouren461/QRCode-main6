package com.drojian.qrcode.utillib.utils

import android.content.Context
import com.drojian.qrcode.utillib.UtilHelper
import com.drojian.qrcode.utillib.log.LogHelper.log

class SPUtil {
    private var prefs = UtilHelper.context?.getSharedPreferences(UtilHelper.spName, Context.MODE_PRIVATE)
    
    fun set(key: String, any: Any) {
        try {
            when (any) {
                is String -> {
                    prefs?.edit()?.putString(key, any)?.apply()
                }
                is Int -> {
                    prefs?.edit()?.putInt(key, any)?.apply()
                }
                is Boolean -> {
                    prefs?.edit()?.putBoolean(key, any)?.apply()
                }
                is Float -> {
                    prefs?.edit()?.putFloat(key, any)?.apply()
                }
                is Long -> {
                    prefs?.edit()?.putLong(key, any)?.apply()
                }
                else -> {
                    throw IllegalArgumentException("SPUtil can't set illegal type:$any")
                }
            }
        } catch (e: Exception) {
            e.log("SPUtil.set")
        }
    }

    fun get(key: String, defaultValue: Boolean = false) = prefs?.getBoolean(key, defaultValue) ?: defaultValue

    fun get(key: String, defaultValue: Int = 0) = prefs?.getInt(key, defaultValue) ?: defaultValue

    fun get(key: String, defaultValue: Float = 0F) = prefs?.getFloat(key, defaultValue) ?: defaultValue

    fun get(key: String, defaultValue: String = "") = prefs?.getString(key, defaultValue) ?: defaultValue

    fun get(key: String, defaultValue: Long = 0L) = prefs?.getLong(key, defaultValue) ?: defaultValue

    // commit同步提交
    fun commit(key: String, int: Int) {
        try {
            prefs?.edit()?.putInt(key, int)?.commit()
        } catch (e: Exception) {
            e.log()
        }
    }

    fun contains(key: String) = prefs?.contains(key)

    fun remove(key: String) = prefs?.edit()?.remove(key)?.commit()

    fun getPrefs() = prefs

    fun clearData() {
        prefs?.edit()?.clear()?.apply()
    }

    companion object {
        @Volatile
        private var instance: SPUtil? = null

        @JvmStatic
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: SPUtil().also { instance = it }
        }
    }
}