package qrscanner.barcodescanner.barcodereader.qrcodereader.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.drojian.qrcode.utillib.log.Logcat.d



//所有Fragment的基类，规范了生命周期和视图初始化
abstract class BaseFragment : Fragment() {
    //判断当前Fragment 是否处于隐藏状态(例如在ViewPager中通过add/hide切换时)
    private var mIsHidden = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(getLayoutResId(),container,false)//所有Activity的基类:提供沉浸式状态栏，生命周期埋点等基础功能
        //按照顺序初始化
        initView(root)   //先初始化视图(找到那些findViewById的控件)
        initData()       //初始化数据(把数据塞进控件里)
        return root
    }

    override fun onResume() {
        //仅有Fragment 真正可见时才触发"正在显示的回调"
        if (!mIsHidden){
            iAmShowing()
        }
        super.onResume()
    }

    override fun onPause() {
        //仅当Fragment真正离开时才触发"正在隐藏的回调"
        if (!mIsHidden){
            iAmHidden()
        }
        super.onPause()
    }

    //当Fragment状态改变时调用(常用于手动管理多Fragment切换的情况)
    override fun onHiddenChanged(hidden: Boolean) {
        mIsHidden = hidden
        if (mIsHidden){
            iAmHidden()
        }else{
            iAmShowing()
        }
    }

    //子类页面可重写，当页面离开用户视线时触发(可用于停止动画或者暂停任务)
    protected open fun iAmHidden() {
        d(this.name +"iAmHidden")
    }
    //子类页面可重写，当页面显示给用户时触发(用于埋点或启动动画)
    protected open fun iAmShowing() {
        d(this.name+"iAmShowing")
    }

    //初始化布局资源
   protected abstract fun getLayoutResId(): Int

    //初始化视图控件
   protected abstract fun initView(root: View?)
    //初始化数据逻辑
    protected abstract fun initData()
    protected val  name: String
        get() = this.javaClass.getSimpleName()

    //处理返回键逻辑 -> 如果子类向拦截返回键，返回true
    fun onBackPressed(): Boolean{
        return false
    }
    //自动处理顶部状态栏边距，防止内容被状态栏遮挡
    protected fun enableInsetsViewTop(insetsView:View?){
        if (insetsView == null){
            return
        }
        ViewCompat.setOnApplyWindowInsetsListener(insetsView){ v,insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            //初始化视图布局
            v.setPadding(systemBars.left,systemBars.top,systemBars.right,0)
            insets
        }
    }

}