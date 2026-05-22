package qrscanner.barcodescanner.barcodereader.qrcodereader.page;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.drojian.qrcode.utillib.log.Logcat;
import com.drojian.qrcode.utillib.utils.StatusBarUtil;

import qrscanner.barcodescanner.barcodereader.qrcodereader.R;
import qrscanner.barcodescanner.barcodereader.qrcodereader.base.ActivityStack;
import qrscanner.barcodescanner.barcodereader.qrcodereader.base.BaseActivity;
import qrscanner.barcodescanner.barcodereader.qrcodereader.data.Constant;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.CreateFragment;
import qrscanner.barcodescanner.barcodereader.qrcodereader.page.history.HistoryFragment;
import qrscanner.barcodescanner.barcodereader.qrcodereader.util.AnalyticsHelper;

/**
 * 应用的主界面 Activity
 * 采用“单 Activity + 多 Fragment”的架构，负责管理底部的导航栏以及
 * “历史记录”和“创建”两个核心功能页面的切换。
 */
public class MainActivity extends BaseActivity {
    // 定义底部 Tab 的类型常量
    private static final int TAB_TYPE_HISTORY = 2; // 历史记录 Tab
    public static final int TAB_TYPE_CREATE = 4;  // 创建二维码 Tab

    // 两个主要的子页面 Fragment
    private HistoryFragment mHistoryFragment;
    private CreateFragment mCreateFragment;

    // 底部导航栏的 UI 控件引用（图标和文本）
    private ImageView mTabScanIV;
    private TextView mTabScanTV;
    private ImageView mTabHistoryIV;
    private TextView mTabHistoryTV;
    private ImageView mTabMoreIV;
    private TextView mTabMoreTV;
    private ImageView mTabCreateIV;
    private TextView mTabCreateTV;
    
    // 记录当前选中的 Tab 类型，默认显示“创建”页
    public int mCurSelectedTab = TAB_TYPE_CREATE;

    @Nullable
    private MainViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 设置状态栏背景色为白色，并设置图标为深色（沉浸式适配）
        StatusBarUtil.setStatusBarColor(this, Color.WHITE, true);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayout() {
        // 返回主页面的布局文件
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        // 初始化 ViewModel，用于处理 Activity 的数据逻辑
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    @Override
    protected void initView() {
        Window window = getWindow();
        // 保持屏幕常亮（方便用户在扫描或展示二维码时屏幕不熄灭）
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        // 初始化底部导航栏点击事件和初始状态
        initBottomTab();
        
        // 默认切换到“创建”页面
        switchFragment(TAB_TYPE_CREATE);
    }

    @Override
    protected void initAction() {
        // 这里可以放置一些全局的事件监听
    }

    public boolean isShowingFullAd = false;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 应用完全退出时，清理 Activity 栈管理数据
        ActivityStack.onAppExit();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 接管系统的返回键逻辑
     */
    @Override
    public void onBackPressed() {
        // 1. 如果当前在“历史”页面，优先尝试让历史页消耗返回事件（例如关闭编辑模式）
        if (mCurSelectedTab == TAB_TYPE_HISTORY && mHistoryFragment != null) {
            if (mHistoryFragment.maybeConsumeBackPressedEvent()) {
                return;
            }
        }

        // 2. 策略：如果当前不在“创建”页面，点击返回键不退出应用，而是跳转回“创建”页
        if (mCurSelectedTab != TAB_TYPE_CREATE) {
            onBottomTabSelect(TAB_TYPE_CREATE);
            return;
        }
        
        // 3. 如果已经在“创建”页，则执行默认的返回逻辑（通常是回到桌面）
        super.onBackPressed();
    }

    /**
     * 核心逻辑：切换 Fragment
     * 使用 hide/show 方式，避免 Fragment 重复创建并保持页面状态
     */
    public synchronized void switchFragment(int type) {
        mCurSelectedTab = type;
        try {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            // 检查 Fragment 是否已经存在（可能是从 SavedInstanceState 恢复的）
            if (mHistoryFragment == null) {
                Fragment maybeExistFragment = getSupportFragmentManager().findFragmentByTag("f2");
                if (maybeExistFragment instanceof HistoryFragment) {
                    mHistoryFragment = (HistoryFragment) maybeExistFragment;
                }
            }
            if (mCreateFragment == null) {
                Fragment maybeExistFragment = getSupportFragmentManager().findFragmentByTag("f4");
                if (maybeExistFragment instanceof CreateFragment) {
                    mCreateFragment = (CreateFragment) maybeExistFragment;
                }
            }

            // 先隐藏所有已存在的 Fragment
            if (mHistoryFragment != null) {
                fragmentTransaction.hide(mHistoryFragment);
            }
            if (mCreateFragment != null) {
                fragmentTransaction.hide(mCreateFragment);
            }
            
            // 根据点击的类型显示对应的 Fragment
            switch (type) {
                case TAB_TYPE_HISTORY:
                    if (mHistoryFragment == null) {
                        mHistoryFragment = new HistoryFragment();
                        fragmentTransaction.add(R.id.fl_fragment_container, mHistoryFragment, "f2");
                    } else {
                        fragmentTransaction.show(mHistoryFragment);
                    }
                    break;
                case TAB_TYPE_CREATE:
                default:
                    if (mCreateFragment == null) {
                        mCreateFragment = new CreateFragment();
                        fragmentTransaction.add(R.id.fl_fragment_container, mCreateFragment, "f4");
                    } else {
                        fragmentTransaction.show(mCreateFragment);
                    }
                    break;
            }
            // 提交事务，允许状态丢失（防止极端情况下的崩溃）
            fragmentTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            AnalyticsHelper.logException(e);
            Logcat.d("Exception " + e);
        }
    }

    /**
     * 初始化底部导航栏
     */
    private void initBottomTab() {
        mTabScanIV = findViewById(R.id.iv_tab_scan);
        mTabScanTV = findViewById(R.id.tv_tab_scan);

        mTabHistoryIV = findViewById(R.id.iv_tab_history);
        mTabHistoryTV = findViewById(R.id.tv_tab_history);

        mTabMoreIV = findViewById(R.id.iv_tab_more);
        mTabMoreTV = findViewById(R.id.tv_tab_more);

        mTabCreateIV = findViewById(R.id.iv_tab_creat);
        mTabCreateTV = findViewById(R.id.tv_tab_creat);

        // 扫描功能入口，目前处于暂未开放状态
        findViewById(R.id.ll_tab_scan).setOnClickListener(view -> {
            Toast.makeText(this, "该功能暂未开放", Toast.LENGTH_SHORT).show();
        });
        
        // 历史 Tab 点击监听
        findViewById(R.id.ll_tab_history).setOnClickListener(view -> {
            onBottomTabSelect(TAB_TYPE_HISTORY);
            AnalyticsHelper.logClickEvent("Main_history_click"); // 埋点统计
        });
        
        // 更多功能入口，目前处于暂未开放状态
        findViewById(R.id.ll_tab_more).setOnClickListener(view -> {
            Toast.makeText(this, "该功能暂未开放", Toast.LENGTH_SHORT).show();
        });

        //创建Tab 点击监听
        findViewById(R.id.ll_tab_creat).setOnClickListener(view -> {
            onBottomTabSelect(TAB_TYPE_CREATE);
            AnalyticsHelper.logClickEvent("Main_create_click");
        });

        // 初始化底栏状态：设置默认选中“创建”图标
        mTabScanIV.setImageResource(R.drawable.vector_ic_tab_scan_unselected);
        mTabScanTV.setTextColor(getResources().getColor(R.color.bottom_tab_unselected_color));
        mTabHistoryIV.setImageResource(R.drawable.vector_ic_tab_history_unselected);
        mTabHistoryTV.setTextColor(getResources().getColor(R.color.bottom_tab_unselected_color));
        mTabMoreIV.setImageResource(R.drawable.vector_ic_tab_more_unselected);
        mTabMoreTV.setTextColor(getResources().getColor(R.color.bottom_tab_unselected_color));
        mTabCreateIV.setImageResource(R.drawable.vector_ic_tab_creat_selected);
        mTabCreateTV.setTextColor(getResources().getColor(R.color.bottom_tab_selected_color));
    }



     //处理底部 Tab 选中的 UI 更新逻辑
    public void onBottomTabSelect(int type) {
        if (type == mCurSelectedTab) {
            return; // 如果已经是当前选中的 Tab，直接返回
        } else {
            switchFragment(type); // 否则切换 Fragment
        }
        
        // 更新底栏图标的颜色和样式
        switch (type) {
            case TAB_TYPE_HISTORY:
                mTabScanIV.setImageResource(R.drawable.vector_ic_tab_scan_unselected);
                mTabScanTV.setTextColor(getResources().getColor(R.color.bottom_tab_unselected_color));
                mTabHistoryIV.setImageResource(R.drawable.vector_ic_tab_history_selected);
                mTabHistoryTV.setTextColor(getResources().getColor(R.color.bottom_tab_selected_color));
                mTabMoreIV.setImageResource(R.drawable.vector_ic_tab_more_unselected);
                mTabMoreTV.setTextColor(getResources().getColor(R.color.bottom_tab_unselected_color));
                mTabCreateIV.setImageResource(R.drawable.vector_ic_tab_creat_unselected);
                mTabCreateTV.setTextColor(getResources().getColor(R.color.bottom_tab_unselected_color));
                break;
            case TAB_TYPE_CREATE:
            default:
                mTabScanIV.setImageResource(R.drawable.vector_ic_tab_scan_unselected);
                mTabScanTV.setTextColor(getResources().getColor(R.color.bottom_tab_unselected_color));
                mTabHistoryIV.setImageResource(R.drawable.vector_ic_tab_history_unselected);
                mTabHistoryTV.setTextColor(getResources().getColor(R.color.bottom_tab_unselected_color));
                mTabMoreIV.setImageResource(R.drawable.vector_ic_tab_more_unselected);
                mTabMoreTV.setTextColor(getResources().getColor(R.color.bottom_tab_unselected_color));
                mTabCreateIV.setImageResource(R.drawable.vector_ic_tab_creat_selected);
                mTabCreateTV.setTextColor(getResources().getColor(R.color.bottom_tab_selected_color));
                break;
        }
    }

    /**
     * 关键重写：不保留系统自动保存的状态
     * 解决 app 被系统回收或杀死后，重启时自动恢复 Fragment 导致的重叠或 ID 异常问题
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // 不调用 super 方法，防止 Fragment 被自动保存
        //super.onSaveInstanceState(outState);
    }

    /**
     * 启动 MainActivity 的统一方法
     * @param tabIndex 默认选中的 tab
     * @param isFromShortCut 是否来自桌面快捷方式
     */
    public static void startMe(Context context, int tabIndex, boolean isFromShortCut) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constant.EXTRA_SELECT_TAB, tabIndex);
        intent.putExtra(Constant.EXTRA_IS_FROM_SHORTCUT, isFromShortCut);
        context.startActivity(intent);
    }
}
