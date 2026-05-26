package qrscanner.barcodescanner.barcodereader.qrcodereader.page.history

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.toColorInt
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.drojian.qrcode.utillib.listener.DialogListener
import com.drojian.qrcode.viewlib.dialog.HistoryDeleteConfirmDialog
import qrscanner.barcodescanner.barcodereader.qrcodereader.R
import qrscanner.barcodescanner.barcodereader.qrcodereader.base.BaseFragment
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.MainActivity
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.history.CreateHistoryFragment.OnCreateSelectedModeChangeListener
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.history.ScanHistoryFragment.OnScanSelectedModeChangeListener
import qrscanner.barcodescanner.barcodereader.qrcodereader.util.AnalyticsHelper.logHistory

//历史的主页面Fragment  ->使用ViewPage2 组合了"扫描历史"和"创建历史" 两个子页面，提供了统一的编辑模式(选择，全选，删除)管理逻辑
class HistoryFragment : BaseFragment(), OnScanSelectedModeChangeListener, OnCreateSelectedModeChangeListener {
    // UI 控件：全选图标、删除图标、两个 Tab 文本及下划线游标
    private var selectAllIV: AppCompatImageView? = null
    private var deleteIV: AppCompatImageView? = null
    private var scanTabTV: AppCompatTextView? = null
    private var createTabTV: AppCompatTextView? = null
    private var scanTabCursorView: View? = null
    private var createTabCursorView: View? = null

    private var viewPager: ViewPager2 ?= null
    private var viewPagerAdapter: HistoryViewPagerAdapter ?= null

    // 两个子 Fragment实例
    private var scanHistoryFragment: ScanHistoryFragment ?= null
    private var createHistoryFragment: CreateHistoryFragment ?= null

    // 当前的选择模式：普通模式（1）或 编辑模式（2）
    private var currentSelectModel: Int = SELECT_MODEL_NORMAL

    // 记录两个子页面的数据量，用于控制删除按钮的显示/隐藏
    private var scanHistoryItemCount = -1
    private var createHistoryItemCount = -1

    // 默认初始显示的子 Tab 索引
    private var initialSubTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 从参数中获取初始 Tab 索引
        initialSubTab = arguments?.getInt(EXTRA_INITIAL_SUB_TAB, 0) ?: 0
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_history
    }

    /**
     * 初始化数据：创建子 Fragment 并设置监听器
     */
    //初始化数据:
    override fun initData() {
        logHistory("page_show") // 埋点：进入历史页
        scanHistoryFragment = ScanHistoryFragment()
        createHistoryFragment = CreateHistoryFragment()

        // 设置选择模式变化的监听，用于同步 TopBar 的状态
        scanHistoryFragment?.setSelectStateListener(this)
        createHistoryFragment?.setSelectStateListener(this)

        val fragmentList: MutableList<Fragment> = ArrayList()
        fragmentList.add(scanHistoryFragment!!)
        fragmentList.add(createHistoryFragment!!)

        val mActivity = activity ?: return
        viewPagerAdapter = HistoryViewPagerAdapter(this)
        viewPagerAdapter?.setFragmentList(fragmentList)
        // 绑定适配器并设置预加载
        viewPager?.setAdapter(viewPagerAdapter)
        viewPager?.setOffscreenPageLimit(2)
        // 设置初始显示的子 Tab
        if (initialSubTab != 0) {
            viewPager?.post {
                viewPager?.setCurrentItem(initialSubTab, false)
            }
        }
    }

    //初始化视图并绑定事件
    override fun initView(root: View?) {
        root ?: return
        selectAllIV = root.findViewById(R.id.iv_select_all)
        deleteIV = root.findViewById(R.id.iv_delete)
        viewPager = root.findViewById(R.id.vp_history)
        scanTabTV = root.findViewById(R.id.tab_scan)
        createTabTV = root.findViewById(R.id.tab_create)
        scanTabCursorView = root.findViewById(R.id.view_cursor_scan)
        createTabCursorView = root.findViewById(R.id.view_cursor_create)

        // ViewPager 页面切换监听
        viewPager?.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // 切换页面时重置为普通模式
                currentSelectModel = SELECT_MODEL_NORMAL
                updateTabView(position) // 更新 Tab 颜色和游标
                updateDeleteViewVisible() // 根据数据量检查删除按钮是否该显示
                updateSelectState() // 同步编辑状态 UI
                super.onPageSelected(position)
            }
        })

        // Tab 点击切换逻辑
        scanTabTV?.setOnClickListener { v: View? -> viewPager?.currentItem = 0 }
        createTabTV?.setOnClickListener { v: View? -> viewPager?.currentItem = 1 }

        // 全选按钮点击
        selectAllIV?.setOnClickListener { v: View? ->
            if (viewPager?.currentItem == 0) {
                scanHistoryFragment?.selectAll()
            } else {
                createHistoryFragment?.selectAll()
            }
        }

        // 删除/编辑图标点击逻辑
        deleteIV?.setOnClickListener { v: View? ->
            if (currentSelectModel == SELECT_MODEL_NORMAL) {
                // 如果是普通模式，点击进入“编辑模式”
                currentSelectModel = SELECT_MODEL_SELECTED
                updateSelectState()
            } else {
                // 如果已经在编辑模式，点击执行“删除”操作
                if (viewPager?.currentItem == 0) {
                    if (scanHistoryFragment!!.getSelectedItemCount() > 0) {
                        showDeleteConfirmDialog() // 有选中项，弹窗确认
                    } else {
                        currentSelectModel = SELECT_MODEL_NORMAL // 无选中项，切回普通模式
                        updateSelectState()
                    }
                } else {
                    if (createHistoryFragment!!.getSelectedItemCount() > 0) {
                        showDeleteConfirmDialog()
                    } else {
                        currentSelectModel = SELECT_MODEL_NORMAL
                        updateSelectState()
                    }
                }
            }
        }
        // 适配沉浸式状态栏
        enableInsetsViewTop(root.findViewById<View>(R.id.main))
    }

    override fun iAmShowing() {
        super.iAmShowing()
        // 页面可见时确保状态栏图标是深色的
        WindowInsetsControllerCompat(activity?.window ?: return, activity?.window?.decorView ?: return).isAppearanceLightStatusBars = true
    }

    override fun onStart() {
        super.onStart()
    }

    //更新Tab选中样式的UI
    private fun updateTabView(currentTab: Int) {
        if (currentTab == 0) {
            //扫描历史界面变亮
            scanTabTV?.setTextColor("#4991FF".toColorInt())
            createTabTV?.setTextColor("#FF9EA5B6".toColorInt())
            scanTabCursorView?.visibility = View.VISIBLE
            createTabCursorView?.visibility = View.INVISIBLE
        } else {
            //创建历史界面变亮
            scanTabTV?.setTextColor("#FF9EA5B6".toColorInt())
            createTabTV?.setTextColor("#4991FF".toColorInt())
            scanTabCursorView?.visibility = View.INVISIBLE
            createTabCursorView?.visibility = View.VISIBLE
        }
    }

    //根据当前选中模式，更新顶部(垃圾桶获全选)的UI
    private fun updateSelectState() {
        if (currentSelectModel == SELECT_MODEL_NORMAL) {
            // 普通模式：显示垃圾桶图标（带扫把样式）
            deleteIV?.setImageResource(R.drawable.ic_history_delete_sweep_black)
            selectAllIV?.setVisibility(View.GONE)
        } else {
            // 编辑模式：显示确认删除图标，并显示全选按钮
            deleteIV?.setImageResource(R.drawable.ic_history_delete_black)
            selectAllIV?.setVisibility(View.VISIBLE)
        }

        // 通知对应的子 Fragment 切换列表的选择模式
        if (viewPager?.currentItem == 0) {
            scanHistoryFragment?.changeSelectModel(currentSelectModel)
        } else {
            createHistoryFragment?.changeSelectModel(currentSelectModel)
        }
    }

    //执行真正的物理删除操作
    private fun deleteFragmentSelectedItems() {
        if (viewPager?.currentItem == 0) {
            scanHistoryFragment?.deleteHistoryItem()
        } else {
            createHistoryFragment?.deleteHistoryItem()
        }
        currentSelectModel = SELECT_MODEL_NORMAL
    }

    //弹出确认删除对话框
    private fun showDeleteConfirmDialog() {
        if (null != activity) {
            HistoryDeleteConfirmDialog.show(activity, listener = object : DialogListener {
                override fun onPositive() {
                    // 用户点击确定，执行删除
                    deleteFragmentSelectedItems()
                    updateSelectState()
                }

                override fun onNegative() {
                    //默认返回原界面
                }
            })
        }
    }

    //子Fragment 通知父容器选择模式已改变(如长按进入编辑模式)
    override fun onSelectModeChanged(selectMode: Int) {
        this.currentSelectModel = selectMode
        updateSelectState()
    }

    //子Fragment通知父容器扫描列表数量变化，以便控制顶部删除图标的显隐
    override fun onScanHistoryItemCountChanged(count: Int) {
        this.scanHistoryItemCount = count
        updateDeleteViewVisible()
    }
    //回调监听创建历史记录的列表数量变化
    override fun onCreateHistoryItemCountChanged(count: Int) {
        this.createHistoryItemCount = count
        updateDeleteViewVisible()
    }
    //如果当前页面没有数据，则不显示任何删除/编辑相关的按钮
    fun updateDeleteViewVisible() {
        if (viewPager != null) {
            val itemCount: Int = if (viewPager?.currentItem == 0) {
                scanHistoryItemCount
            } else {
                createHistoryItemCount
            }
            if (itemCount == 0) {
                selectAllIV?.setVisibility(View.GONE)
                deleteIV?.setVisibility(View.GONE)
            } else {
                deleteIV?.setVisibility(View.VISIBLE)
            }
        }
    }
    //处理返回键逻辑:如果是编辑模式，点击返回键先切回普通模式
    fun maybeConsumeBackPressedEvent(): Boolean {
        if (currentSelectModel == SELECT_MODEL_SELECTED) {
            currentSelectModel = SELECT_MODEL_NORMAL
            updateSelectState()
            return true
        }
        return false
    }

    companion object {
        const val SELECT_MODEL_NORMAL: Int = 1 // 普通显示状态
        const val SELECT_MODEL_SELECTED: Int = 2 // 编辑（多选）状态
        const val EXTRA_INITIAL_SUB_TAB = "extra_initial_sub_tab"

        @JvmStatic
        fun newInstance(subTab: Int): HistoryFragment {
            val fragment = HistoryFragment()
            val args = Bundle()
            args.putInt(EXTRA_INITIAL_SUB_TAB, subTab)
            fragment.arguments = args
            return fragment
        }
    }
}