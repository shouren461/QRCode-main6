package qrscanner.barcodescanner.barcodereader.qrcodereader.base

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.pm.ShortcutManagerCompat
import com.drojian.qrcode.baselib.BaseApp
import com.drojian.qrcode.utillib.UtilHelper
import com.drojian.qrcode.utillib.utils.AppUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import qrscanner.barcodescanner.barcodereader.qrcodereader.util.AnalyticsHelper

/**
 * 应用的 Application 类，负责全局初始化
 */
class App : BaseApp() {
    /**
     * 判断当前是否为正式发布版本
     */
    override fun isRelease(): Boolean {
        return isReleaseVersion
    }

    override fun onCreate() {
        // 设置 SharedPreferences 的文件名
        UtilHelper.spName = "qr_prefs"
        super.onCreate()
        try {
            context = applicationContext
            // 动态初始化 FileProvider 的 Authority
            AUTHORITIES = context?.applicationInfo?.packageName + ".fileprovider"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 初始化一些基础库和工具
        AppUtil.initPieWebView(this)
        ActivityStack.init(this) // 初始化 Activity 栈管理
        AnalyticsHelper.bind(this) // 初始化埋点统计
        
        // 彻底清除所有已有的桌面快捷方式
        try {
            ShortcutManagerCompat.removeAllDynamicShortcuts(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 内存不足时取消全局协程作用域，释放资源
     */
    override fun onLowMemory() {
        super.onLowMemory()
        rootScope.cancel()
    }

    companion object {

        @JvmStatic
        val isReleaseVersion = false

        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            private set
        
        /**
         * 全局协程作用域，用于执行一些不依赖于 Activity 生命周期的任务
         */
        val rootScope by lazy { MainScope() }

        @JvmField
        var AUTHORITIES: String = ""
        
        // 记录是否为单次进程
        var isOnceProcess = true

        @JvmStatic
        var hasShowResult = false

        // 记录是否是第一次打开应用
        var isFirstOpen = false
    }
}