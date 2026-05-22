package com.drojian.qrcode.utillib.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import com.drojian.qrcode.utillib.log.LogHelper.log
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

object FileUtil {

    fun getImagePath(context: Context?, uri: Uri?): String? {
        if (context == null || uri == null) {
            return null
        }
        var filePath: String? = null
        var inputStream: InputStream? = null
        if (uri.authority != null) {
            try {
                inputStream = context.contentResolver.openInputStream(uri)
                filePath = createCacheImage(context, inputStream)?.path
            } catch (e: Exception) {
                e.log()
            } finally {
                try {
                    inputStream?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        if (filePath.isNullOrEmpty()) {
            try {
                filePath = getPath(context, uri)
            } catch (e: Throwable) {
                e.log()
            }
        }
        return filePath
    }

    private fun createCacheImage(context: Context, inputStream: InputStream?): File? {
        var targetFile: File? = null
        inputStream?.let {
            try {
                targetFile = File(context.externalCacheDir, "feedback_" + System.currentTimeMillis() + ".jpg")
                val outputStream: OutputStream = FileOutputStream(targetFile)
                val buffer = ByteArray(8 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
                outputStream.close()
            } catch (e: Exception) {
                e.log()
            }
        }
        return targetFile
    }


    private fun getPath(context: Context, uri: Uri): String? {
        val pathHead = "file:///"
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return pathHead + Environment.getExternalStorageDirectory() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
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
            try {
                return pathHead + getDataColumn(context, uri, null, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            try {
                return pathHead + uri.path
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
        return null
    }

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