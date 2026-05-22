package com.drojian.qrcode.utillib.log

import android.util.Log
import com.drojian.qrcode.utillib.UtilHelper

object Logcat {
    private const val TAG = "Logcat"
    private const val LOG_LENGTH = 4000
    @JvmStatic
    fun d(msg: String) = printLog(DEBUG, TAG, msg)

    @JvmStatic
    fun d(tag: String? = TAG, msg: String) = printLog(DEBUG, tag, msg)


    @JvmStatic
    fun e(msg: String) =printLog(ERROR, TAG, msg)

    @JvmStatic
    fun e(tag: String? = TAG, msg: String) = printLog(ERROR, tag, msg)


    private fun printLog(priority: Int, tag: String? = TAG, msg: String) {
        if (UtilHelper.isRelease) {
            return
        }
        try {
            when (priority) {
                DEBUG -> {
                    if (msg.length > LOG_LENGTH) {
                        for (i in 1..100) {
                            if (i * LOG_LENGTH > msg.length) {
                                Log.d(tag, createLog(Throwable(), msg.substring((i - 1) * LOG_LENGTH, msg.length)))
                                break
                            } else {
                                Log.d(tag, createLog(Throwable(), msg.substring((i - 1) * LOG_LENGTH, i * LOG_LENGTH)))
                            }
                        }
                    } else {
                        Log.e(tag, createLog(Throwable(), msg))
                    }
                }
                else -> {
                    if (msg.length > LOG_LENGTH) {
                        for (i in 1..100) {
                            if (i * LOG_LENGTH > msg.length) {
                                Log.e(tag, createLog(Throwable(), msg.substring((i - 1) * LOG_LENGTH, msg.length)))
                                break
                            } else {
                                Log.e(tag, createLog(Throwable(), msg.substring((i - 1) * LOG_LENGTH, i * LOG_LENGTH)))
                            }
                        }
                    } else {
                        Log.e(tag, createLog(Throwable(), msg))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun createLog(throwable: Throwable, msg: String): String {
        try {
            val elements = throwable.stackTrace
            val className = elements[2].fileName
            val methodName = elements[2].methodName
            val lineNumber = elements[2].lineNumber
            return "$methodName($className:$lineNumber)$msg"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private const val VERBOSE = 2
    private const val DEBUG = 3
    private const val INFO = 4
    private const val WARN = 5
    private const val ERROR = 6
    private const val ASSERT = 7
}