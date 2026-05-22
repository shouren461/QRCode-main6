package com.drojian.qrcode.scanresultlib.handler

import android.app.Activity
import com.drojian.qrcode.scanlib.scan.parse.format.ParseCalendarModel
import com.drojian.qrcode.scanresultlib.BaseResultHandler
import com.drojian.qrcode.scanresultlib.R
import com.drojian.qrcode.scanresultlib.ResultHandlerConfig
import com.drojian.qrcode.scanresultlib.button.ParseAction
import com.drojian.qrcode.scanresultlib.util.HandlerUtils
import java.text.DateFormat

/**
 * @author yangfengfan 2020-10-19
 */
class CalendarHandler(activity: Activity, val parsedModel: ParseCalendarModel, resultHandlerConfig: ResultHandlerConfig) : BaseResultHandler(activity, parsedModel, resultHandlerConfig) {

    override val parseActionList: Array<ParseAction> = arrayOf(
        ParseAction.ADD_EVENT,
        ParseAction.SEND_EMAIL,
        ParseAction.COPY,
        ParseAction.SHARE,
    )


    override fun handleButtonPress(action: ParseAction) {
        when (action) {
            ParseAction.ADD_EVENT -> {
                var description = parsedModel.description
                parsedModel.organizer?.let {
                    description = if (description.isNullOrEmpty()) {
                        it
                    } else {
                        "$description$it".trimIndent()
                    }
                }
                HandlerUtils.addCalendarEvent(
                    activity,
                    parsedModel.summary,
                    parsedModel.start,
                    parsedModel.startAllDay,
                    parsedModel.end,
                    parsedModel.location,
                    description,
                    parsedModel.attendees
                )
            }
            ParseAction.SEND_EMAIL  -> {
                HandlerUtils.sendEmail(activity, null, null, null, parsedModel.summary, generateCopyText(getDisplayList()))
            }
            else -> super.handleButtonPress(action)
        }
    }

    override fun getDisplayList(): List<SimpleResult> {
        val simpleResultList: MutableList<SimpleResult> = ArrayList()
        parsedModel.summary?.let {
            if (it.isNotEmpty()) {
                simpleResultList.add(SimpleResult(R.string.parse_result_content_summary, it))
            }
        }
        format(parsedModel.startAllDay, parsedModel.start)?.let {
            if (it.isNotEmpty()) {
                simpleResultList.add(SimpleResult(R.string.parse_result_content_start, it))
            }
        }
        if (parsedModel.end > 0L) {
            format(parsedModel.endAllDay, parsedModel.end)?.let {
                if (it.isNotEmpty()) {
                    simpleResultList.add(SimpleResult(R.string.parse_result_content_end, it))
                }
            }
        }
        parsedModel.description?.let {
            if (it.isNotEmpty()) {
                simpleResultList.add(SimpleResult(R.string.parse_result_content_note, it))
            }
        }
        parsedModel.location?.let {
            if (it.isNotEmpty()) {
                simpleResultList.add(SimpleResult(R.string.parse_result_content_address, it))
            }
        }
        return simpleResultList
    }

    private fun format(allDay: Boolean, date: Long): String? {
        if (date < 0L) {
            return null
        }
        val format = if (allDay)
            DateFormat.getDateInstance(DateFormat.MEDIUM)
        else
            DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
        return format.format(date)
    }
}