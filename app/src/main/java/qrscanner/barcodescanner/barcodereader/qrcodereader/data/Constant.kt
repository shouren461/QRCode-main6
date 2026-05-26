package qrscanner.barcodescanner.barcodereader.qrcodereader.data

//全局定义常量类
object Constant{
    //Intent传递参数:选中主页面的哪个Tab(0:扫描,1:创建,2:历史,3:设置)
    const val EXTRA_SELECT_TAB = "key_select_tab"
    //快捷方式(Shortcuts) 的Action前缀
    const val EXTRA_IS_FROM_SHORTCUT = "extra_is_from_shortcut";
}
//SharePreference(SP)存储的Key定义
//是否需要显示欢饮引导页
const val SP_SHOW_WELCOME = "pref_ket_show_welcome"
//Activity跳转请求/结果码
//跳转到创建结果页面的请求码
const val REQUEST_CODE_CREATE_RESULT_PAGE = 1001
//关闭创建输入页面的结果码
const val RESULT_CODE_CLOSE_CREATE_PAGE = 1002