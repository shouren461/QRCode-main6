package com.drojian.qrcode.viewlib.feedback

class FeedbackHelper {
    var maxPhotoSize: Int = 1// 支持上传图片数量 -1不支持添加图片  0可无限添加
    var submitGoneOnFalse: Boolean = false // 提交按钮不可用时 Gone 或者 UnEnable
    var returnAllReasonList: Boolean = false // 返回整个Reason，不管是否选中
    var selectReasonToSubmit: Boolean = true // 必须选择Reason才能提交
}