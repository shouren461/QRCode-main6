package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

data class CreateCalendarModel(
    var summary: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var location: String = "",
    var description: String = ""
) : CreateResultModel(CreateFormat.Calendar) {


    override fun formatResult() {
        val start = if (startDate.contains("T")) {
            "DTSTART:"
        } else {
            "DTSTART;VALUE=DATE:"
        }

        val end = if (startDate.contains("T")) {
            "DTEND:"
        } else {
            "DTEND;VALUE=DATE:"
        }

        super.result = "BEGIN:VEVENT\n" +
                "SUMMARY:$summary\n" +
                "$start$startDate\n" +
                "$end$endDate\n" +
                "LOCATION:$location\n" +
                "DESCRIPTION:$description\n" +
                "END:VEVENT\n"
    }

}