package qrscanner.barcodescanner.barcodereader.qrcodereader.data;

import android.content.Context;

import com.drojian.qrcode.utillib.utils.DateUtil;
import com.drojian.qrcode.utillib.utils.SPUtil;
import com.drojian.qrcode.utillib.utils.VersionUtil;

import qrscanner.barcodescanner.barcodereader.qrcodereader.util.AnalyticsHelper;

//应用全局业务助手类 ->作用:负责管理应用中各种业务相关的持久化配置(如开关设置，应用打开次数，安装信息等)
//同时采用单例模式，确保全局只有一个实例，方便在项目中国任何地方存取数据
public class QRCodeHelper {
    //1.SharedPreferences 存储时使用的 Key（键），相当于数据库中的字段名
    private static final String PREF_KEY_COPY = "pref_key_copy"; // 自动复制到剪贴板开关的 Key
    private static final String PREF_KEY_APP_OPEN_TIME = "pref_key_app_open_time"; // 应用打开总次数的 Key
    private static final String PREF_KEY_FIRST_INSTALL_TIMES_MILLIS = "pref_key_first_install_times_millis_41"; // 首次安装时间戳的 Key
    private static final String PREF_KEY_FIRST_INSTALL_VERSION_CODE = "pref_key_first_install_version_code_41"; // 首次安装时版本号的 Key

    private static QRCodeHelper mInstance;

    //2.内存缓存变量（静态变量，方便快速读取，避免频繁读取磁盘文件带来的性能开销）
    private static boolean isCopy; //内存中保存的“是否自动复制”状态
    private static int appShowTimes; //内存中保存的“应用打开总次数”
    private static long firstInstallTimeMillis = 0; //内存中保存的“首次安装时间”
    private static int firstInstallVersionCode = -1; //内存中保存的“首次安装时的版本号”

    /**
     * 单例获取方法：获取全局唯一的 QRCodeHelper 实例
     * @param context 上下文对象
     * @return QRCodeHelper 实例
     */
    //单例获取方法:回去全局唯一的QRCodeHelper实例  @param context 上下文对象  @return QRCodeHelper实例
    public synchronized static QRCodeHelper getInstance(Context context){
        if (mInstance == null){
            mInstance = new QRCodeHelper();
            //在实例第一次创建时，从磁盘加载保存的数据到内存中
            init(context);
        }
        return mInstance;
    }

    //初始化:从SharePreference(磁盘)加载核心配置到内存变量中
    private static void init(Context context) {
        //1,读取“是否自动复制”，默认值为 false
        isCopy = SPUtil.getInstance().get(PREF_KEY_COPY, false);

        //2,读取“应用打开总次数”，默认值为 0
        appShowTimes = SPUtil.getInstance().get(PREF_KEY_APP_OPEN_TIME, 0);

        //3,逻辑:处理首次安装的时间记录
        firstInstallTimeMillis = SPUtil.getInstance().get(PREF_KEY_FIRST_INSTALL_TIMES_MILLIS, 0L);
        if (firstInstallTimeMillis == 0) {
            // 如果读取不到（说明是第一次启动），则记录当前系统时间并保存到磁盘
            firstInstallTimeMillis = System.currentTimeMillis();
            SPUtil.getInstance().set(PREF_KEY_FIRST_INSTALL_TIMES_MILLIS, firstInstallTimeMillis);
        }

        //4,逻辑:处理首次安装时的版本号记录
        firstInstallVersionCode = SPUtil.getInstance().get(PREF_KEY_FIRST_INSTALL_VERSION_CODE, -1);
        if (firstInstallVersionCode == -1) {
            // 如果没有记录过安装版本号
            if (appShowTimes > 0) {
                // 如果应用已经打开过多次但没记录，说明是旧版升级用户，版本号记为 0
                firstInstallVersionCode = 0;
            } else {
                // 否则说明是纯新用户，获取当前 App 的版本号并保存
                firstInstallVersionCode = (int) VersionUtil.getVersionCode(context);
            }
            SPUtil.getInstance().set(PREF_KEY_FIRST_INSTALL_VERSION_CODE, (int)firstInstallVersionCode);
        }
    }
}