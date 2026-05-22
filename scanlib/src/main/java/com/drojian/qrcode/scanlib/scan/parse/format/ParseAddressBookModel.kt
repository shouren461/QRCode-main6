package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

/**
 * @author yangfengfan 2020-08-27
 *
 * 联系人解析结果
 */
data class ParseAddressBookModel(
    val names: Array<String>? = arrayOf(),
    val nicknames: Array<String>? = arrayOf(),
    val pronunciation: String? = null,
    val phoneNumbers: Array<String>? = arrayOf(),
    val phoneTypes: Array<String>? = arrayOf(),
    val emails: Array<String>? = arrayOf(),
    val emailTypes: Array<String>? = arrayOf(),
    val instantMessenger: String? = null,
    val note: String? = null,
    val addresses: Array<String>? = arrayOf(),
    val addressTypes: Array<String>? = arrayOf(),
    val org: String? = null,
    val birthday: String? = null,
    val title: String? = null,
    val urls: Array<String>? = arrayOf(),
    val geo: Array<String>? = arrayOf(),
) : BaseParseModel(ParsedFormat.ADDRESSBOOK) {

    init {
        require(!(phoneNumbers != null && phoneTypes != null && phoneNumbers.size != phoneTypes.size)) { "Phone numbers and types lengths differ" }
        require(!(emails != null && emailTypes != null && emails.size != emailTypes.size)) { "Emails and types lengths differ" }
        require(!(addresses != null && addressTypes != null && addresses.size != addressTypes.size)) { "Addresses and types lengths differ" }
    }

    override fun getShowText() = try {
        getBestShowContent(
            names?.get(0),
            nicknames?.get(0),
            phoneNumbers?.get(0),
            title,
            emails?.get(0),
            pronunciation,
            title,
            note,
            birthday
        )
    } catch (e: Exception) {
        null
    }

}