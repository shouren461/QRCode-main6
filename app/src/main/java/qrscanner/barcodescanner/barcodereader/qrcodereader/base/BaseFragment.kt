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


/**
 * 所有 Fragment 的基类，规范了生命周期管理和视图初始化
 */
abstract class BaseFragment : Fragment() {
    // 标记当前 Fragment 是否处于隐藏状态（例如在 ViewPager 中或通过 add/hide 切换时）
    private var mIsHidden = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(getLayoutResId(), container, false)
        // 按照顺序执行初始化
        initData()
        initView(root)
        return root
    }

    override fun onResume() {
        // 只有当 Fragment 真正可见时才触发“正在显示”的回调
        if (!mIsHidden) {
            iAmShowing()
        }
        super.onResume()
    }

    override fun onPause() {
        // 只有当 Fragment 真正离开时才触发“正在隐藏”的回调
        if (!mIsHidden) {
            iAmHide()
        }
        super.onPause()
    }

    /**
     * 当 Fragment 的隐藏状态改变时调用（常用于手动管理多 Fragment 切换的情况）
     */
    override fun onHiddenChanged(hidden: Boolean) {
        mIsHidden = hidden
        if (mIsHidden) {
            iAmHide()
        } else {
            iAmShowing()
        }
    }

    /**
     * 子类可重写，当页面显示给用户时触发（可用于埋点或启动动画）
     */
    protected open fun iAmShowing() {
        d(this.name + " iAmShowing")
    }

    /**
     * 子类可重写，当页面离开用户视线时触发（可用于停止动画或暂停任务）
     */
    protected open fun iAmHide() {
        d(this.name + " iAmHide")
    }

    // 强制子类实现的模板方法

    protected abstract fun getLayoutResId(): Int

    protected abstract fun initData()

    protected abstract fun initView(root: View?)

    protected val name: String
        get() = this.javaClass.getSimpleName()

    /**
     * 处理返回键逻辑，如果子类想拦截返回键，返回 true
     */
    fun onBackPressed(): Boolean {
        return false
    }

    /**
     * 自动处理顶部状态栏边距，防止内容被状态栏遮挡
     */
    protected fun enableInsetsViewTop(insetsViews: View?) {
        if (insetsViews == null) {
            return
        }
        ViewCompat.setOnApplyWindowInsetsListener(insetsViews) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }
}
