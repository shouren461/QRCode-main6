package com.drojian.qrcode.cameralib.view

import android.util.Size

data class UseSize(
    var width: Int = 0,
    var height: Int = 0
) {
    fun getRatio(): Float = 1f * width / height

    fun getBig(): Int = if (width > height) width else height

    fun getSmall(): Int = if (width > height) width else height

    fun toSize() = Size(width, height)

    override fun toString() = "$width $height ${getRatio()}"
}