package com.drojian.qrcode.cameralib.view

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.widget.FrameLayout

/**
 * 外部直接使用CameraView就行，用来代替 SurfaceView
 * 隐藏 SurfaceView的 宽高计算细节，适配多分辨率
 */
abstract class CameraView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private var cameraSurfaceView: CameraSurfaceView? = null

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        cameraSurfaceView = CameraSurfaceView(context)
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(cameraSurfaceView, layoutParams)
    }

    fun setCameraPreviewSize(useSize: UseSize) {
        cameraSurfaceView?.setCameraPreviewSize(useSize)
    }

    fun getHolder(): SurfaceHolder? = cameraSurfaceView?.holder

    fun addSurfaceCallback(callback: SurfaceHolder.Callback) {
        cameraSurfaceView?.holder?.addCallback(callback)
    }

}