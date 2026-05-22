package com.drojian.qrcode.utillib.log

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.util.Calendar

object LogFile {
    private const val SPACE_LIMIT = 5000
    private var allowCollectData = false

    fun setIsCollectUserData(isCollectUserData: Boolean) {
        LogFile.allowCollectData = isCollectUserData
    }

    fun logToFile(context: Context, content: String) = GlobalScope.launch(Dispatchers.IO) {
        if (!allowCollectData) {
            return@launch
        }
        logToFileForce(context, content)
    }


    fun logToFileForce(context: Context, content: String) = GlobalScope.launch(Dispatchers.IO) {
        var fileOutputStream: FileOutputStream? = null
        try {
            val logFile = getLogFile(context)
            if (logFile.exists()) {
                fileOutputStream = FileOutputStream(logFile, true)
                val contentString = "${getCurTimeString()} $content\n"
                fileOutputStream.write(contentString.toByteArray())
                fileOutputStream.flush()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            fileOutputStream?.close()
        }
    }


    fun getLogFile(context: Context): File {
        try {
            checkLogFileSize(context)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return File(getLogFileDirPath(context) + "/crash.log")
    }

    private fun getLogFileDirPath(context: Context): String {
        val file = File(context.applicationContext.cacheDir.absolutePath + "/crash/")

        if (!file.exists()) {
            try {
                file.mkdirs()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return file.absolutePath
    }

    private fun checkLogFileSize(context: Context) {
        val logFile = File(getLogFileDirPath(context) + "/crash.log")
        if (logFile.exists()) {
            var fileInputStream: FileInputStream? = null
            try {
                fileInputStream = FileInputStream(logFile)
                val size = fileInputStream.available()
                if (size / 1024 > SPACE_LIMIT) {
                    logFile.delete()
                    logFile.createNewFile()
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            } finally {
                fileInputStream?.close()
            }
        } else {
            logFile.createNewFile()
        }
    }

    private fun getCurTimeString(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)} ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${calendar.get(Calendar.SECOND)}"
    }

    private fun clearLog(context: Context) {
        try {
            val logFile = getLogFile(context)
            logFile.delete()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    suspend fun getLogString(context: Context): String = withContext(Dispatchers.IO) {
        val logFile = getLogFile(context)
        val logStringBuilder = StringBuilder()
        var bufferedReader: BufferedReader? = null
        try {
            bufferedReader = BufferedReader(FileReader(logFile))
            var tempString = bufferedReader.readLine()
            while (tempString != null) {
                logStringBuilder.append(tempString).append("\n")
                tempString = bufferedReader.readLine()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
        return@withContext logStringBuilder.toString()
    }
}