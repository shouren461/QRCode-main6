package com.drojian.qrcode.scanresultlib.util

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import com.drojian.qrcode.scanresultlib.BaseResultHandler
import com.drojian.qrcode.scanresultlib.R
import com.drojian.qrcode.scanresultlib.ResultHandlerConfig
import com.drojian.qrcode.scanresultlib.webview.ResultWebViewActivity
import java.util.Locale

/**
 * @author yangfengfan 2020-10-19
 */
object HandlerUtils {

    @JvmField
    var searchEngine = SearchEngine.Google

    const val googleSearch = "https://www.google.com/search?q="
    const val bingSearch = "https://www.bing.com/search?q="
    const val yahooSearch = "https://search.yahoo.com/search?p="
    const val duckGoSearch = "https://duckduckgo.com/?q="
    const val ecosiaSearch = "https://www.ecosia.org/search?q="
    const val yandexSearch = "https://yandex.com/search/?text="
    const val amazonSearch = "https://www.amazon.com/s?k="
    const val ebaySearch = "https://www.ebay.com/sch/i.html?&_nkw="
    const val naverSearch = "https://search.naver.com/search.naver?query="

    private val EMAIL_TYPE_STRINGS = arrayOf("home", "work", "mobile")
    private val PHONE_TYPE_STRINGS = arrayOf("home", "work", "mobile", "fax", "pager", "main")
    private val ADDRESS_TYPE_STRINGS = arrayOf("home", "work")
    private val EMAIL_TYPE_VALUES = intArrayOf(
        ContactsContract.CommonDataKinds.Email.TYPE_HOME,
        ContactsContract.CommonDataKinds.Email.TYPE_WORK,
        ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
    )
    private val PHONE_TYPE_VALUES = intArrayOf(
        ContactsContract.CommonDataKinds.Phone.TYPE_HOME,
        ContactsContract.CommonDataKinds.Phone.TYPE_WORK,
        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
        ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK,
        ContactsContract.CommonDataKinds.Phone.TYPE_PAGER,
        ContactsContract.CommonDataKinds.Phone.TYPE_MAIN
    )
    private val ADDRESS_TYPE_VALUES = intArrayOf(
        ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME,
        ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK
    )
    private const val NO_TYPE = -1
    const val MAX_BUTTON_COUNT = 4


    fun formatPhone(phoneData: String?): String {
        // Just collect the call to a deprecated method in one place
        return PhoneNumberUtils.formatNumber(phoneData)
    }

    fun sendSMS(activity: Activity, phoneNumber: String?, body: String?) {
        sendSMSFromUri(activity, "smsto:$phoneNumber", body)
    }

    fun sendMMS(activity: Activity, phoneNumber: String?, subject: String?, body: String?) {
        sendMMSFromUri(activity, "mmsto:$phoneNumber", subject, body)
    }


    /**
     * 添加联系人
     */
    fun addContact(
        activity: Activity,
        names: Array<String>?,
        nicknames: Array<String>?,
        pronunciation: String?,
        phoneNumbers: Array<String>?,
        phoneTypes: Array<String>?,
        emails: Array<String>?,
        emailTypes: Array<String>?,
        note: String?,
        instantMessenger: String?,
        address: String?,
        addressType: String?,
        org: String?,
        title: String?,
        urls: Array<String>?,
        birthday: String?,
        geo: Array<String>?
    ) {
        try {
            // Only use the first name in the array, if present.
            val intent = Intent(Intent.ACTION_INSERT_OR_EDIT, ContactsContract.Contacts.CONTENT_URI)
            intent.type = ContactsContract.Contacts.CONTENT_ITEM_TYPE
            putExtra(intent, ContactsContract.Intents.Insert.NAME, names?.get(0))
            putExtra(intent, ContactsContract.Intents.Insert.PHONETIC_NAME, pronunciation)

            phoneNumbers?.let {
                val phoneCount = it.size.coerceAtMost(Contents.PHONE_KEYS.size)
                for (x in 0 until phoneCount) {
                    putExtra(intent, Contents.PHONE_KEYS[x], it[x])

                    phoneTypes?.let {
                        if (x < phoneTypes.size && toPhoneContractType(phoneTypes[x]) >= 0) {
                            intent.putExtra(Contents.PHONE_TYPE_KEYS[x], toPhoneContractType(phoneTypes[x]))
                        }
                    }
                }
            }

            emails?.let {
                val emailCount = it.size.coerceAtMost(Contents.EMAIL_KEYS.size)
                for (x in 0 until emailCount) {
                    putExtra(intent, Contents.EMAIL_KEYS[x], it[x])
                    emailTypes?.let {
                        if (x < emailTypes.size && toEmailContractType(emailTypes[x]) >= 0) {
                            intent.putExtra(Contents.EMAIL_TYPE_KEYS[x], emailTypes[x])
                        }
                    }
                }
            }

            val data = ArrayList<ContentValues>()
            urls?.let {
                for (url in it) {
                    if (url.isNotEmpty()) {
                        data.add(ContentValues(2).also { data ->
                            data.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                            data.put(ContactsContract.CommonDataKinds.Website.URL, url)
                        })
                        break
                    }
                }
            }

            birthday?.let {
                data.add(ContentValues(3).also { data ->
                    data.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                    data.put(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
                    data.put(ContactsContract.CommonDataKinds.Event.START_DATE, it)
                })
            }

            nicknames?.let {
                for (nickname in it) {
                    if (nickname.isNotEmpty()) {
                        data.add(
                            ContentValues(3).also { data ->
                                data.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)
                                data.put(
                                    ContactsContract.CommonDataKinds.Nickname.TYPE,
                                    ContactsContract.CommonDataKinds.Nickname.TYPE_DEFAULT
                                )
                                data.put(ContactsContract.CommonDataKinds.Nickname.NAME, nickname)
                            }
                        )
                        break
                    }
                }
            }

            if (data.isNotEmpty()) {
                intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data)
            }

            StringBuilder().apply {
                note?.let {
                    this.append('\n').append(it)
                }
                geo?.let {
                    if (it.size >= 2) {
                        this.append('\n').append(it[0]).append(',').append(it[1])
                    }
                }

                if (this.isNotEmpty()) {
                    // Remove extra leading '\n'
                    putExtra(intent, ContactsContract.Intents.Insert.NOTES, this.substring(1))
                }
            }

            if (instantMessenger != null && instantMessenger.startsWith("xmpp:")) {
                intent.putExtra(ContactsContract.Intents.Insert.IM_PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_JABBER)
                intent.putExtra(ContactsContract.Intents.Insert.IM_HANDLE, instantMessenger.substring(5))
            } else {
                putExtra(intent, ContactsContract.Intents.Insert.IM_HANDLE, instantMessenger)
            }
            putExtra(intent, ContactsContract.Intents.Insert.POSTAL, address)
            if (addressType != null) {
                val type: Int = toAddressContractType(addressType)
                if (type >= 0) {
                    intent.putExtra(ContactsContract.Intents.Insert.POSTAL_TYPE, type)
                }
            }
            putExtra(intent, ContactsContract.Intents.Insert.COMPANY, org)
            putExtra(intent, ContactsContract.Intents.Insert.JOB_TITLE, title)
            launchIntent(activity, intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Do a geo search using the address as the query.
     *
     * @param address The address to find
     */
    fun searchMap(activity: Activity, address: String?) {
        launchIntent(activity, Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(address))))
    }

    fun dialPhone(activity: Activity, phoneNumber: String?) {
        phoneNumber?.let {
            launchIntent(activity, Intent(Intent.ACTION_DIAL, Uri.parse("tel:$it")))
        }
    }

    fun dialPhoneFromUri(activity: Activity, uri: String?) {
        launchIntent(activity, Intent(Intent.ACTION_DIAL, Uri.parse(uri)))
    }

    fun addPhoneOnlyContact(
        activity: Activity,
        phoneNumbers: Array<String>?,
        phoneTypes: Array<String>?
    ) {
        addContact(
            activity,
            null,
            null,
            null,
            phoneNumbers,
            phoneTypes,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }

    fun addEmailOnlyContact(
        activity: Activity,
        emails: Array<String>?,
        emailTypes: Array<String>?
    ) {
        addContact(
            activity,
            null,
            null,
            null,
            null,
            null,
            emails,
            emailTypes,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }

    /**
     * Sends an intent to create a new calendar event by prepopulating the Add Event UI. Older
     * versions of the system have a bug where the event title will not be filled out.
     *
     * @param summary     A description of the event
     * @param start       The start time
     * @param allDay      if true, event is considered to be all day starting from start time
     * @param end         The end time (optional; can be < 0 if not specified)
     * @param location    a text description of the event location
     * @param description a text description of the event itself
     * @param attendees   attendees to invite
     */
    fun addCalendarEvent(
        activity: Activity,
        summary: String?,
        start: Long,
        allDay: Boolean,
        end: Long,
        location: String?,
        description: String?,
        attendees: Array<String>?
    ) {

        val intent = Intent(Intent.ACTION_INSERT)
        intent.type = "vnd.android.cursor.item/event"
        intent.putExtra("beginTime", start)
        if (allDay) {
            intent.putExtra("allDay", true)
        }

        val endTime = if (end < 0L) {
            if (allDay) {
                start + 24 * 60 * 60 * 1000
            } else {
                start
            }
        } else {
            end
        }
        intent.putExtra("endTime", endTime)

        putExtra(intent, "title", summary)
        putExtra(intent, "eventLocation", location)
        putExtra(intent, "description", description)
        attendees?.let { intent.putExtra(Intent.EXTRA_EMAIL, it) }

        try {
            // Do this manually at first
            rawLaunchIntent(activity, intent)
        } catch (e: Exception) {
            // For calendar apps that don't like "INSERT":
            intent.action = Intent.ACTION_EDIT
            launchIntent(activity, intent) // Fail here for real if nothing can handle it
        }
    }


    fun sendEmail(
        activity: Activity,
        to: Array<String>?,
        cc: Array<String>?,
        bcc: Array<String>?,
        subject: String?,
        body: String?
    ) {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.type = "application/octet-stream"
        to?.let {
            if (it.isNotEmpty()) {
                intent.putExtra(Intent.EXTRA_EMAIL, it)
            }
        }
        cc?.let {
            if (it.isNotEmpty()) {
                intent.putExtra(Intent.EXTRA_CC, it)
            }
        }
        bcc?.let {
            if (it.isNotEmpty()) {
                intent.putExtra(Intent.EXTRA_BCC, it)
            }
        }

        putExtra(intent, Intent.EXTRA_SUBJECT, subject)
        putExtra(intent, Intent.EXTRA_TEXT, body)
        if (hasGmail(activity)) {
            intent.setPackage(PACKAGE_GMAIL)
        } else if (hasEmail(activity)) {
            intent.setPackage(PACKAGE_EMAIL_APP)
        }
        launchIntent(activity, intent)
    }

    fun openURL(resultHandler: BaseResultHandler, url: String?) {
        url?.let {
            // Strangely, some Android browsers don't seem to register to handle HTTP:// or HTTPS://.
            // Lower-case these as it should always be OK to lower-case these schemes.
            val uri = when {
                it.startsWith("HTTP://") -> Uri.parse("http" + it.substring(4))
                it.startsWith("HTTPS://") -> Uri.parse("https" + it.substring(5))
                else -> Uri.parse(it)
            }
            if (resultHandler.resultHandlerConfig.useWebView) {
                ResultWebViewActivity.startMe(resultHandler.activity , ResultWebViewActivity.IntentFrom.URL, it, resultHandler.resultHandlerConfig)
            } else {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                launchIntent(resultHandler.activity, intent)
            }
        }
    }

    fun publicOpenURL(activity: Activity, url: String?, resultHandlerConfig: ResultHandlerConfig) {
        url?.let {
            // Strangely, some Android browsers don't seem to register to handle HTTP:// or HTTPS://.
            // Lower-case these as it should always be OK to lower-case these schemes.
            val uri = when {
                it.startsWith("HTTP://") -> Uri.parse("http" + it.substring(4))
                it.startsWith("HTTPS://") -> Uri.parse("https" + it.substring(5))
                else -> Uri.parse(it)
            }
            if (resultHandlerConfig.useWebView) {
                ResultWebViewActivity.startMe(activity, ResultWebViewActivity.IntentFrom.URL, it, resultHandlerConfig)
            } else {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                launchIntent(activity, intent)
            }
        }
    }

    fun openViber(activity: Activity, phone: String?) {
        if (hasViber(activity)) {
            try {
                val intent = Intent("android.intent.action.VIEW")
                intent.setClassName("com.viber.voip", "com.viber.voip.WelcomeActivity")
                intent.data = Uri.parse("tel:" + Uri.encode(phone?.substring("viber://add?number=".length)))
                activity.startActivity(intent)
            } catch (e: Exception) {
                AlertDialog.Builder(activity)
                    .setMessage(R.string.lh_intent_failed)
                    .setPositiveButton(R.string.lh_button_ok, null)
                    .show()
            }
        } else {
            AlertDialog.Builder(activity)
                .setMessage(R.string.lh_intent_failed)
                .setPositiveButton(R.string.lh_button_ok, null)
                .show()
        }
    }

    fun customSearch(activity: Activity, searchEngine: String, query: String?) {
        try {
            val uri = Uri.parse(searchEngine + query)
            launchIntent(activity, Intent(Intent.ACTION_VIEW, uri))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun webSearch(resultHandler: BaseResultHandler, query: String?) {
        try {
            val uri = when (searchEngine) {
                SearchEngine.Google -> {
                    Uri.parse(googleSearch + query)
                }
                SearchEngine.Bing -> {
                    Uri.parse(bingSearch + query)
                }
                SearchEngine.Yahoo -> {
                    Uri.parse(yahooSearch + query)
                }
                SearchEngine.Duck -> {
                    Uri.parse(duckGoSearch + query)
                }
                SearchEngine.Ecosia -> {
                    Uri.parse(ecosiaSearch + query)
                }
                SearchEngine.Yandex -> {
                    Uri.parse(yandexSearch + query)
                }
                SearchEngine.Amazon -> {
                    Uri.parse(amazonSearch + query)
                }
                SearchEngine.Ebay -> {
                    Uri.parse(ebaySearch + query)
                }
                SearchEngine.Naver -> {
                    Uri.parse(naverSearch + query)
                }
            }
            if (resultHandler.resultHandlerConfig.useWebView && query != null) {
                ResultWebViewActivity.startMe(resultHandler.activity, ResultWebViewActivity.IntentFrom.Product, query, resultHandler.resultHandlerConfig)
            } else {
                launchIntent(resultHandler.activity, Intent(Intent.ACTION_VIEW, uri))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun publicWebSearch(activity: Activity, searchEngine:SearchEngine = SearchEngine.Google, query: String?) {
        try {
            val baseSearchUrl = when (searchEngine) {
                SearchEngine.Google -> {
                    googleSearch
                }
                SearchEngine.Bing -> {
                    bingSearch
                }
                SearchEngine.Yahoo -> {
                    yahooSearch
                }
                SearchEngine.Duck -> {
                    duckGoSearch
                }
                SearchEngine.Ecosia -> {
                    ecosiaSearch
                }
                SearchEngine.Yandex -> {
                    yahooSearch
                }
                SearchEngine.Amazon -> {
                    amazonSearch
                }
                SearchEngine.Ebay -> {
                    ebaySearch
                }
                SearchEngine.Naver -> {
                    naverSearch
                }
            }
            val uri = Uri.parse(baseSearchUrl + query)
            launchIntent(activity, Intent(Intent.ACTION_VIEW, uri))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun amazonSearch(resultHandler: BaseResultHandler, query: String?) {
        val uri = when (resultHandler.resultHandlerConfig.countryCode) {
            "MX" -> "https://www.amazon.com.mx/s?k="
            "DE" -> "https://www.amazon.de/s?k="
            "IN" -> "https://www.amazon.in/s?k="
            else -> "https://www.amazon.com/s?k="
        }
        if (resultHandler.resultHandlerConfig.useWebView) {
            ResultWebViewActivity.startMe(resultHandler.activity, ResultWebViewActivity.IntentFrom.URL, uri + query, resultHandler.resultHandlerConfig)
        } else {
            launchIntent(resultHandler.activity, Intent(Intent.ACTION_VIEW, Uri.parse(uri + query)))
        }
    }

    fun ebaySearch(resultHandler: BaseResultHandler, query: String?) {
        val uri = when (resultHandler.resultHandlerConfig.countryCode) {
            "DE" -> "https://www.ebay.de/sch/i.html?&_nkw="
            else -> "https://www.ebay.com/sch/i.html?&_nkw="
        }
        if (resultHandler.resultHandlerConfig.useWebView) {
            ResultWebViewActivity.startMe(resultHandler.activity, ResultWebViewActivity.IntentFrom.URL, uri + query, resultHandler.resultHandlerConfig)
        } else {
            launchIntent(resultHandler.activity, Intent(Intent.ACTION_VIEW, Uri.parse(uri + query)))
        }
    }

    fun yahooSearch(resultHandler: BaseResultHandler, query: String?) {
        val uri = when (resultHandler.resultHandlerConfig.countryCode) {
            "JP" -> "https://search.yahoo.co.jp/search?p="
            else -> "https://search.yahoo.com/search?p="
        }
        if (resultHandler.resultHandlerConfig.useWebView) {
            ResultWebViewActivity.startMe(resultHandler.activity, ResultWebViewActivity.IntentFrom.URL, uri + query, resultHandler.resultHandlerConfig)
        } else {
            launchIntent(resultHandler.activity, Intent(Intent.ACTION_VIEW, Uri.parse(uri + query)))
        }
    }

    fun rakutenSearch(resultHandler: BaseResultHandler, query: String?) {
        val uri = Uri.parse("https://search.rakuten.co.jp/search/mall/$query")
        if (resultHandler.resultHandlerConfig.useWebView) {
            ResultWebViewActivity.startMe(resultHandler.activity, ResultWebViewActivity.IntentFrom.URL, uri.toString(), resultHandler.resultHandlerConfig)
        } else {
            launchIntent(resultHandler.activity, Intent(Intent.ACTION_VIEW, uri))
        }
    }


    // Uses the mobile-specific version of Product Search, which is formatted for small screens.
    fun openProductSearch(resultHandler: BaseResultHandler, upc: String?) {
        val uri = Uri.parse("https://www.google." + LocaleManager.getProductSearchCountryTLD(resultHandler.activity) + "/m/search?tbm=shop&q=" + upc)
        if (resultHandler.resultHandlerConfig.useWebView && upc != null) {
            ResultWebViewActivity.startMe(resultHandler.activity, ResultWebViewActivity.IntentFrom.URL, upc, resultHandler.resultHandlerConfig)
        } else {
            launchIntent(resultHandler.activity, Intent(Intent.ACTION_VIEW, uri))
        }
    }

    fun openBookSearch(resultHandler: BaseResultHandler, isbn: String?) {
        val uri = Uri.parse("https://books.google." + LocaleManager.getBookSearchCountryTLD(resultHandler.activity) + "/books?vid=isbn" + isbn)
        if (resultHandler.resultHandlerConfig.useWebView && isbn != null) {
            ResultWebViewActivity.startMe(resultHandler.activity, ResultWebViewActivity.IntentFrom.URL, isbn, resultHandler.resultHandlerConfig)
        } else {
            launchIntent(resultHandler.activity, Intent(Intent.ACTION_VIEW, uri))
        }
    }

    fun openMap(activity: Activity, geoURI: String?) {
        launchIntent(activity, Intent(Intent.ACTION_VIEW, Uri.parse(geoURI)))
    }

    fun getDirections(activity: Activity, latitude: Double, longitude: Double, geoURI: String?) {
        val mapPackageList: MutableList<String> = ArrayList()
        mapPackageList.add("com.google.android.apps.maps") // GoogleMap
        mapPackageList.add("com.autonavi.minimap") // 高德地图
        mapPackageList.add("com.baidu.BaiduMap") // 百度地图
        mapPackageList.add("com.tencent.map") // 腾讯地图

        val uri = when (findSuitableApp(activity, mapPackageList)) {
            0 -> "https://maps.google." + LocaleManager.getCountryTLD(activity) + "/maps?f=d&daddr=" + latitude + ',' + longitude
            1 -> "androidamap://navi?sourceApplication=qrcode&lat=$latitude&lon=$longitude&dev=1&style=0"
            2 -> "baidumap://map/navi?location=$latitude,$longitude"
            3 -> "qqmap://map/routeplan?type=drive&fromcoord=CurrentLocation&tocoord=$latitude,$longitude"
            else -> "https://maps.google." + LocaleManager.getCountryTLD(activity) + "/maps?f=d&daddr=" + latitude + ',' + longitude
        }

        launchIntent(activity, Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    }

    fun copyResultText(context: Context, s: String) {
        try {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val mClipData = ClipData.newPlainText("Label", s)
            cm.setPrimaryClip(mClipData)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    fun shareBySMS(activity: Activity, contents: String?) {
        sendSMSFromUri(activity, "smsto:", contents)
    }

    fun shareByEmail(activity: Activity, contents: String?) {
        sendEmail(activity, null, null, null, null, contents)
    }

    fun trimStringBuilder(stringBuilder: StringBuilder?) {
        stringBuilder?.let {
            if (it.isNotEmpty()) {
                if (it[it.length - 1] == '\n') {
                    it.deleteCharAt(it.length - 1)
                }
            }
        }
    }


    /**
     * Like [.rawLaunchIntent] but will show a user dialog if nothing is available to handle.
     */
    fun launchIntent(activity: Activity, intent: Intent?) {
        try {
            rawLaunchIntent(activity, intent)
        } catch (e: Exception) {
            AlertDialog.Builder(activity)
                .setMessage(R.string.lh_intent_failed)
                .setPositiveButton(R.string.lh_button_ok, null)
                .show()
        }
    }

    private fun sendMMSFromUri(activity: Activity, uri: String, subject: String?, body: String?) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(uri))
        // The Messaging app needs to see a valid subject or else it will treat this an an SMS.
        if (subject == null || subject.isEmpty()) {
//            putExtra(intent, "subject", activity.getString(R.string.msg_default_mms_subject));
        } else {
            putExtra(intent, "subject", subject)
        }
        putExtra(intent, "sms_body", body)
        intent.putExtra("compose_mode", true)
        launchIntent(activity, intent)
    }

    private fun sendSMSFromUri(activity: Activity, uri: String?, body: String?) {
        uri?.let {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(uri))
            putExtra(intent, "sms_body", body)
            // Exit the app once the SMS is sent
            intent.putExtra("compose_mode", true)
            launchIntent(activity, intent)
        }
    }

    private fun putExtra(intent: Intent, key: String?, value: String?) {
        key?.let { k ->
            value?.let { v ->
                intent.putExtra(k, v)
            }
        }

    }

    private fun toEmailContractType(typeString: String?): Int {
        return doToContractType(typeString, EMAIL_TYPE_STRINGS, EMAIL_TYPE_VALUES)
    }

    private fun toPhoneContractType(typeString: String?): Int {
        return doToContractType(typeString, PHONE_TYPE_STRINGS, PHONE_TYPE_VALUES)
    }

    private fun toAddressContractType(typeString: String?): Int {
        return doToContractType(typeString, ADDRESS_TYPE_STRINGS, ADDRESS_TYPE_VALUES)
    }

    private fun doToContractType(typeString: String?, types: Array<String>, values: IntArray): Int {
        typeString?.let {
            for (i in types.indices) {
                if (typeString.startsWith(types[i]) || typeString.startsWith(
                        types[i].lowercase(
                            Locale.ENGLISH
                        )
                    )
                ) {
                    return values[i]
                }
            }
        }
        return NO_TYPE
    }

    /**
     * Like [.launchIntent] but will tell you if it is not handle-able
     * via [ActivityNotFoundException].
     *
     * @throws ActivityNotFoundException if Intent can't be handled
     */
    @Throws(ActivityNotFoundException::class)
    private fun rawLaunchIntent(activity: Activity, intent: Intent?) {
        intent?.let {
            intent.addFlags(Intents.FLAG_NEW_DOC)
            activity.startActivity(it)
        }
    }

    /**
     * 是否安装了list中的app
     *
     * @param context
     * @param appPackageNameList
     * @return 返回第一个找到的index, 没找到返回-1
     */
    private fun findSuitableApp(context: Context, appPackageNameList: List<String>): Int {
        for (i in appPackageNameList.indices) {
            val suitPackageName = appPackageNameList[i]
            if (isAppInstalled(context, suitPackageName)) {
                return i
            }
        }
        return -1
    }


    private fun isAppInstalled(context: Context, packageName: String?): Boolean {
        val packageInfo= try {
            if (packageName != null) {
                context.packageManager.getPackageInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES)
            } else {
                null
            }
        } catch (e: Throwable) {
            null
        }
        return packageInfo != null
    }

    enum class SearchEngine {
        Google,
        Bing,
        Yahoo,
        Duck,
        Ecosia,
        Yandex,
        Amazon,
        Ebay,
        Naver
    }
}