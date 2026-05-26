package qrscanner.barcodescanner.barcodereader.qrcodereader.data

import com.drojian.qrcode.utillib.utils.SPUtil

//仅显示一次或者持久化开关的配置管理类
//利用Kotlin的属性委托(Getter或者Setter方法)自动处理SharePreference 的读写
object OnceConfig {
    //是否需要显示欢迎界面
    @JvmStatic
    var showWelcome = false
        get() {
            field = SPUtil.getInstance().get(SP_SHOW_WELCOME,field);
            return field;
        }
        set(value) {
            field = value
            SPUtil.getInstance().set(SP_SHOW_WELCOME,field);
        }

}