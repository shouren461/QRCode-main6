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

//应用的Application类,负责全局初始化
class App : BaseApp() {
    //判断当前是否是正式发行版本  ->true:表示正式版本  false:表示测试版本
    override fun isRelease(): Boolean {
        return isReleaseVersion
    }

    //应用启动时的初始化方法，执行全局初始化操作
    override fun onCreate() {
        //设置SharedPreferences 的文件名
        UtilHelper.spName = "qr_prefs"
        super.onCreate()
        try {
            //保存应用上下文，方便全局调用
            context = applicationContext
            //动态初始化FileProvider的Authority
            //格式:Authority ->包名 + ".fileprovider"   ->用于安全的共享给其他应用
            AUTHORITIES = context?.applicationContext ?.packageName + "fileprovider"
        }catch (e: Exception){
            e.printStackTrace()
        }

        //初始化一些基础库和工具
        AppUtil.initPieWebView(this);//初始化Webview相关配置
        ActivityStack.init(this)  //初始化Activity栈管理
        AnalyticsHelper.bind(this)  //初始化埋点统计

    }
    //内存不足时取消全局协程作用域，释放资源    ->防止内存泄漏和资源浪费
    override fun onLowMemory() {
        super.onLowMemory()
        rootScope.cancel()
    }

    //静态成员变量区域   ->包含全局共享的配置和状态
    companion object{

        //默认不是发行版本，而是测试版本
        @JvmStatic
        val isReleaseVersion = false
        //全局应用上下文
        @SuppressLint("StaticFieldLeak")
        var context: Context ?= null
            private set

        //全局协程作用域  ->用于执行一些不依赖于Activity生命周期的任务
        //使用lazy懒加载，只在第一次访问时创建
        val rootScope by lazy { MainScope() }

        //FileProvider中的Authority,用于文件夹共享
        @JvmStatic
        var AUTHORITIES: String = ""

        //记录是否是单次线程 ->用于判断应用是否从最近任务列表中打开
        var isOnceProcess  = true

        //记录是否已经显示过扫描结果页面
        @JvmStatic
        var hasShowResult = false

        //记录是否是第一次打开应用  ->用于引导页，新手教程等逻辑判断
        var isFirstOpen = false
    }
}