package qrscanner.barcodescanner.barcodereader.qrcodereader.base

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.drojian.qrcode.utillib.utils.SPUtil
import qrscanner.barcodescanner.barcodereader.qrcodereader.R
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.MainActivity
import qrscanner.barcodescanner.barcodereader.qrcodereader.util.AnalyticsHelper.logPageViewEvent

/**
 * 所有 Activity 的基类，提供沉浸式状态栏、生命周期埋点、语言切换等基础功能
 */
abstract class BaseActivity : AppCompatActivity() {
    @JvmField
    protected var isRestore: Boolean = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 检查是否是从销毁中恢复的
        if (savedInstanceState != null) {
            isRestore = true
        }
        setContentView()
        // 设置沉浸式外观（全屏显示，处理状态栏/导航栏）
        makeAppEdgeToEdge()
        // 按照规范顺序执行初始化
        initData()
        initView()
        initAction()
        // 页面打开埋点统计
        logPageViewEvent(this.name)
    }

    /**
     * 实现“边到边”设计，让内容可以延伸到状态栏和导航栏下方
     */
    private fun makeAppEdgeToEdge() {
        enableEdgeToEdge()
        // 针对不同 Android 版本微调导航栏颜色/对比度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarContrastEnforced(false)
        } else if (Build.VERSION.SDK_INT < 26) { 
            window.navigationBarColor = Color.BLACK
        }

        // 设置状态栏和导航栏图标为深色（适用于浅色背景）
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars =
            true
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        
        // 针对主页（MainActivity）和其他页面设置不同的边距策略
        if (this is MainActivity) {
            // MainActivity 通常包含相机扫描页，不需要顶部边距，由内部 Fragment 自行处理
            enableInsetsView(findViewById(R.id.main), setTop = false)
        } else {
            // 其他页面默认预留顶部状态栏高度，防止内容被遮挡
            enableInsetsView(findViewById(R.id.main), setTop = true)
        }
    }

    /**
     * 适配系统栏（状态栏/导航栏/输入法）的边距工具
     */
    protected fun enableInsetsView(
        insetsViews: View?, setTop: Boolean = true, setBottom: Boolean = true
    ) {
        if (insetsViews == null) {
            return
        }
        ViewCompat.setOnApplyWindowInsetsListener(insetsViews) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // 处理输入法弹出时的底部边距
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottom = if (imeVisible) imeInsets.bottom else systemInsets.bottom

            val top = if (setTop) {
                systemBars.top
            } else {
                0
            }
            val finalBottom = if (setBottom) {
                bottom
            } else {
                0
            }

            // 应用计算后的内边距
            v.setPadding(systemBars.left, top, systemBars.right, finalBottom)
            insets
        }
    }

    /**
     * 设置布局，子类可重写
     */
    protected open fun setContentView() {
        setContentView(this.getLayout())
    }

    // 强制子类实现的模板方法

    protected abstract fun getLayout(): Int // 返回布局 ID

    protected abstract fun initData() // 初始化数据逻辑

    protected abstract fun initView() // 初始化视图控件

    protected abstract fun initAction() // 初始化点击事件等交互

    protected val context: Context
        get() = this

    protected val name: String
        get() = this.javaClass.getSimpleName()
}
