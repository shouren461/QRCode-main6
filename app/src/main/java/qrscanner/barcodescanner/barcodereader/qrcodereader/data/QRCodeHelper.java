package qrscanner.barcodescanner.barcodereader.qrcodereader.data;

import android.content.Context;

import com.drojian.qrcode.utillib.utils.DateUtil;
import com.drojian.qrcode.utillib.utils.SPUtil;
import com.drojian.qrcode.utillib.utils.VersionUtil;

import qrscanner.barcodescanner.barcodereader.qrcodereader.util.AnalyticsHelper;

/**
 * 应用全局业务助手类
 * 负责管理各种业务相关的持久化配置，如扫描次数统计、引导页显示状态等
 */
public class QRCodeHelper {
    // --- SharedPreferences 内部使用的 Key 常量 ---
    private static final String PREF_KEY_BEEP = "pref_key_beep"; // 扫码提示音开关
    private static final String PREF_KEY_COPY = "pref_key_copy"; // 自动复制到剪贴板开关
    private static final String PREF_KEY_APP_OPEN_TIME = "pref_key_app_open_time"; // 应用打开总次数
    private static final String PREF_KEY_APP_OPEN_TIME_HAVE_PERMISSION = "pref_key_app_open_time_have_permission"; // 有权限时的打开次数
    private static final String PREF_KEY_TODAY_TIME_STAMP = "pref_key_today_time_stamp"; // 记录“今天”的时间戳（用于跨天重置）
    private static final String PREF_KEY_TODAY_SCAN_TIME = "pref_key_today_scan_time"; // 今日扫描次数
    private static final String PREF_KEY_TOTAL_SCAN_TIME = "pref_key_total_scan_time"; // 历史扫描总次数
    private static final String PREF_KEY_IS_SHOW_PRODUCT_GUIDE = "pref_key_is_show_product_guide"; // 是否显示过商品引导
    private static final String PREF_KEY_IS_SHOW_SCAN_GUIDE = "pref_key_is_show_scan_guide"; // 是否显示过扫描引导
    private static final String PREF_KEY_SEARCH_ENGINE_CONFIG = "pref_key_search_engine_config"; // 搜索引擎设置（如 Google, Bing）
    private static final String PREF_KEY_IS_SHOW_SCAN_RESULT_OPINION = "pref_key_is_show_scan_result_opinion"; // 是否显示过结果反馈弹窗
    private static final String PREF_KEY_IS_SHOW_FEEDBACK_ACTIVITY = "pref_key_is_show_feedback_activity"; // 是否进入过反馈页
    private static final String PREF_KEY_IS_SCAN_RESULT_FEEDBACK_CLICK = "pref_key_is_scan_feedback_click"; // 结果页反馈按钮是否点过
    private static final String PREF_KEY_FIRST_INSTALL_TIMES_MILLIS = "pref_key_first_install_times_millis_41";
    private static final String PREF_KEY_FIRST_INSTALL_VERSION_CODE = "pref_key_first_install_version_code_41";
    private static final String PREF_KEY_SCAN_FEEDBACK_TEST_SHOW_TIMES = "pref_key_scan_feedback_test_show_times_41";
    private static final String PREF_KEY_SCAN_FEEDBACK_TEST_LAST_SHOW_TIME_Millis = "pref_key_scan_feedback_test_last_show_time_millis_41";

    private static final String DEBUG_CONFIG_COUNTRY = "debug_country_config";

    private static QRCodeHelper mInstance;

    // --- 内存中的变量缓存（避免频繁读取磁盘） ---
    private static boolean isBeep;
    private static boolean isCopy;
    private static int appShowTimes;
    private static int scanTimes; // 内存记录的单次运行扫码次数
    private static int totalScanTimes; 
    private static int todayScanTimes;
    public static boolean isScanThisOpen = false; // 本次打开是否进行过扫描
    private static long todayTimeStamp;
    private static int appShowTimesAndHasPermission;
    private static String searchEngineConfig = "Google";
    private static boolean isShowProductGuide = false;
    private static boolean isShowScanGuide = false;
    private static boolean isShowScanResultOpinion = false;
    private static boolean isShowFeedbackActivity = false;
    private static boolean isScanResultFeedbackClick = false;
    private static int scanFeedbackTestShowTimes_41 = 0;
    private static long lastScanFeedbackShowTimeMillis_41 = 0;
    private static long firstInstallTimeMillis = 0;
    private static int firstInstallVersionCode = -1;
    private static String debugConfigCountry = "";

    /**
     * 单例获取方法
     */
    public synchronized static QRCodeHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new QRCodeHelper();
            init(context);
        }
        return mInstance;
    }

    /**
     * 初始化：从 SharedPreferences 加载所有配置到内存中
     */
    private static void init(Context context) {
        debugConfigCountry = SPUtil.getInstance().get(DEBUG_CONFIG_COUNTRY, "");
        isBeep = SPUtil.getInstance().get(PREF_KEY_BEEP, false);
        isCopy = SPUtil.getInstance().get(PREF_KEY_COPY, false);
        appShowTimes = SPUtil.getInstance().get(PREF_KEY_APP_OPEN_TIME, 0);
        todayTimeStamp = SPUtil.getInstance().get(PREF_KEY_TODAY_TIME_STAMP, 0L);
        todayScanTimes = SPUtil.getInstance().get(PREF_KEY_TODAY_SCAN_TIME, 0);
        totalScanTimes = SPUtil.getInstance().get(PREF_KEY_TOTAL_SCAN_TIME, 0);
        appShowTimesAndHasPermission = SPUtil.getInstance().get(PREF_KEY_APP_OPEN_TIME_HAVE_PERMISSION, 0);
        isShowProductGuide = SPUtil.getInstance().get(PREF_KEY_IS_SHOW_PRODUCT_GUIDE, isShowProductGuide);
        isShowScanGuide = SPUtil.getInstance().get(PREF_KEY_IS_SHOW_SCAN_GUIDE, isShowScanGuide);
        searchEngineConfig = SPUtil.getInstance().get(PREF_KEY_SEARCH_ENGINE_CONFIG, searchEngineConfig);
        isShowScanResultOpinion = SPUtil.getInstance().get(PREF_KEY_IS_SHOW_SCAN_RESULT_OPINION, isShowScanResultOpinion);
        isShowFeedbackActivity = SPUtil.getInstance().get(PREF_KEY_IS_SHOW_FEEDBACK_ACTIVITY, isShowFeedbackActivity);
        isScanResultFeedbackClick = SPUtil.getInstance().get(PREF_KEY_IS_SCAN_RESULT_FEEDBACK_CLICK, isScanResultFeedbackClick);
        
        // 跨天检查逻辑：如果是新的一天，重置今日扫码次数并上报昨天的统计数据
        if (!DateUtil.INSTANCE.isSameDay(System.currentTimeMillis(), todayTimeStamp)) {
            if (todayScanTimes != 0) {
                AnalyticsHelper.logScanOneDayTimes(String.valueOf(todayScanTimes));
            }
            setTodayScanTime(0);
            setTodayTimeStamp(System.currentTimeMillis());
        }
        
        neverShareFromOut = SPUtil.getInstance().get(PREF_KEY_FIRST_SHARE_FROM_OUT, true);

        firstInstallTimeMillis = SPUtil.getInstance().get(PREF_KEY_FIRST_INSTALL_TIMES_MILLIS, firstInstallTimeMillis);
        if (firstInstallTimeMillis == 0) {
            firstInstallTimeMillis = System.currentTimeMillis();
            SPUtil.getInstance().set(PREF_KEY_FIRST_INSTALL_TIMES_MILLIS, firstInstallTimeMillis);
        }
        firstInstallVersionCode = SPUtil.getInstance().get(PREF_KEY_FIRST_INSTALL_VERSION_CODE, firstInstallVersionCode);
        if (firstInstallVersionCode == -1) {
            if (appShowTimes > 0) {
                firstInstallVersionCode = 0;
            } else {
                firstInstallVersionCode = (int) VersionUtil.getVersionCode(context);
            }
            SPUtil.getInstance().set(PREF_KEY_FIRST_INSTALL_VERSION_CODE, (int)firstInstallVersionCode);
        }
        lastScanFeedbackShowTimeMillis_41 = SPUtil.getInstance().get(PREF_KEY_SCAN_FEEDBACK_TEST_LAST_SHOW_TIME_Millis, lastScanFeedbackShowTimeMillis_41);
        scanFeedbackTestShowTimes_41 = SPUtil.getInstance().get(PREF_KEY_SCAN_FEEDBACK_TEST_SHOW_TIMES,
                scanFeedbackTestShowTimes_41);
    }

    // --- 各类配置项的 Getter 和 Setter (自动同步到 SP) ---

    public boolean isBeep() {
        return isBeep;
    }

    public void setIsBeep(boolean isBeep) {
        QRCodeHelper.isBeep = isBeep;
        SPUtil.getInstance().set(PREF_KEY_BEEP, isBeep);
    }

    public void setDebugCountry(String country) {
        debugConfigCountry = country;
        SPUtil.getInstance().set(DEBUG_CONFIG_COUNTRY, country);
    }

    public static String getDebugCountry() {
        return debugConfigCountry;
    }

    public boolean isCopy() {
        return isCopy;
    }

    public void setIsCopy(boolean isCopy) {
        QRCodeHelper.isCopy = isCopy;
        SPUtil.getInstance().set(PREF_KEY_COPY, isCopy);
    }


    public int getAppOpenTimes() {
        return appShowTimes;
    }

    public void setAppOpenTimes() {
        appShowTimes = appShowTimes + 1;
        SPUtil.getInstance().set(PREF_KEY_APP_OPEN_TIME, appShowTimes);
    }


    /**
     * 增加一次扫描计数（包括单次运行次数、今日次数、历史总次数）
     */
    public static void addScanTime() {
        scanTimes++;
        totalScanTimes++;
        SPUtil.getInstance().set(PREF_KEY_TOTAL_SCAN_TIME, totalScanTimes);
    }

    public static int getScanTimes() {
        return scanTimes;
    }

    public static long getTodayTimeStamp() {
        return todayTimeStamp;
    }

    private static void setTodayTimeStamp(long timeStamp) {
        todayTimeStamp = timeStamp;
        SPUtil.getInstance().set(PREF_KEY_TODAY_TIME_STAMP, timeStamp);
    }

    public static void setTodayScanTime(int scanTimes) {
        todayScanTimes = scanTimes;
        SPUtil.getInstance().set(PREF_KEY_TODAY_SCAN_TIME, todayScanTimes);
    }

    public static int getTodayScanTimes() {
        return todayScanTimes;
    }

    /**
     * 有相机权限的情况下打开次数
     */
    public void setAppOpenTimesWhenHasPermission() {
        appShowTimesAndHasPermission = appShowTimesAndHasPermission + 1;
        SPUtil.getInstance().set(PREF_KEY_APP_OPEN_TIME_HAVE_PERMISSION, appShowTimesAndHasPermission);
    }


    public boolean getIsShowProductGuide() {
        return isShowProductGuide;
    }

    public void setIsShowProductGuide() {
        isShowProductGuide = true;
        SPUtil.getInstance().set(PREF_KEY_IS_SHOW_PRODUCT_GUIDE, isShowProductGuide);
    }

    public boolean getIsShowScanGuide() {
        return isShowScanGuide;
    }

    public void setIsShowScanGuide() {
        isShowScanGuide = true;
        SPUtil.getInstance().set(PREF_KEY_IS_SHOW_SCAN_GUIDE, isShowScanGuide);
    }

    public static String getSearchEngineConfig() {
        return searchEngineConfig;
    }

    public static void setSearchEngineConfig(String searchEngineConfig) {
        QRCodeHelper.searchEngineConfig = searchEngineConfig;
        SPUtil.getInstance().set(PREF_KEY_SEARCH_ENGINE_CONFIG, searchEngineConfig);
    }

    public static boolean isIsShowScanResultOpinion() {
        return isShowScanResultOpinion;
    }

    public static void setIsShowScanResultOpinion(boolean isShowScanResultOpinion) {
        SPUtil.getInstance().set(PREF_KEY_IS_SHOW_SCAN_RESULT_OPINION, isShowScanResultOpinion);
        QRCodeHelper.isShowScanResultOpinion = isShowScanResultOpinion;
    }

    public static boolean isIsShowFeedbackActivity() {
        return isShowFeedbackActivity;
    }

    public static void setIsShowFeedbackActivity(boolean isShowFeedbackActivity) {
        SPUtil.getInstance().set(PREF_KEY_IS_SHOW_FEEDBACK_ACTIVITY, isShowFeedbackActivity);
        QRCodeHelper.isShowFeedbackActivity = isShowFeedbackActivity;
    }

    public static boolean isScanResultFeedbackClick() {
        return isScanResultFeedbackClick;
    }

    public static void setScanResultFeedbackClick(boolean isScanResultFeedbackClick) {
        SPUtil.getInstance().set(PREF_KEY_IS_SCAN_RESULT_FEEDBACK_CLICK, isScanResultFeedbackClick);
        QRCodeHelper.isScanResultFeedbackClick = isScanResultFeedbackClick;
    }


    // 用户还未使用过外部分享图片识别的功能
    private static boolean neverShareFromOut;
    private static final String PREF_KEY_FIRST_SHARE_FROM_OUT = "pref_key_first_share_from_out";

    // 用户是否从未使用过从外部分享图片识别的功能
    public static boolean isNeverShareFromOut() {
        return neverShareFromOut;
    }

    // 已经完成分享操作了
    public static void completeFirstShareFromOut() {
        QRCodeHelper.neverShareFromOut = false;
        SPUtil.getInstance().set(PREF_KEY_FIRST_SHARE_FROM_OUT, neverShareFromOut);
    }

    public static void addTestScanFeedbackShowTimes() {
        lastScanFeedbackShowTimeMillis_41 = System.currentTimeMillis();
        SPUtil.getInstance().set(PREF_KEY_SCAN_FEEDBACK_TEST_LAST_SHOW_TIME_Millis, lastScanFeedbackShowTimeMillis_41);
        scanFeedbackTestShowTimes_41++;
        SPUtil.getInstance().set(PREF_KEY_SCAN_FEEDBACK_TEST_SHOW_TIMES, scanFeedbackTestShowTimes_41);
    }


    public static boolean needShowTestScanFeedback() {
        if (scanFeedbackTestShowTimes_41 >= 3) {
            return false;
        }
        return System.currentTimeMillis() - lastScanFeedbackShowTimeMillis_41 > 1000 * 60 * 60 * 24;
    }

    public static long getFirstInstallVersionCode() {
        return firstInstallVersionCode;
    }

}