package com.drojian.qrcode.cameralib.view

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceView

/**
 * 解决全屏时，屏幕拉伸预览变形问题
 * SurfaceView 宽高重新计算，适配三星等宽高奇葩机型
 */
class CameraSurfaceView : SurfaceView {

    private var cameraPreviewSize: UseSize? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setCameraPreviewSize(useSize: UseSize) {
        holder.setFixedSize(useSize.width, useSize.height)
        cameraPreviewSize = useSize
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        cameraPreviewSize?.let { size ->
            val needSetSize = viewOnMeasure(widthMeasureSpec, heightMeasureSpec, size)
            setMeasuredDimension(needSetSize.width, needSetSize.height)
        }
    }

    private fun viewOnMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int, cameraPreviewSize: UseSize): UseSize {
        val surfaceViewHeight = MeasureSpec.getSize(heightMeasureSpec)
        val surfaceViewWidth = MeasureSpec.getSize(widthMeasureSpec)
        //相机旋转90度，宽高 取反
        return getPreviewSize(cameraPreviewSize.height, cameraPreviewSize.width, surfaceViewWidth, surfaceViewHeight)
    }

    private fun getPreviewSize(cameraPreviewWidth: Int, cameraPreviewHeight: Int, viewWidth: Int, viewHeight: Int): UseSize {
        if (cameraPreviewWidth == viewWidth && cameraPreviewHeight == viewHeight) {
            return UseSize(viewWidth, viewHeight)
        }
        //计算出 view应该展示的宽度
        val viewShouldShowWidth = cameraPreviewWidth * viewHeight / cameraPreviewHeight
        return if (viewShouldShowWidth >= viewWidth) {
            UseSize(viewShouldShowWidth, viewHeight)
        } else {
            UseSize(viewWidth, cameraPreviewHeight * viewWidth / cameraPreviewWidth)
        }
    }
}