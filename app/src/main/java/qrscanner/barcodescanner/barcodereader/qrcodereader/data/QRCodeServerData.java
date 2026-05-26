package qrscanner.barcodescanner.barcodereader.qrcodereader.data;

import android.content.Context;
import android.text.TextUtils;

import com.drojian.qrcode.utillib.log.Logcat;
import com.drojian.qrcode.utillib.utils.LocaleUtil;

import org.json.JSONException;
import org.json.JSONObject;

import qrscanner.barcodescanner.barcodereader.qrcodereader.base.App;
import qrscanner.barcodescanner.barcodereader.qrcodereader.util.AnalyticsHelper;

//服务器数据管理类  ->负责保存从FireBase remote Config 或其他服务器接口获取的开关，数值等配置信息
public class QRCodeServerData {
    private static JSONObject  debugJSONObject = new JSONObject();

    private static String isShowSettingRate = "0";//是否显示页面的评分入口
    private static String isNewResultUi_41 ="";   //4.1版本是否启用新的结果扫描页UI
    private static String isRemoveScanBannerBG_41 = "1";  //4.1 版本是否移除结果页Banner广告的背景
    private static String isMainBannerShow = "1";      //历史，设置，创建页面的Banner广告是否显示

    //更新全局服务器配置(通常是在应用启动时同步)
    public static void ifNeedUpdate(Context context) {
        //通常从外部库或 Firebase 获取最新值
        isShowSettingRate = getRemoteConfig(context, "is_show_setting_rate_36", isShowSettingRate);
        isNewResultUi_41 = getRemoteConfig(context, "V41_is_new_result_ui", isNewResultUi_41);
        isRemoveScanBannerBG_41 = getRemoteConfig(context, "V41_is_remove_scan_banner_bg", isRemoveScanBannerBG_41);
        isMainBannerShow = getRemoteConfig(context, "V49_main_banner_show", isMainBannerShow);
        //测试环境下打印关键日志
        if (!App.isReleaseVersion()){
            String tagLog = "Firebase_";
            Logcat.d(tagLog+"V41_is_new_result_ui: "+isNewResultUi_41);
        }

    }
    //云端配置读取封装(此处逻辑，目前用于占位，需结合具体方案实现)
    private static String getRemoteConfig(final Context context, final String key, final String defaultValue) {
        String values = "";  //此处调用FirebaseRemoteConfig.getInstance().getString(key)
        if (TextUtils.isEmpty(values)){
            return defaultValue;
        }
        return values;
    }
}