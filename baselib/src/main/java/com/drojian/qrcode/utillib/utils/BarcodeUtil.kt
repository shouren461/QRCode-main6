package com.drojian.qrcode.utillib.utils

import android.content.Context
import android.util.Base64
import com.drojian.qrcode.utillib.log.LogHelper.log
import com.drojian.qrcode.utillib.utils.EmojiUtil.countryCodeToEmoji
import org.json.JSONObject
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

object BarcodeUtil {
    private var list: List<BarcodeInfo>? = null

    /**
     * 获取条形码所属国家或地区
     */
    @JvmStatic
    fun getBarcodeArea(context: Context, barcodeFormat: String, barCodePrefix: String, showCountryName: Boolean = true): String? {
        if (barcodeFormat == "UPC_A" || barcodeFormat == "UPC_E") {
            //这两种属于美国/加拿大
            val amEmoji = countryCodeToEmoji("US")
            val caEmoji = countryCodeToEmoji("CA")
            return "$amEmoji /$caEmoji"
        }

        list?.let {
            //list不为空时，不需要再去assets中读取数据
            return getBarcodeArea(barCodePrefix, showCountryName)
        }

        //list为空时，从assets中读取数据
        try {
            val fileName = "attribution_barcodeprefix.txt"
            val encryptJsonStr = context.assets.open(fileName).bufferedReader().use {
                it.readText()
            }
            val jsonStr = decrypt(encryptJsonStr, "des_attribution_barcodeprefix")
            list = jsonToList(jsonStr)
        } catch (e: Exception) {
            e.log("条码所属国家/地区数据解析异常")
        }

        return getBarcodeArea(barCodePrefix, showCountryName)
    }

    private fun decrypt(input: String, password: String): String {
        //创建cipher对象
        val cipher = Cipher.getInstance("DES")
        //初始化cipher(参数：加密/解密模式)
        val kf = SecretKeyFactory.getInstance("DES")
        val keySpec = DESKeySpec(password.toByteArray())
        val key: Key = kf.generateSecret(keySpec)
        cipher.init(Cipher.DECRYPT_MODE, key)
        //base64解码
        val encrypt = cipher.doFinal(Base64.decode(input, DEFAULT_BUFFER_SIZE))
        return String(encrypt)
    }

    private fun getBarcodeArea(barCodePrefix: String, showCountryName: Boolean): String? {
        return list?.let {
            var result: String? = null
            loop@ for (item in it) {
                val code = item.barcodePrefix ?: continue@loop
                if (code.contains(" – ")) {
                    val range = code.split(" – ")
                    if (range[0] <= barCodePrefix && range[1] >= barCodePrefix) {
                        result = generateArea(item, showCountryName)
                        break@loop
                    }
                } else if (barCodePrefix == code) {
                    result = generateArea(item, showCountryName)
                    break@loop
                }
            }
            return result
        }
    }

    private fun generateArea(barcodeInfo: BarcodeInfo, showCountryName: Boolean): String {
        if (barcodeInfo.locale?.isNotEmpty() == true) {
            val emoji = countryCodeToEmoji(barcodeInfo.locale)
            return if (showCountryName) {
                "$emoji  ${barcodeInfo.attribute}"
            } else {
                emoji
            }

        }
        return "${barcodeInfo.attribute}"
    }

    private fun jsonToList(jsonStr: String): List<BarcodeInfo> {
        val list = ArrayList<BarcodeInfo>()
        //将得到json数据转换为一个json对象
        val jsonObject = JSONObject(jsonStr)
        //获取"data"的json对象,并将其转换为一个json数组
        val array = jsonObject.getJSONArray("data")
        //通过循环获取数据,并放入list集合中
        for (index in 0 until array.length()) {
            val jObject = array.getJSONObject(index)
            list.add(
                BarcodeInfo(
                    jObject.optString("en"), jObject.optString("code"), jObject.optString("locale")
                )
            )
        }
        return list
    }
}

private data class BarcodeInfo(
    val attribute: String? = null,
    val barcodePrefix: String?= null,
    val locale: String? = null
)