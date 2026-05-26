package qrscanner.barcodescanner.barcodereader.qrcodereader.page

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.utillib.log.Logcat
import com.drojian.qrcode.utillib.utils.DimensionUtil
import com.drojian.qrcode.utillib.utils.StatusBarUtil
import qrscanner.barcodescanner.barcodereader.qrcodereader.R
import qrscanner.barcodescanner.barcodereader.qrcodereader.base.App
import qrscanner.barcodescanner.barcodereader.qrcodereader.base.BaseActivity
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.Constant
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.OnceConfig
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.QRCodeHelper
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.QRCodeServerData


//欢迎页/启动页 Activity  ->用于冷启动时的初始化和数据加载 ,根据屏幕类型选择不同的布局，并自动跳转到主页
class WelcomeActivity : BaseActivity() {

    //判断当前页手机是否是折叠屏手机
    private fun isFoldScreeen(): Boolean{
        //计算屏幕宽高比 ，折叠屏通常宽高比更接近1:1 (小于1.5)
        return DimensionUtil.screenHeightPixels(this)/ DimensionUtil.screenWidthPixels(this)
            .toFloat() <1.5
    }

    //选择布局文件，普通屏幕使用activity_welcome ，折叠屏采用activity_welcome2
    override fun getLayout() = if (isFoldScreeen()){
        R.layout.activity_welcome_2
    }else{
        R.layout.activity_welcome
    }
    //Activity 创建时的初始化 -> 设置状态栏样式和全屏模式
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //隐藏状态栏文字，使其图标变成白色图标
        StatusBarUtil.hideStatusBarText(this)
        //开启全屏模式，隐藏状态栏和导航栏
        StatusBarUtil.openFullScreenModel(this)

    }
    //初始化数据 ->执行启动时的数据记载，并自动跳转到主页
    override fun initData() {
        //检查是否需要更新服务器数据
        QRCodeServerData.ifNeedUpdate(this)
        //初始化二维码工具类
        QRCodeHelper.getInstance(this)
        //判断是否是首次打开数据(用于展示引导页)
        App.isFirstOpen = !OnceConfig.showWelcome
        //跳转到主页MainActivity
        startActivity(Intent(this, MainActivity::class.java).apply {
            //传递Intent的action (如扫描二维码的action)
            action = intent.action
            //传递是否是从快捷方式打开的标识
            putExtra(
                Constant.EXTRA_IS_FROM_SHORTCUT,
                intent.getBooleanExtra(Constant.EXTRA_IS_FROM_SHORTCUT,false)
            )
        })
        //关闭当前欢迎页
        this.finish()
    }
    //初始化视图 -> 设置窗口内边距，适配系统导航栏
    override fun initView() {
        //不设置顶部内边距，设置底部内边栏适配导航栏
        enableInsetsView(findViewById(R.id.main),false,true)
    }
    //初始化点击事件 ->设置继续按钮和隐私政策文本的交互
    override fun initAction() {
        //1,继续按钮点击事件
        findViewById<View>(R.id.tv_continue).setOnClickListener {
            //标记已经展示过欢迎页,下次不再提示
            OnceConfig.showWelcome = true
            //跳转到主页
            startActivity(Intent(this, MainActivity::class.java).apply {
                action = intent.action
                putExtra(
                    Constant.EXTRA_IS_FROM_SHORTCUT,
                    intent.getBooleanExtra(Constant.EXTRA_IS_FROM_SHORTCUT,false)
                )
            })
            //关闭欢迎页
            this.finish()
        }
        //隐私政策  -文本处理
        try {
            val tvPrivice = findViewById<TextView>(R.id.tv_privacy)
            val privacyPolice  = "Privacy police"
            //获取隐私政策提示文本，将"privacy police"嵌入其中
            val text = getString(R.string.privacy_policy_tip,privacyPolice)
            //创建可点击的span,用于实现链接效果
            val clickableSpan : ClickableSpan = object : ClickableSpan() {
                override fun onClick(p0: View) {
                    //点击隐私政策的处理效果，目前没反应
                }
            }
            //创建富文本对象
            val style = SpannableStringBuilder(text)
            val startIndex = text.indexOf(privacyPolice)
            //为"privacy police"设置可点击事件
            style.setSpan(
                clickableSpan,
                startIndex,
                startIndex+privacyPolice.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // 为"privacy police"设置蓝色背景
            val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#4991FF"))
            style.setSpan(
                foregroundColorSpan,
                startIndex,
                startIndex + privacyPolice.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            //设置TextView 支持链接点击
            tvPrivice?.movementMethod  = LinkMovementMethod.getInstance()
            tvPrivice?.text = style
        }catch (e: Exception){
            //发生异常时打印日志
            e.log()
        }

    }


    //处理新的Intent  ->当Activity已经存在时，系统会调用此方法  @param intent 新的启动Intent
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        //更新当前的Intent的action
        getIntent().action = intent.action
    }

    //静态方法区域
    companion object{
        //启动WelcomeActivity的静态方法 ->@param context 上下文
        //                         ->@param action (如扫描二维码的Action)
        //                         ->@param isFromShortCut 是否是从快捷方式启动
        @JvmStatic
        fun startMe(context: Context,action: String? ,isFromShortCut: Boolean){
            var starter =  Intent(context, WelcomeActivity::class.java).apply {
                this.action = action
                //FLAG_ACTIVITY_CLEAR_TOP:如果Activity已经存在，清除其上所有的Activity
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(Constant.EXTRA_IS_FROM_SHORTCUT,isFromShortCut)
            }
            //启动Activity
            context.startActivity(starter)
        }
    }



}