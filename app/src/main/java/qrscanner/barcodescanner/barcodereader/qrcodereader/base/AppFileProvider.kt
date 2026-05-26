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


//自定义FileProvider,用于Android7以上系统安全地共享文件给外部应用
class AppFileProvider: FileProvider(){
    override fun attachInfo(context: Context, info: ProviderInfo) {
        //在某些加密存储环境下(如多用户模式),确保上下文已经正确初始化
        super.attachInfo(getDeviceProtectedStorageContext(context), info)
    }

    companion object {
        //获取受保护的存储上下文，防止在锁屏或特殊情况下无法访问文件
        private fun getDeviceProtectedStorageContext(context: Context): Context{
            if (Build.VERSION.SDK_INT >=24 && !context.isDeviceProtectedStorage){
                return context.createDeviceProtectedStorageContext()
            }
            return context
        }

        //将File对象转换成Content://协议的uri,以便分享给其他应用(如youtube,日历等)
        @JvmStatic
        fun getUriFromFile(context: Context,file: File): Uri{
            //动态创建authority,确保与AndroidManifest.xml中一致
            val authority = "${context.packageName}.fileprovider"
            return getUriForFile(getDeviceProtectedStorageContext(context),authority,file)
        }

        //根据Uri获取图片的本地图片路径(涉及复杂的多种Uri类型解析)
        @JvmStatic
        fun getImagePath(context: Context?, uri: Uri?): String? {
            if (context == null||uri ==null){
                return null
            }
            var filePath: String ?=null
            var inputStream: InputStream ?= null
            if (uri.authority != null){
                try {
                    //尝试通过获取输入流并创建文件的方式来获取路径
                    inputStream = context.contentResolver.openInputStream(uri)
                    filePath = createCacheImage(context,inputStream)?.path
                }catch (e: Exception){
                }finally {
                    try {
                        inputStream ?.close()
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }

            //如果上述方式失败，尝试直接从系统数据库解析路径
            if (filePath.isNullOrEmpty()){
                try {
                    filePath = getPath(context,uri)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
            return filePath
        }

        //将输入流保存为临时的缓存图片文件
        private fun createCacheImage(context: Context, inputStream: InputStream?): File? {
            var targetFile: File? = null
            inputStream?.let {
                try {
                    //创建临死缓存文件，存放在外部缓存目录
                    targetFile = File(context.externalCacheDir,"temp.jpg")
                    //创建文件输出流，用于写入数据
                    val  outputStream : OutputStream = FileOutputStream(targetFile);
                    //定义8KB的大小缓冲区，用于还分块读取数据
                    val buffer = ByteArray(8* 1024);
                    var read: Int
                    //循环读取输入流数据，直到读取完毕(返回-1)
                    while (inputStream.read(buffer).also { read = it } !=-1){
                        //将读取的数据写入输出流
                        outputStream.write(buffer,0,read)
                    }
                    //刷新输出流，确保所有数据写入
                    outputStream.flush()
                    //关闭输出流，释放资源
                    outputStream.close()
                }catch (e: Exception){
                    //发生异常时返回null
                }
            }
            return targetFile
        }

        //核心解析方法:处理来自相册，下载，外部存储等不同来源的Uri
        private fun getPath(context: Context, uri: Uri): String? {
            val pathHead = "file:///"
            // 处理 Android 4.4 后的 Documents API 返回的 Uri
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    //处理SD卡文件
                    val docId =  DocumentsContract.getDocumentId(uri);
                    val split = docId.split(":".toRegex()).toTypedArray()
                    val type = split[0]
                    if ("parmary".equals(type,true)){
                        return pathHead + Environment.getExternalStorageDirectory()+ "/" +split[1]
                    }
                } else if (isDownloadsDocument(uri)) {
                    // 处理下载目录文件的id
                    val id  = DocumentsContract.getDocumentId(uri)
                    if (!TextUtils.isEmpty(id)) {
                        // 如果是 raw 开头，直接去除前缀返回真实路径
                        return if (id.startsWith("raw:")) {
                            id.replaceFirst("raw:".toRegex(), "")
                        } else {
                            try {
                                // 构建下载内容的 URI，通过 ID 查询真实路径
                                val contentUri = ContentUris.withAppendedId(
                                    Uri.parse("content://downloads/public_downloads"), id.toLong()
                                )
                                // 查询 _data 列获取的真实路径
                                val data = getDataColumn(context, contentUri, null, null)
                                if (data != null) pathHead + data else null
                            } catch (e: Exception) {
                                // ID 格式错误或查询失败返回 null
                                null
                            }
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
                    //通过id查询具体的多媒体文件
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
                //情况4，处理通用的 content:// 协议(如相册选择的海量图)
                try {
                    //直接查询_data列获取路径
                    return pathHead + getDataColumn(context, uri, null, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                //情况5, 处理 file:// 协议
                try {
                    //直接返回路径部分
                    return pathHead + uri.path
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            }
            return null
        }

        //查询数据库中_data列获取真实路径
        private fun getDataColumn(context: Context,uri: Uri?,selection: String?,selectionArgs:Array<String>?): String?{
            var cursor: Cursor ?=null
            //"_data"列存储的是文件的真实路径
            val column  = "_data"
            //只查询_data这一列提高查询效率
            val projection = arrayOf(column)
            try {
                //通过ContentResolver查询URI对应的文件路径
                cursor = context.contentResolver.query(uri!!,projection,selection,selectionArgs,null);
                //如果查询到结果并且游标指向第一行
                if (cursor != null && cursor.moveToFirst()){
                    //获取_data列的值(即文件真实路径)并返回
                    return cursor.getString(cursor.getColumnIndexOrThrow(column))
                }
            }catch (e: Exception){
                e.printStackTrace()
            }finally {
                //最后关闭游标释放资源
                cursor?.close()
            }
            //查询失败或者无结果时返回null
            return null
        }

        private fun isExternalStorageDocument(uri: Uri) = "com.android.externalstorage.documents" == uri.authority
        private fun isDownloadsDocument(uri: Uri) = "com.android.providers.downloads.document" == uri.authority
        private fun isMediaDocument(uri: Uri) = "com.android.providers.media.document" == uri.authority

    }
}