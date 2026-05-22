package qrscanner.barcodescanner.barcodereader.qrcodereader.page.create.result

import qrscanner.barcodescanner.barcodereader.qrcodereader.R

/**
 * 创建二维码的枚举类型
 * 关联了每种类型的显示文本资源 ID 和对应的图标资源 ID
 */
enum class CreateType(var stringSrc: Int, var drawableIcon: Int) {
    // YouTube 视频、频道或链接类型
    YOUTUBE(R.string.youtube, R.drawable.vector_ic_youtube),
    // 日历事件类型
    CALENDAR(R.string.result_calendar, R.drawable.vector_ic_calendar)
}
