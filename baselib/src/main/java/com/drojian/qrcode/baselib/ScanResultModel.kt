package com.drojian.qrcode.baselib

import android.graphics.Rect
import org.json.JSONObject

data class ScanResultModel(val timestamp: Long = 0L,
                           val codeFormat: CodeFormat = CodeFormat.QR_CODE,
                           val text: String = "",
                           var isFavorites: Boolean = false,
                           val note: String = "",
                           var showedContent: String = text,
                           val resultDetail: String = "") {

    var boundingBox: Rect? = null
    var data: ByteArray? = null

    // Model to Json
     fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put(JSON_NAME_CONTENT, text)
            jsonObject.put(JSON_NAME_FORMAT, codeFormat)
            jsonObject.put(JSON_NAME_TIMESTAMP, timestamp)
            jsonObject.put(JSON_NAME_SHOW_CONTENT, showedContent)
            jsonObject.put(JSON_NAME_IS_FAVORITES, isFavorites)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    companion object {
        private const val JSON_NAME_CONTENT = "qbl_j_c"
        private const val JSON_NAME_FORMAT = "qbl_j_f"
        private const val JSON_NAME_TIMESTAMP = "qbl_j_t"
        private const val JSON_NAME_SHOW_CONTENT = "qbl_j_s"
        private const val JSON_NAME_IS_FAVORITES = "qbl_j_fav"

        // Json to Model
        @JvmStatic
        fun toModel(jsonString: String): ScanResultModel? {
            try {
                val jsonObject = JSONObject(jsonString)
                val rawContent = jsonObject.getString(JSON_NAME_CONTENT)
                val barcodeFormat = CodeFormat.valueOf(jsonObject.getString(JSON_NAME_FORMAT))
                val timestamp = jsonObject.getLong(JSON_NAME_TIMESTAMP)
                val showedContent = jsonObject.getString(JSON_NAME_SHOW_CONTENT)

                val isFavorites = jsonObject.optBoolean(JSON_NAME_IS_FAVORITES, false)

                return ScanResultModel(
                    text = rawContent,
                    codeFormat = barcodeFormat,
                    timestamp = timestamp,
                    showedContent = showedContent,
                    isFavorites = isFavorites
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

    }

}