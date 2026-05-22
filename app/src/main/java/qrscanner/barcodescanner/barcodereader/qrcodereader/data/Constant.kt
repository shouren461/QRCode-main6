package qrscanner.barcodescanner.barcodereader.qrcodereader.data

/**
 * 全局常量定义类
 */
object Constant {


    // Intent 传递参数：选中主页面的哪个 tab (0: 扫描, 1: 创建, 2: 历史, 3: 设置)
    const val EXTRA_SELECT_TAB = "key_select_tab"
    // 快捷方式（Shortcuts）的 Action 前缀
    const val EXTRA_IS_FROM_SHORTCUT = "extra_is_from_shortcut"
}

// --- SharedPreferences (SP) 存储的 Key 定义 ---

// 是否显示过扫码输入对话框
const val SP_SHOW_SCAN_INPUT_DIALOG = "click_show_dialog"
// 是否显示过扫码输入提示对话框
const val SP_SHOW_SCAN_INPUT_TIPS_DIALOG = "has_show_tips"
// 是否需要显示欢迎页/引导页
const val SP_SHOW_WELCOME = "pref_ket_show_welcome"
// 记录用户是否永久拒绝了相机权限
const val SP_DENIED_CAMERA_FOREVER = "denied_camera_forever"
// 是否显示过评分优化引导对话框
const val SP_SHOW_RATE_OPTIMIZE_DIALOG = "show_rate_optimize_dialog"
// 是否显示过权限解释说明对话框
const val SP_SHOW_PERMISSION_EXPLAIN_DIALOG = "sp_show_permission_explain_dialog"
// 权限请求状态标记：从拒绝变为“应显示解释弹窗”的状态
const val SP_SHOW_RATIONAL_FALSE_TO_TRUE_STATE = "sp_show_rational_false_to_true_state"
// 权限被拒绝且勾选“不再询问”的次数记录
const val SP_NEVER_ASK_COUNT = "sp_never_ask_count"

// --- Activity 跳转请求/结果码 ---

// 跳转到创建结果页面的请求码
const val REQUEST_CODE_CREATE_RESULT_PAGE = 1001
// 关闭创建输入页面的结果码
const val RESULT_CODE_CLOSE_CREATE_PAGE = 1002