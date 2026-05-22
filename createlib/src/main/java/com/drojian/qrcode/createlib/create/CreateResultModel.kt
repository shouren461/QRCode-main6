package com.drojian.qrcode.createlib.create

import org.json.JSONObject

open class CreateResultModel {

    var result: String = ""
    var codeFormat: com.drojian.qrcode.baselib.CodeFormat = com.drojian.qrcode.baselib.CodeFormat.QR_CODE
    var createFormat: CreateFormat = CreateFormat.Text
    var createTimeMillis: Long = 0
    var showText: String = ""
        get() {
            return if (field.isBlank()) {
                result
            } else {
                field
            }
        }
    var isFavorite: Boolean = false
    var jsonString: String = ""

    constructor() : this(result = "")

    constructor(result: String) : this(result = result, codeFormat = com.drojian.qrcode.baselib.CodeFormat.QR_CODE, createFormat = CreateFormat.Text)

    constructor(codeFormat: com.drojian.qrcode.baselib.CodeFormat) : this(result = "", codeFormat = codeFormat, createFormat = CreateFormat.Text)

    constructor(createFormat: CreateFormat) : this(result = "", codeFormat = com.drojian.qrcode.baselib.CodeFormat.QR_CODE, createFormat = createFormat)

    constructor(result: String, createFormat: CreateFormat) : this(
        result = result,
        codeFormat = com.drojian.qrcode.baselib.CodeFormat.QR_CODE,
        createFormat = createFormat
    )

    constructor(result: String, codeFormat: com.drojian.qrcode.baselib.CodeFormat, createFormat: CreateFormat) : this(
        result = result,
        codeFormat = codeFormat,
        createFormat = createFormat,
        createTimeMillis = System.currentTimeMillis(),
        showText = result,
        isFavorite = false,
        jsonString = ""
    )

    constructor(
        result: String,
        codeFormat: com.drojian.qrcode.baselib.CodeFormat,
        createFormat: CreateFormat,
        createTimeMillis: Long,
        showText: String,
        isFavorite: Boolean,
        jsonString: String
    ) {
        this.result = result
        this.codeFormat = codeFormat
        this.createFormat = createFormat
        this.createTimeMillis = createTimeMillis
        this.showText = showText
        this.isFavorite = isFavorite
        this.jsonString = jsonString
    }

    // result赋值
    open fun formatResult() {}

    fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put(JSON_NAME_CREATE_RESULT, result)
            jsonObject.put(JSON_NAME_BARCODE_FORMAT, codeFormat.name)
            jsonObject.put(JSON_NAME_CREATE_FORMAT, createFormat.name)
            jsonObject.put(JSON_NAME_CREATE_TIME_MILLIS, createTimeMillis)
            jsonObject.put(JSON_NAME_CREATE_SHOW_TEXT, showText)
            jsonObject.put(JSON_NAME_IS_FAVORITE, isFavorite)
            jsonObject.put(JSON_NAME_JSON_STRING, jsonString)
        } catch (e: Exception) {
        }
        return jsonObject
    }

    companion object {
        private const val JSON_NAME_CREATE_FORMAT = "jn_c_f"
        private const val JSON_NAME_BARCODE_FORMAT = "jn_b_f"
        private const val JSON_NAME_CREATE_RESULT = "jn_c_r"
        private const val JSON_NAME_CREATE_TIME_MILLIS = "jn_c_tm"
        private const val JSON_NAME_CREATE_SHOW_TEXT = "jn_c_st"
        private const val JSON_NAME_IS_FAVORITE = "jn_i_f"
        private const val JSON_NAME_JSON_STRING = "jn_j_s"

        @JvmStatic
        fun fromJson(jsonString: String): CreateResultModel? {
            try {
                val jsonObject = JSONObject(jsonString)
                val baseCreator = CreateResultModel()
                baseCreator.result = jsonObject.getString(JSON_NAME_CREATE_RESULT)
                baseCreator.codeFormat = com.drojian.qrcode.baselib.CodeFormat.valueOf(jsonObject.getString(JSON_NAME_BARCODE_FORMAT))
                baseCreator.createFormat = CreateFormat.valueOf(jsonObject.getString(JSON_NAME_CREATE_FORMAT))
                baseCreator.createTimeMillis = jsonObject.getLong(JSON_NAME_CREATE_TIME_MILLIS)
                baseCreator.showText = jsonObject.getString(JSON_NAME_CREATE_SHOW_TEXT)
                baseCreator.isFavorite = jsonObject.getBoolean(JSON_NAME_IS_FAVORITE)
                baseCreator.jsonString = jsonObject.getString(JSON_NAME_JSON_STRING)
                return baseCreator
            } catch (e: Exception) {
            }
            return null
        }

    }
}