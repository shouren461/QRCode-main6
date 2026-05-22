package com.drojian.qrcode.scanresultlib.handler

import android.app.Activity
import com.drojian.qrcode.scanlib.scan.parse.format.ParseAddressBookModel
import com.drojian.qrcode.scanresultlib.BaseResultHandler
import com.drojian.qrcode.scanresultlib.R
import com.drojian.qrcode.scanresultlib.ResultHandlerConfig
import com.drojian.qrcode.scanresultlib.button.ParseAction
import com.drojian.qrcode.scanresultlib.util.HandlerUtils
import com.drojian.qrcode.scanresultlib.util.HandlerUtils.MAX_BUTTON_COUNT
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author yangfengfan 2020-09-03
 */
class AddressBookHandler(activity: Activity, val parsedModel: ParseAddressBookModel, resultHandlerConfig: ResultHandlerConfig) :
    BaseResultHandler(activity, parsedModel, resultHandlerConfig) {

    private var fields: BooleanArray? = null
    private var buttonCount = 0

    private val dateFormats = arrayOf<DateFormat>(
        SimpleDateFormat("yyyyMMdd", Locale.ENGLISH),
        SimpleDateFormat("yyyyMMdd'T'HHmmss", Locale.ENGLISH),
        SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    )

    init {
        for (format in dateFormats) {
            format.isLenient = false
        }
        val addresses = parsedModel.addresses
        val phoneNumbers = parsedModel.phoneNumbers
        val emails = parsedModel.emails
        fields = BooleanArray(MAX_BUTTON_COUNT)
        fields?.let {
            it[0] = true // Add contact is always available
            it[1] = !addresses.isNullOrEmpty() && addresses[0].isNotEmpty()
            it[2] = !phoneNumbers.isNullOrEmpty()
            it[3] = !emails.isNullOrEmpty()
        }
        buttonCount = 0
        fields?.let {
            for (x in 0 until MAX_BUTTON_COUNT) {
                if (it[x]) {
                    buttonCount++
                }
            }
        }
    }

    override val parseActionList: Array<ParseAction> = arrayListOf(ParseAction.ADD_CONTACTS).also {
        if (!parsedModel.addresses.isNullOrEmpty() && !parsedModel.addresses?.get(0).isNullOrEmpty()) {
            it.add(ParseAction.SHOW_ON_MAP)
        }
        if (!parsedModel.phoneNumbers.isNullOrEmpty()) {
            it.add(ParseAction.CALL)
        }
        if (!parsedModel.emails.isNullOrEmpty()) {
            it.add(ParseAction.SEND_EMAIL)
        }
        it.add(ParseAction.COPY)
        it.add(ParseAction.SHARE)
    }.toTypedArray()


    override fun handleButtonPress(action: ParseAction) {
        val addresses = parsedModel.addresses
        val address = if (addresses.isNullOrEmpty()) null else addresses[0]
        val addressTypes = parsedModel.addressTypes
        val addressType = if (addressTypes.isNullOrEmpty()) null else addressTypes[0]
        when (action) {
            ParseAction.ADD_CONTACTS -> {
                HandlerUtils.addContact(
                    activity,
                    parsedModel.names,
                    parsedModel.nicknames,
                    parsedModel.pronunciation,
                    parsedModel.phoneNumbers,
                    parsedModel.phoneTypes,
                    parsedModel.emails,
                    parsedModel.emailTypes,
                    parsedModel.note,
                    parsedModel.instantMessenger,
                    address,
                    addressType,
                    parsedModel.org,
                    parsedModel.title,
                    parsedModel.urls,
                    parsedModel.birthday,
                    parsedModel.geo
                )
            }
            ParseAction.SHOW_ON_MAP -> {
                HandlerUtils.searchMap(activity, address)
            }
            ParseAction.CALL -> {
                HandlerUtils.dialPhone(activity, parsedModel.phoneNumbers?.get(0))
            }
            ParseAction.SEND_EMAIL -> {
                HandlerUtils.sendEmail(activity, parsedModel.emails, null, null, null, null)
            }
            else -> super.handleButtonPress(action)
        }
    }

    override fun getDisplayList(): List<SimpleResult> {
        val simpleResultList: MutableList<SimpleResult> = ArrayList()
        parsedModel.names?.let {
            if (it.isNotEmpty()) {
                val contents = StringBuilder()
                maybeAppend(it, contents)
                simpleResultList.add(SimpleResult(R.string.parse_result_content_name, contents.toString()))
            }
        }
        parsedModel.phoneNumbers?.let {
            if (it.isNotEmpty()) {
                val contents = StringBuilder()
                maybeAppend(it, contents)
                simpleResultList.add(SimpleResult(R.string.parse_result_content_tel, contents.toString()))
            }
        }
        parsedModel.emails?.let {
            if (it.isNotEmpty()) {
                val contents = StringBuilder()
                maybeAppend(it, contents)
                simpleResultList.add(SimpleResult(R.string.parse_result_content_email, contents.toString()))
            }
        }
        parsedModel.addresses?.let {
            if (it.isNotEmpty()) {
                val contents = StringBuilder()
                maybeAppend(it, contents)
                simpleResultList.add(SimpleResult(R.string.parse_result_content_address, contents.toString()))
            }
        }
        parsedModel.nicknames?.let {
            if (it.isNotEmpty()) {
                val contents = StringBuilder()
                maybeAppend(it, contents)
                simpleResultList.add(SimpleResult(R.string.parse_result_content_nickname, contents.toString()))
            }
        }
        parsedModel.note?.let {
            simpleResultList.add(SimpleResult(R.string.parse_result_content_note, it))
        }
        parsedModel.org?.let {
            simpleResultList.add(SimpleResult(R.string.parse_result_content_org, it))
        }
        parsedModel.birthday?.let {
            simpleResultList.add(SimpleResult(R.string.parse_result_content_birthday, it))
        }
        parsedModel.title?.let {
            simpleResultList.add(SimpleResult(R.string.parse_result_content_title, it))
        }
        parsedModel.urls?.let {
            val contents = StringBuilder()
            maybeAppend(it, contents)
            simpleResultList.add(SimpleResult(R.string.parse_result_content_url, contents.toString()))
        }
        parsedModel.geo?.let {
            val contents = StringBuilder()
            maybeAppend(it, contents)
            simpleResultList.add(SimpleResult(R.string.parse_result_content_geo, contents.toString()))
        }
        return simpleResultList
    }


}