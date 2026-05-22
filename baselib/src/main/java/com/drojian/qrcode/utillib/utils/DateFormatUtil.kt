package com.drojian.qrcode.utillib.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * d               月中的某一天。一位数的日期没有前导零。
 * dd             月中的某一天。一位数的日期有一个前导零。
 * ddd           周中某天的缩写名称，在   AbbreviatedDayNames   中定义。
 * dddd         周中某天的完整名称，在   DayNames   中定义。
 * M               月份数字。一位数的月份没有前导零。
 * MM             月份数字。一位数的月份有一个前导零。
 * MMM           月份的缩写名称，在   AbbreviatedMonthNames   中定义。
 * MMMM         月份的完整名称，在   MonthNames   中定义。
 * y               不包含纪元的年份。不具有前导零。
 * yy             不包含纪元的年份。具有前导零。
 * yyyy         包括纪元的四位数的年份。
 * gg             时期或纪元。
 * h               12   小时制的小时。一位数的小时数没有前导零。
 * hh             12   小时制的小时。一位数的小时数有前导零。
 * H               24   小时制的小时。一位数的小时数没有前导零。
 * HH             24   小时制的小时。一位数的小时数有前导零。
 * m               分钟。一位数的分钟数没有前导零。
 * mm             分钟。一位数的分钟数有一个前导零。
 * s               秒。一位数的秒数没有前导零。
 * ss             秒。一位数的秒数有一个前导零。
 * f               秒的小数精度为一位。其余数字被截断。
 */
object DateFormatUtil {

    fun formatBatchDate(timeStamp: Long, locale: Locale): String {
        return SimpleDateFormat("HH:mm, MMM dd", locale).format(Date(timeStamp))
    }

    fun formatHistoryDate(timeStamp: Long,locale: Locale): String {
        return SimpleDateFormat("HH:mm", locale).format(Date(timeStamp))
    }
}