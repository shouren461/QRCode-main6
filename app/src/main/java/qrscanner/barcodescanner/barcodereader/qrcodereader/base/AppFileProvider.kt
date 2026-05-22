package qrscanner.barcodescanner.barcodereader.qrcodereader.base

import android.content.ContentUris
import android.content.Context
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


/**
 * 自定义的 FileProvider，用于在 Android 7.0+ 系统中安全地共享文件给外部应用
 */
class AppFileProvider : FileProvider() {
    override fun attachInfo(context: Context, info: ProviderInfo) {
        // 在某些加密存储环境下（如多用户模式），确保上下文已正确初始化
        super.attachInfo(getDeviceProtectedStorageContext(context), info)
    }
    
    companion object {

        /**
         * 获取受保护的存储上下文，防止在锁屏或特殊环境下无法访问文件
         */
        private fun getDeviceProtectedStorageContext(context: Context): Context {
            if (Build.VERSION.SDK_INT >= 24 && !context.isDeviceProtectedStorage) {
                return context.createDeviceProtectedStorageContext()
            }
            return context
        }

        /**
         * 将 File 对象转换为 content:// 协议的 Uri，以便分享给其他应用（如 WhatsApp、Gmail）
         */
        @JvmStatic
        fun getUriFromFile(context: Context, file: File): Uri {
            // 动态构建 authority，确保与 AndroidManifest.xml 中一致
            val authority = "${context.packageName}.fileprovider"
            return getUriForFile(getDeviceProtectedStorageContext(context), authority, file)
        }

        /**
         * 根据 Uri 获取图片的本地绝对路径（涉及复杂的多种 Uri 类型解析）
         */
        @JvmStatic
        fun getImagePath(context: Context?, uri: Uri?): String? {
            if (context == null || uri == null) {
                return null
            }
            var filePath: String? = null
            var inputStream: InputStream? = null
            if (uri.authority != null) {
                try {
                    // 尝试通过读取输入流并创建缓存文件的方式来获取路径
                    inputStream = context.contentResolver.openInputStream(uri)
                    filePath = createCacheImage(context, inputStream)?.path
                } catch (e: Exception) {
                } finally {
                    try {
                        inputStream?.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            
            // 如果上述方式失败，尝试直接从系统数据库解析路径
            if (filePath.isNullOrEmpty()) {
                try {
                    filePath = getPath(context, uri)
                } catch (e: Throwable) {
                }
            }
            return filePath
        }
        
        /**
         * 将输入流保存为临时的缓存图片文件
         */
        private fun createCacheImage(context: Context, inputStream: InputStream?): File? {
            var targetFile: File? = null
            inputStream?.let {
                try {
                    targetFile = File(context.externalCacheDir, "tempFile.jpg")
                    val outputStream: OutputStream = FileOutputStream(targetFile)
                    val buffer = ByteArray(8 * 1024)
                    var read: Int
                    while (inputStream.read(buffer).also { read = it } != -1) {
                        outputStream.write(buffer, 0, read)
                    }
                    outputStream.flush()
                    outputStream.close()
                } catch (e: Exception) {
                }
            }
            return targetFile
        }
        
        /**
         * 核心解析方法：处理来自相册、下载、外部存储等不同来源的 Uri
         */
        private fun getPath(context: Context, uri: Uri): String? {
            val pathHead = "file:///"
            // 处理 Android 4.4 后的 Documents API 返回的 Uri
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // 处理 SD 卡文件
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        return pathHead + Environment.getExternalStorageDirectory() + "/" + split[1]
                    }
                } else if (isDownloadsDocument(uri)) {
                    // 处理下载目录文件
                    val id = DocumentsContract.getDocumentId(uri)
                    if (!TextUtils.isEmpty(id)) {
                        return if (id.startsWith("raw:")) {
                            id.replaceFirst("raw:".toRegex(), "")
                        } else try {
                            val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), id.toLong())
                            pathHead + getDataColumn(context, contentUri, null, null)
                        } catch (e: NumberFormatException) {
                            null
                        }
                    }
                } else if (isMediaDocument(uri)) {
                    // 处理多媒体库文件（图片、视频、音频）
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    when (type) {
                        "image" -> {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }
                        "video" -> {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        }
                        "audio" -> {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        }
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    try {
                        return pathHead + getDataColumn(context, contentUri, selection, selectionArgs)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return null
                }
            } else if ("content".equals(uri.scheme, ignoreCase = true)) {
                // 处理通用的 content:// 协议
                try {
                    return pathHead + getDataColumn(context, uri, null, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                // 处理 file:// 协议
                try {
                    return pathHead + uri.path
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            }
            return null
        }
        
        /**
         * 查询系统数据库中的 _data 列获取真实路径
         */
        private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)
            try {
                cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndexOrThrow(column))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
            return null
        }
        
        private fun isExternalStorageDocument(uri: Uri) = "com.android.externalstorage.documents" == uri.authority
        
        private fun isDownloadsDocument(uri: Uri) = "com.android.providers.downloads.documents" == uri.authority
        
        private fun isMediaDocument(uri: Uri) = "com.android.providers.media.documents" == uri.authority
        
    }
}