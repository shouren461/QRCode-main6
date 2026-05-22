package qrscanner.barcodescanner.barcodereader.qrcodereader.util

import android.app.Application
import android.text.TextUtils
import com.drojian.qrcode.utillib.analytics.BaseAnalytics
import com.drojian.qrcode.utillib.log.Logcat
import com.drojian.qrcode.utillib.utils.URLUtil
import com.google.firebase.crashlytics.FirebaseCrashlytics
import qrscanner.barcodescanner.barcodereader.qrcodereader.base.App
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.QRCodeServerData

object AnalyticsHelper {
    private val baseAnalytics: BaseAnalytics = BaseAnalytics()

    fun bind(application: Application) {
        baseAnalytics.bind(application)
    }

    @JvmStatic
    fun logSplashInterstitialADEvent(itemId: String) {
        baseAnalytics.logSelectContent("Splash_ad", itemId)
    }


    @JvmStatic
    fun logRateUsEvent(itemId: String) {
        baseAnalytics.logSelectContent("Ratings", itemId)
    }

    @JvmStatic
    fun logSearchDialog(itemId: String) {
        baseAnalytics.logSelectContent("Barcode_input", itemId)
    }

    @JvmStatic
    fun logScanResultEvent(contentType: String, itemId: String) {
        Logcat.e("$contentType:$itemId")
        baseAnalytics.logSelectContent("ScanResult_$contentType", itemId)
    }

    @JvmStatic
    fun logScanURLResult(url: String?) {
        val domainUrl = QRUtil.getDomainName(url)
        if (!TextUtils.isEmpty(domainUrl)) {
            baseAnalytics.logSelectContent("Scan_URL_short", domainUrl)
        }
        val retrofitUrl = url ?: ""
        if (!TextUtils.isEmpty(url)) {
            baseAnalytics.logSelectContent("Scan_URL_long", retrofitUrl)
        }
    }

    @JvmStatic
    fun logCreateURLResult(url: String?) {
        val domainUrl = QRUtil.getDomainName(url)
        if (!TextUtils.isEmpty(domainUrl)) {
            baseAnalytics.logSelectContent("Create_URL_short", domainUrl)
        }
    }

    @JvmStatic
    fun logScanAlbumEvent(itemId: String) {
        baseAnalytics.logSelectContent("Scan_from_img", itemId)
    }

    @JvmStatic
    fun logScanEvent(itemId: String) {
        baseAnalytics.logSelectContent("Scan_event", itemId)
    }

    @JvmStatic
    fun logScanTimeEvent(itemId: String) {
        baseAnalytics.logSelectContent("Scan_time", itemId)
    }

    @JvmStatic
    fun logScanFrequency(itemId: String) {
        baseAnalytics.logSelectContent("Scan_freq", itemId)
    }

    @JvmStatic
    fun logScanOneDayTimes(itemId: String) {
        baseAnalytics.logSelectContent("Scan_daily", itemId)
    }

    @JvmStatic
    fun logClickEvent(itemId: String) {
        baseAnalytics.logSelectContent("Click_event", itemId)
    }

    @JvmStatic
    fun logPageViewEvent(itemId: String) {
        baseAnalytics.logSelectContent("Page_event", itemId)
    }

    @JvmStatic
    fun logException(throwable: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    @JvmStatic
    fun logMessage(msg: String) {
        baseAnalytics.logSelectContent("Message", msg)
    }

    // Create新增
    @JvmStatic
    fun logCreateHome(msg: String) {
        baseAnalytics.logSelectContent("Create_page", msg)
    }

    @JvmStatic
    fun logCalendarFreeResult(msg: String) {
        baseAnalytics.logSelectContent("Create_result_calendar", msg)
    }

    @JvmStatic
    fun logContactFreeResult(msg: String) {
        baseAnalytics.logSelectContent("Create_result_contact", msg)
    }

    @JvmStatic
    fun logEmailFreeResult(msg: String) {
        baseAnalytics.logSelectContent("Create_result_email", msg)
    }

    @JvmStatic
    fun logMeCardFreeResult(msg: String) {
        baseAnalytics.logSelectContent("Create_result_mecard", msg)
    }

    @JvmStatic
    fun logMeCardNotes(msg: String) {
        baseAnalytics.logSelectContent("Mecard_notes", msg)
    }

    @JvmStatic
    fun logSMSFreeResult(msg: String) {
        baseAnalytics.logSelectContent("Create_result_SMS", msg)
    }


    @JvmStatic
    fun logWifiFreeResult(msg: String) {
        baseAnalytics.logSelectContent("Create_result_wifi", msg)
    }

    @JvmStatic
    fun logWifiPassResult(msg: String) {
        baseAnalytics.logSelectContent("Wifi_pswd", msg)
    }

    @JvmStatic
    fun logFacebookPremiumResult(msg: String) {
        baseAnalytics.logSelectContent("Create_result_fb", msg)
    }

    @JvmStatic
    fun logInstagramPremiumResult(msg: String) {
        baseAnalytics.logSelectContent("Create_result_ins", msg)
    }

    @JvmStatic
    fun logPaypalPremiumResult(msg: String) {
        baseAnalytics.logSelectContent("Create_result_paypal", msg)
    }

    @JvmStatic
    fun logSpotifyPremiumResult(msg: String) {
        baseAnalytics.logSelectContent("Create_result_spotify", msg)
    }

    @JvmStatic
    fun logTwitterPremiumResult(msg: String) {
        baseAnalytics.logSelectContent("Create_result_tw", msg)
    }

    @JvmStatic
    fun logViberPremiumResult(msg: String) {
        baseAnalytics.logSelectContent("Create_result_viber", msg)
    }

    @JvmStatic
    fun logWhatsappPremiumResult(msg: String) {
        baseAnalytics.logSelectContent("Create_result_wa", msg)
    }

    @JvmStatic
    fun logYoutubePremiumResult(msg: String) {
        baseAnalytics.logSelectContent("Create_result_youtube", msg)
    }

    @JvmStatic
    fun logCreateResultNumber(msg: String) {
        baseAnalytics.logSelectContent("Create_result_count", msg)
    }

    @JvmStatic
    fun logCreateResultTotal(msg: String) {
        baseAnalytics.logSelectContent("Create_result_event", msg)
    }

    @JvmStatic
    fun logHistory(msg: String) {
        baseAnalytics.logSelectContent("Create_history", msg)
    }

    @JvmStatic
    fun logIAP(msg: String) {
        baseAnalytics.logSelectContent("IAP_event", msg)
    }

    @JvmStatic
    fun logScanFeedback(itemId: String) {
        baseAnalytics.logSelectContent("emotional_page", itemId)
    }

    fun logFeedback(itemId: String) {
        baseAnalytics.logSelectContent("FeedBack", itemId)
    }

    @JvmStatic
    fun logProductNumber(itemId: String) {
        baseAnalytics.logSelectContent("Product_code", itemId)
    }

    @JvmStatic
    fun logBarcodeFormat(itemId: String) {
        baseAnalytics.logSelectContent("Code_type", itemId)
    }

    @JvmStatic
    fun logScanResultFormat(itemId: String) {
        baseAnalytics.logSelectContent("Scan_result_type", itemId)
    }

    /**
     * 以下为核心埋点，不要动
     */
    @JvmStatic
    fun logKeyParsedFormat(itemId: String) {
        Logcat.e("key_content", "ParsedFormat: $itemId")
        baseAnalytics.logSelectContent("ParsedFormat", itemId)
    }

    @JvmStatic
    fun logKeyCreateFormat(itemId: String) {
        Logcat.e("key_content", "CreateFormat: $itemId")
        baseAnalytics.logSelectContent("CreateFormat", itemId)
    }

    @JvmStatic
    fun logKeyRateUs(itemId: String) {
        Logcat.e("key_content", "RateUs: $itemId")
        baseAnalytics.logSelectContent("RateUs", itemId)
    }

    @JvmStatic
    fun logKeyFeedback(itemId: String) {
        Logcat.e("key_content", "Feedback: $itemId")
        baseAnalytics.logSelectContent("Feedback", itemId)
    }

    @JvmStatic
    fun logKeyScanRate(itemId: String) {
        Logcat.e("key_content", "ScanRate: $itemId")
        baseAnalytics.logSelectContent("ScanRate", itemId)
    }

    @JvmStatic
    fun logKeyScanURL(url: String) {
        if (URLUtil.isHttpUrl(url) || url.startsWith("www.")) {
            val shortURL = QRUtil.getDomainName(url)
            if (!TextUtils.isEmpty(shortURL)) {
                Logcat.e("key_content", "url_short: $shortURL")
                baseAnalytics.logSelectContent("url_short", shortURL)
            }

            if (!TextUtils.isEmpty(url)) {
                Logcat.e("key_content", "url_long: $url")
                baseAnalytics.logSelectContent("url_long", url)
            }
        }
    }

    /**
     * 无结果页  埋点
     */
    fun logNoResultEvent(itemId: String) {
        Logcat.e("result_none:$itemId")
        baseAnalytics.logSelectContent("result_none", itemId)
    }

    fun logFakeResultEvent(itemId: String) {
        Logcat.e("result_fake:$itemId")
        baseAnalytics.logSelectContent("result_fake", itemId)
    }

    @JvmStatic
    fun logForPermission(contentType: String, itemId: String) {
        if (!App.isFirstOpen)
            return
        Logcat.e("Logcat57", "$contentType:$itemId")
        baseAnalytics.logSelectContent(contentType, itemId)
    }

    fun logDemoRetainEvent(itemId: String){
        Logcat.e("Logcat57", "start_demo_page:$itemId")
        baseAnalytics.logSelectContent("start_demo_page", itemId)
    }

    fun logDemoProductScanEvent(itemId: String) {
        Logcat.e("Logcat57", "product_scan_page:$itemId")
        baseAnalytics.logSelectContent("product_scan_page", itemId)
    }

    fun logRateOptimization(itemId: String){
        Logcat.e("Logcat57", "Fail_popup:$itemId")
        baseAnalytics.logSelectContent("Fail_popup", itemId)
    }

}