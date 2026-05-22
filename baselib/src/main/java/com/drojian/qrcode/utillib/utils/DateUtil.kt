package com.drojian.qrcode.utillib.utils

import android.text.TextUtils
import com.drojian.qrcode.utillib.log.LogHelper.log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

object DateUtil {

    fun getDateWithTimeZone() = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT) + " " + getDate() + " " + getTime()

    /**
     * 判断是否是今天
     */
    fun isToday(date: Date): Boolean {
        val todayCal = Calendar.getInstance()
        val dateCal = Calendar.getInstance()

        todayCal.time = Date()
        dateCal.time = date

        return isSameDay(todayCal, dateCal)
    }

    /**
     * 判断date和当前日期是否在同一周内
     */
    fun isSameWeekWithToday(date: Date): Boolean {
        val todayCal = Calendar.getInstance()
        val dateCal = Calendar.getInstance()

        todayCal.time = Date()
        dateCal.time = date

        return isSameWeek(todayCal, dateCal)
    }

    fun isSameYear(c1: Calendar?, c2: Calendar?) = c1?.get(Calendar.YEAR) == c2?.get(Calendar.YEAR)
    fun isSameWeek(c1: Calendar?, c2: Calendar?) = c1?.get(Calendar.WEEK_OF_YEAR) == c2?.get(Calendar.WEEK_OF_YEAR)
    fun isSameDay(c1: Calendar?, c2: Calendar?) = c1?.get(Calendar.DAY_OF_YEAR) == c2?.get(Calendar.DAY_OF_YEAR)

    //仅判断了月份和日
    fun isSameDay(timeStamp1: Long, timeStamp2: Long): Boolean {
        return TextUtils.equals(getTimeOnTimeStamp(timeStamp1), getTimeOnTimeStamp(timeStamp2))
    }

    private fun getTimeOnTimeStamp(timeStamp: Long) = try {
        SimpleDateFormat("MM-dd").format(Date(timeStamp))
    } catch (e: Exception) {
        timeStamp.toString()
    }


    // 获取两个时间戳之间的秒数
    fun getTwoTimestampSecond(timestamp: Long, currentTimestamp: Long = System.currentTimeMillis()) =
        abs(currentTimestamp - timestamp) / 1000

    fun getDate(timestamp: Long = System.currentTimeMillis()): String {
        getFormatDate(timestamp, "yyyy-MM-dd")?.let {
            return it
        }
        var time = ""
        try {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            time = "" + calendar.get(Calendar.YEAR) + ":" + getFormatMonth(calendar.get(Calendar.MONTH) + 1) +
                    ":" + calendar.get(Calendar.DAY_OF_MONTH)
        } catch (e: Exception) {
            e.log()
        }
        return time
    }


    fun getTime(timestamp: Long = System.currentTimeMillis(), pattern: String = "HH:mm"): String {
        getFormatDate(timestamp, pattern)?.let {
            return it
        }
        var time = ""
        try {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            time =
                "" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND)
        } catch (e: Exception) {
            e.log()
        }
        return time
    }


    fun getMonthText(month: Int) =
        "Jan_Feb_Mar_Apr_May_Jun_Jul_Aug_Sep_Oct_Nov_Dec".split("_".toRegex()).toTypedArray()[month]

    // 如果月小于9,前面要带上0. eg: 9 -> 09
    fun getFormatMonth(month: Int) = if (month < 9) "0$month" else month

    // 如果日小于10,前面要带上0. eg: 9 -> 09
    fun getFormatDay(day: Int) = if (day < 10) "0$day" else day

    // 如果时小于10,前面要带上0. eg: 9 -> 09
    fun getFormatHour(hour: Int) = if (hour < 10) "0$hour" else hour

    // 如果分小于10,前面要带上0. eg: 9 -> 09
    fun getFormatMinute(minute: Int) = if (minute < 10) "0$minute" else minute

    // 如果秒小于10,前面要带上0. eg: 9 -> 09
    fun getFormatSecond(second: Int) = if (second < 10) "0$second" else second

    private fun getFormatDate(timestamp: Long, pattern: String?): String? {
        try {
            return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timestamp))
        } catch (e: Exception) {
            e.log()
        }
        return null
    }

    fun isExceedTime(currentTime: Long, lastTime: Long,hours:Int):Boolean{
        val time = currentTime - lastTime
        return time > hours * 60 * 60 * 1000
    }
}