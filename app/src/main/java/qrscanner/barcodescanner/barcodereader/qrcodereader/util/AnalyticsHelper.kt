package qrscanner.barcodescanner.barcodereader.qrcodereader.util

import android.app.Application
import android.text.TextUtils
import com.drojian.qrcode.utillib.analytics.BaseAnalytics
import com.drojian.qrcode.utillib.log.Logcat
import com.drojian.qrcode.utillib.utils.URLUtil
import com.google.firebase.crashlytics.FirebaseCrashlytics
import qrscanner.barcodescanner.barcodereader.qrcodereader.base.App
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.QRCodeServerData

//埋点分析助手类  ->作用：负责应用内所有业务逻辑的统计上报，包括扫码、创建和历史记录,
// 通过这些埋点，你可以了解用户最喜欢使用哪些功能，以及在哪个环节遇到了问题。

object AnalyticsHelper {
    private val baseAnalytics: BaseAnalytics = BaseAnalytics()
    //初始化统计SDK
    fun bind(application: Application) {
        baseAnalytics.bind(application)
    }

    //1.创建功能相关埋点（用于分析用户生成了哪些二维码）
    //记录进入创建主页面的事件
    @JvmStatic
    fun logCreateHome(msg: String) {
        baseAnalytics.logSelectContent("Create_page", msg)
    }
    //记录创建日历类型二维码的成功事件
    @JvmStatic
    fun logCalendarFreeResult(msg: String) {
        baseAnalytics.logSelectContent("Create_result_calendar", msg)
    }
    //记录创建YOuTube 类型二维码的成功事件
    @JvmStatic
    fun logYoutubePremiumResult(msg: String) {
        baseAnalytics.logSelectContent("Create_result_youtube", msg)
    }
    //记录二维码生成的具体类型计数
    @JvmStatic
    fun logCreateResultNumber(msg: String) {
        baseAnalytics.logSelectContent("Create_result_count", msg)
    }
    //记录所有二维码生成的总计数
    @JvmStatic
    fun logCreateResultTotal(msg: String) {
        baseAnalytics.logSelectContent("Create_result_event", msg)
    }
    //核心埋点:记录用户生成二维码的具体格式
    @JvmStatic
    fun logKeyCreateFormat(itemId: String) {
        Logcat.e("key_content", "CreateFormat: $itemId")
        baseAnalytics.logSelectContent("CreateFormat", itemId)
    }

    //2. 历史记录相关埋点
    //记录用户查看或操作历史记录页面的 =事件
    @JvmStatic
    fun logHistory(msg: String) {
        baseAnalytics.logSelectContent("Create_history", msg)
    }

    //3. 通用系统埋点
    //记录应用内通用的点击事件
    @JvmStatic
    fun logClickEvent(itemId: String) {
        baseAnalytics.logSelectContent("Click_event", itemId)
    }
    //记录页面访问事件(用于分析哪些页面的活跃度更高)
    @JvmStatic
    fun logPageViewEvent(itemId: String) {
        baseAnalytics.logSelectContent("Page_event", itemId)
    }

    //关键功能:记录代码异常或者崩溃信息，并在Firebase控制台上实时显示
    @JvmStatic
    fun logException(throwable: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

    //4. 扫描功能相关埋点 (当前用于预留或初始实现)
    //记录扫描结果事件（根据扫描到的内容类型分类）
    @JvmStatic
    fun logScanResultEvent(contentType: String, itemId: String) {
        Logcat.e("$contentType:$itemId")
        baseAnalytics.logSelectContent("ScanResult_$contentType", itemId)
    }
    //记录扫描到的 URL 地址（区分域名和完整链接）
    @JvmStatic
    fun logScanURLResult(url: String?) {
        val domainUrl = QRUtil.getDomainName(url)
        if (!TextUtils.isEmpty(domainUrl)) {
            // 记录域名（短地址）
            baseAnalytics.logSelectContent("Scan_URL_short", domainUrl)
        }
        val retrofitUrl = url ?: ""
        if (!TextUtils.isEmpty(url)) {
            // 记录完整链接（长地址）
            baseAnalytics.logSelectContent("Scan_URL_long", retrofitUrl)
        }
    }
    //记录从相册图片进行扫描的点击事件
    @JvmStatic
    fun logScanAlbumEvent(itemId: String) {
        baseAnalytics.logSelectContent("Scan_from_img", itemId)
    }
    //记录通用的扫描启动事件
    @JvmStatic
    fun logScanEvent(itemId: String) {
        baseAnalytics.logSelectContent("Scan_event", itemId)
    }
    //记录条码的原始格式（如 QR_CODE, CODE_128 等）
    @JvmStatic
    fun logBarcodeFormat(itemId: String) {
        baseAnalytics.logSelectContent("Code_type", itemId)
    }

    //记录扫描结果被解析后的业务类型
    @JvmStatic
    fun logScanResultFormat(itemId: String) {
        baseAnalytics.logSelectContent("Scan_result_type", itemId)
    }

    //核心埋点:记录解析成功后的内容格式标识
    @JvmStatic
    fun logKeyParsedFormat(itemId: String) {
        Logcat.e("key_content", "ParsedFormat: $itemId")
        baseAnalytics.logSelectContent("ParsedFormat", itemId)
    }

    //专门记录扫描到的 URL 地址，用于分析用户扫描网页的情况
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
    //记录扫描无结果或失败的事件
    fun logNoResultEvent(itemId: String) {
        Logcat.e("result_none:$itemId")
        baseAnalytics.logSelectContent("result_none", itemId)
    }
}