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

//应用的主界面Activity
// 采用"单Activity +多Fragment"的架构，负责管理底部的导航栏以及  "历史记录"和"创建"两个核心功能页面的切换
public class MainActivity extends BaseActivity {
    //定义底部的Tab的类型常量
    public static final int TAB_TYPE_HISTORY = 2;   //历史记录Tab
    public static final  int TAB_TYPE_CREATE = 4;  //创建二维码Tab

    //两个主要的子页面Fragment
    private HistoryFragment mHistoryFragment;
    private CreateFragment mCreateFragment;

    //底部导航栏的UI控件引用(图标和文本)
    private ImageView mTabScanIV;
    private TextView mTabScanTV;
    private ImageView mTabHistoryIV;
    private TextView mTabHistoryTV;
    private ImageView mTabMoreIV;
    private TextView mTabMoreTV;
    private ImageView mTabCreateIV;
    private TextView mTabCreateTV;

    //记录当前选中的Tab类型，默认显示未"创建"页
    public int mCurSelectedTab = TAB_TYPE_CREATE;
    //初始化MainActivity对应的MainViewModel
    @Nullable
    private MainViewModel viewModel;

    //调用onCreate()方法
    @Override
    protected void onCreate(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        //设置状态栏背景色为白色，并设置图标未深色(沉浸式适配)
        StatusBarUtil.setStatusBarColor(this,Color.WHITE,true);
        super.onCreate(savedInstanceState);
    }

    //初始化布局
    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }
    //初始化相关文件数据逻辑
    @Override
    protected void initData() {
        //初始化viewModel,用于处理Activity的数据逻辑
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }
    //初始化视图控件
    @Override
    protected void initView() {
        Window window = getWindow();
        //保持屏幕常亮(方便用户在扫描或者展示二维码时屏幕不熄灭)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //初始化底部导航栏点击事件和初始状态
        initBottomTab();
        // 从 Intent 中获取默认选中的 Tab，如果没有则使用初始值
        Intent intent = getIntent();
        if (intent != null) {
            int selectTab = intent.getIntExtra(Constant.EXTRA_SELECT_TAB, mCurSelectedTab);
            // 修正：如果传入的是 Constant 中定义的 index (0, 1, 2, 3)，
            // 需要映射到 MainActivity 中定义的常量。
            // 0:扫描(Toast), 1:创建(4), 2:历史(2), 3:设置(Toast)
            if (selectTab == 1) mCurSelectedTab = TAB_TYPE_CREATE;
            else if (selectTab == 2) mCurSelectedTab = TAB_TYPE_HISTORY;
            // 其他情况保留默认值或原值
        }
        switchFragment(mCurSelectedTab);
        updateBottomTabUI(mCurSelectedTab);
    }

    //绑定监听事件
    @Override
    protected void initAction() {
        //在此可以放置一些全局的监听事件
    }
    //进入交互期
    @Override
    protected void onResume() {
        super.onResume();
    }
    //退出交互期
    @Override
    protected void onPause() {
        super.onPause();
    }
    //当Activity已经存在并且处于运行状态(栈顶)，并且再次被启动时，用来接受新的数据，
    // 而不需要再次调用onCreate()创建Activity
    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
    }

    //退出可见期
    @Override
    protected void onStop() {
        super.onStop();
    }
    //销毁Activity，释放资源
    @Override
    protected void onDestroy() {
        //应用完全退出时，清理Activity栈管理状态
        ActivityStack.onAppExit();
        super.onDestroy();
    }
    //在这里可以监听物理按键 按下事件的处理器(比如后续可以重写双击退出应用程序界面)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
    //接管系统的返回键逻辑

    @Override
    public void onBackPressed() {
        //1,如果在"历史"，优先尝试让历史页消耗返回事件(例如关闭编辑模式)
        if (mCurSelectedTab == TAB_TYPE_HISTORY && mHistoryFragment != null){
            if (mHistoryFragment.maybeConsumeBackPressedEvent()){
                return;
            }
        }
        //2,如果当前不在"创建"页面,点击返回键不退出应用，而是跳转到"创建页"
        if (mCurSelectedTab != TAB_TYPE_HISTORY){
            //切换底部导航栏的UI状态
            onBottomTabSelect(TAB_TYPE_CREATE);
            return;
        }
        //3,如果已经在创建页，则执行默认的返回逻辑(通常是退出应用，回到桌面)
        super.onBackPressed();
    }
    //核心逻辑:切换Fragment  ->使用hide/show 方式，避免Fragment 重复创建并保持页面状态
    private void switchFragment(int type) {
        mCurSelectedTab  = type;
        try {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            //1,检查Fragment是否已经存在(可能是SaveInstanceState恢复的)
            if (mHistoryFragment == null){
                Fragment maybeExistFragment = getSupportFragmentManager().findFragmentByTag("f2");
                if (maybeExistFragment instanceof  HistoryFragment){
                    mHistoryFragment = (HistoryFragment) maybeExistFragment;
                }
            }
            if (mCreateFragment == null){
                Fragment mayExistFragment = getSupportFragmentManager().findFragmentByTag("f4");
                if (mayExistFragment instanceof CreateFragment){
                    mCreateFragment = (CreateFragment) mayExistFragment;
                }
            }
            //2,先隐藏所有已存在的Fragment
            if (mHistoryFragment != null){
                fragmentTransaction.hide(mHistoryFragment);
            }
            if (mCreateFragment != null){
                fragmentTransaction.hide(mCreateFragment);
            }
            //3,根据点击的类型显示对应的Fragment
            switch (type){
                case TAB_TYPE_HISTORY:
                    if (mHistoryFragment == null){
                        mHistoryFragment = new HistoryFragment();
                        fragmentTransaction.add(R.id.fl_fragment_container,mHistoryFragment,"f2");
                    }else {
                        fragmentTransaction.show(mHistoryFragment);
                    }
                    break;
                case TAB_TYPE_CREATE:
                default:
                    if (mCreateFragment == null){
                        mCreateFragment = new CreateFragment();
                        fragmentTransaction.add(R.id.fl_fragment_container,mCreateFragment,"f4");
                    }else {
                        fragmentTransaction.show(mCreateFragment);
                    }
                    break;
            }
            //4,提交事务，允许状态丢失(防止极端情况下的崩溃)
            fragmentTransaction.commitAllowingStateLoss();
        }catch (Exception e){
            AnalyticsHelper.logException(e);
            Logcat.e("Exception " + e);
        }
    }
    //初始化底部导航栏
    private void initBottomTab() {
        mTabScanIV  =findViewById(R.id.iv_tab_scan);
        mTabHistoryIV =findViewById(R.id.iv_tab_history);
        mTabMoreIV  =findViewById(R.id.iv_tab_more);
        mTabCreateIV  =findViewById(R.id.iv_tab_creat);

        mTabScanTV =findViewById(R.id.tv_tab_scan);
        mTabHistoryTV =findViewById(R.id.tv_tab_history);
        mTabMoreTV =findViewById(R.id.tv_tab_more);
        mTabCreateTV =findViewById(R.id.tv_tab_creat);

        //扫描功能入口，目前处在尚未开放状态
        findViewById(R.id.ll_tab_scan).setOnClickListener(view ->{
            Toast.makeText(this, "该功能暂未开放", Toast.LENGTH_SHORT).show();
        });
        //历史Tab,点击监听
        findViewById(R.id.ll_tab_history).setOnClickListener(view ->{
            onBottomTabSelect(TAB_TYPE_HISTORY);
            //埋点统计
            AnalyticsHelper.logClickEvent("Main_history_click");
        });
        //更多功能入口，目前处在尚未开放状态
        findViewById(R.id.ll_tab_more).setOnClickListener(view ->{
            Toast.makeText(this, "该功能暂未开放", Toast.LENGTH_SHORT).show();
        });
        //创建Tab,点击监听
        findViewById(R.id.ll_tab_creat).setOnClickListener(view ->{
            onBottomTabSelect(TAB_TYPE_CREATE);
            //埋点统计
            AnalyticsHelper.logClickEvent("Main_create_click");
        });
        //初始化底栏状态:设置默认选中状态"创建"图标
        mTabScanIV.setImageResource(R.drawable.vector_ic_tab_scan_unselected);
        mTabScanTV.setTextColor(getResources().getColor(R.color.bottom_tab_unselected_color));
        mTabHistoryIV.setImageResource(R.drawable.vector_ic_tab_history_unselected);
        mTabHistoryTV.setTextColor(getResources().getColor(R.color.bottom_tab_unselected_color));
        mTabMoreIV.setImageResource(R.drawable.vector_ic_tab_more_unselected);
        mTabMoreTV.setTextColor(getResources().getColor(R.color.bottom_tab_unselected_color));
        mTabCreateIV.setImageResource(R.drawable.vector_ic_tab_creat_selected);
        mTabCreateTV.setTextColor(getResources().getColor(R.color.bottom_tab_selected_color));

    }
    //处理底部Tab选中的UI更新逻辑
    public void onBottomTabSelect(int type) {
        if (type == mCurSelectedTab){
            return;   //如果是当前选中的Tab，直接返回
        }else {
            switchFragment(type); //否则切换Fragment
        }
        updateBottomTabUI(type);
    }

    private void updateBottomTabUI(int type) {
        //更新底栏图标的颜色和样式
        switch (type){
            case TAB_TYPE_HISTORY:
                //初始化底栏状态:设置默认选中状态"创建"图标
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
                //初始化底栏状态:设置默认选中状态"创建"图标
                mTabScanIV.setImageResource(R.drawable.vector_ic_tab_scan_unselected);
                mTabScanTV.setTextColor(getResources().getColor(R.color.bottom_tab_unselected_color));
                mTabHistoryIV.setImageResource(R.drawable.vector_ic_tab_history_unselected);
                mTabHistoryTV.setTextColor(getResources().getColor(R.color.bottom_tab_unselected_color));
                mTabMoreIV.setImageResource(R.drawable.vector_ic_tab_more_unselected);
                mTabMoreTV.setTextColor(getResources().getColor(R.color.bottom_tab_unselected_color));
                mTabCreateIV.setImageResource(R.drawable.vector_ic_tab_creat_selected);
                mTabCreateTV.setTextColor(getResources().getColor(R.color.bottom_tab_selected_color));
        }
    }

    //不保留系统自动保存的状态  ->解决app被系统回收或杀死后，重启后自动恢复Fragment导致的重叠 或 ID异常问题
    @SuppressLint("MissingSuperCall")
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //不调用super方法，防止Fragment被自动保存
        //super.onSaveInstanceState(outState);
    }

    //重启MainActivity的统一方法
    //@param tabIndex 默认选中的tab
    //@param isFromShortCut 是否来自桌面快捷方式
    public static void startMe(Context context,int tabIndex,boolean isFromShortCut){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constant.EXTRA_SELECT_TAB,tabIndex);
        intent.putExtra(Constant.EXTRA_IS_FROM_SHORTCUT,isFromShortCut);
        context.startActivity(intent);
    }
}