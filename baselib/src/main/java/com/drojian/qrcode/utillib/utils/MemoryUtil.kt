package com.drojian.qrcode.utillib.utils

import android.os.Environment
import android.os.StatFs
import com.drojian.qrcode.utillib.log.LogHelper.log
import java.io.File

object MemoryUtil {

    fun getAvailableMemory(): Float {
        return try {
            var freeSize = 0f
            val path: File = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            //获取可用区块数量
            val availableBlocks = stat.availableBlocksLong
            freeSize = availableBlocks * blockSize / 1024.0f / 1024f
            if (freeSize < 0) {
                freeSize = stat.freeBytes / 1024.0f / 1024.0f
            }
            if (freeSize > 0) freeSize else 100.0f
        } catch (e: Throwable) {
            e.log()
            100.0f // 大于最小的值5即可
        }
    }

}