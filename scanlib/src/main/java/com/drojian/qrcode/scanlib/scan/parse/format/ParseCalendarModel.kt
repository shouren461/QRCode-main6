package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * @author yangfengfan 2020-08-27
 *
 * 日历解析结果
 */
data class ParseCalendarModel(
    val summary: String? = null,
    val startString: String? = null,
    val endString: String? = null,
    val durationString: String? = null,
    val location: String? = null,
    val organizer: String? = null,
    val attendees: Array<String>? = arrayOf(),
    val description: String? = null,
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
) : BaseParseModel(ParsedFormat.CALENDAR) {

    private val dateTime = Pattern.compile("[0-9]{8}(T[0-9]{6}Z?)?")
    private val rfc2445Duration = Pattern.compile("P(?:(\\d+)W)?(?:(\\d+)D)?(?:T(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?)?")
    private val rfc2445DurationFieldUnits = longArrayOf(
        7 * 24 * 60 * 60 * 1000L,  // 1 week
        24 * 60 * 60 * 1000L,  // 1 day
        60 * 60 * 1000L,  // 1 hour
        60 * 1000L,  // 1 minute
        1000L
    )

    val startAllDay = startString != null && startString.length == 8
    val endAllDay = endString != null && endString.length == 8
    val start = parseDate(startString)
    val end = if (endString == null) {
        val durationMS = parseDurationMS(durationString)
        if (durationMS < 0L) -1L else start + durationMS
    } else {
        parseDate(endString)
    }

    override fun getShowText() = getBestShowContent(summary, description, description, startString)


    /**
     * Parses a string as a date. RFC 2445 allows the start and end fields to be of type DATE (e.g. 20081021)
     * or DATE-TIME (e.g. 20081021T123000 for local time, or 20081021T123000Z for UTC).
     */
    private fun parseDate(dataString: String?): Long {
        try {
            if (dataString == null) {
                throw ParseException("dataString can't be null", 0)
            }
            if (!dateTime.matcher(dataString).matches()) {
                throw ParseException(dataString, 0)
            }
            if (dataString.length == 8) {
                // Show only year/month/day
                val format = SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
                // For dates without a time, for purposes of interacting with Android, the resulting timestamp
                // needs to be midnight of that day in GMT. See:
                // http://code.google.com/p/android/issues/detail?id=8330
                format.timeZone = TimeZone.getTimeZone("GMT")
                return format.parse(dataString).time
            }
            // The when string can be local time, or UTC if it ends with a Z
            if (dataString.length == 16 && dataString[15] == 'Z') {
                var milliseconds = parseDateTimeString(dataString.substring(0, 15))
                val calendar = GregorianCalendar()
                // Account for time zone difference
                milliseconds += calendar[Calendar.ZONE_OFFSET].toLong()
                // Might need to correct for daylight savings time, but use target time since
                // now might be in DST but not then, or vice versa
                calendar.time = Date(milliseconds)
                return milliseconds + calendar[Calendar.DST_OFFSET]
            }
            return parseDateTimeString(dataString)

        } catch (e: Exception) {

        }
        return 0
    }

    private fun parseDateTimeString(dateTimeString: String): Long {
        val format = SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ENGLISH)
        return format.parse(dateTimeString).time
    }

    private fun parseDurationMS(durationString: CharSequence?): Long {
        if (durationString == null) {
            return -1L
        }
        val m = rfc2445Duration.matcher(durationString)
        if (!m.matches()) {
            return -1L
        }
        var durationMS = 0L
        for (i in rfc2445DurationFieldUnits.indices) {
            val fieldValue: String = m.group(i + 1)
            durationMS += rfc2445DurationFieldUnits[i] * fieldValue.toInt()
        }
        return durationMS
    }

}