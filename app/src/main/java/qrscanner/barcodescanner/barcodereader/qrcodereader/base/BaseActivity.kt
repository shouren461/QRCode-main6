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

//所有Activity的基类:提供沉浸式状态栏，生命周期埋点等基础功能
abstract class BaseActivity : AppCompatActivity() {

    //判断应用程序是初次加载还是异常恢复数据
    @JvmField
    protected var isRestore: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //检查是否 是从异常状态恢复呢  ->检查文件报错状态
        if (savedInstanceState != null){
            //如果不为空，说明系统正在试图恢复之前的页面
            isRestore = true;
        }
        //初始化设置布局方式
        setContentView()
        //设置沉浸式外观(全屏显示，处理状态栏/导航栏等)
        makeAppEdgeToEdge()
        //按照规范顺序初始化
        initData()
        initView()
        initAction()
        //页面打开埋点统计
        logPageViewEvent(this.name)
    }
    //实现边到边设计，让内容可以延伸到状态栏和导航栏下面
    private fun makeAppEdgeToEdge(){
        enableEdgeToEdge()
        //针对不同的Android 版本微调系统导航栏颜色/对比度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            //API版本号大于等于27，Android版本大于7.0
            window.setNavigationBarContrastEnforced(false)
        }else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            window.navigationBarColor = Color.BLACK
        }

        //设置状态栏和导航栏颜色为深色(适用于浅色背景)
        WindowInsetsControllerCompat(window,window.decorView).isAppearanceLightNavigationBars = true
        WindowInsetsControllerCompat(window,window.decorView).isAppearanceLightStatusBars = true

        //针对主页(MainActivity)和其他页面设置不同的边距策略、
        if (this is MainActivity){
            //MainActivity 通常包含相机扫描页，不需要顶部边距，由内部Fragment 自行处理
            enableInsetsView(findViewById<View>(R.id.main), setTop = false)
        }else{
            //其他页面默预留顶部状态栏高度，防止内容被遮挡
            enableInsetsView(findViewById(R.id.main), setTop = true)
        }
    }

    //适配系统栏(状态栏/导航栏/输入法)的边距工具
    protected fun enableInsetsView(insetsViews: View?,setTop: Boolean = true,setBottom: Boolean = true){
        //如果视图为空，直接返回
        if (insetsViews == null){
            return
        }
        //设置窗口内边距监听器,监听系统窗口变化
        ViewCompat.setOnApplyWindowInsetsListener(insetsViews){v,insets ->
            //获取系统栏(状态栏+导航栏)的内边距
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            //处理输入法弹出场景
            //判断输入法是否可见
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            //获取输入法的底部内边距
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            //获取系统栏的底部内边距（通常是导航栏高度）
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            //如果输入法可见使用输入法高度，否则使用系统栏高度
            val  bottom = if (imeVisible) imeInsets.bottom else systemInsets.bottom

            //处理顶部内边距：根据setTop参数决定是否设置底部内边距
            val top  = if (setTop){
                systemBars.top
            }else{
                0
            }

            //处理底部内边距  根据setBottom 参数决定是否设置底部内边距
            val finalBottom = if (setBottom){
                systemBars.bottom
            }else{
                0
            }

            //应用计算后的内边距  ->左右使用系统的内边距，上下根据参数设置
            v.setPadding(systemBars.left,top,systemBars.right,finalBottom)
            //返回insets以便其他监听器继续处理
            insets
        }
    }

    //设置布局，子类可重写
    protected open fun setContentView(){
        setContentView(this.getLayout())
    }

    protected abstract fun getLayout() : Int //返回布局ID
    protected abstract fun initData()   //初始化数据逻辑
    protected abstract fun initView()    //初始化视图控件
    protected abstract fun initAction()   //初始化点击事件等交互
    protected val context: Context
        get() = this
    protected val name: String
        get() = this.javaClass.simpleName
}
