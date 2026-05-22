package com.drojian.qrcode.scanlib.scan.parse.format

import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat

/**
 * @author yangfengfan 2020-08-27
 *
 * VIN【vehicle identification number 车辆识别代号】解析结果
 */
data class ParseVINModel(
    val vin: String? = null,
    val worldManufacturerID: String? = null,
    val vehicleDescriptorSection: String? = null,
    val vehicleIdentifierSection: String? = null,
    val countryCode: String? = null,
    val vehicleAttributes: String? = null,
    val modelYear: Int = 0,
    val plantCode: Char = 0.toChar(),
    val sequentialNumber: String? = null,
) : BaseParseModel(ParsedFormat.VIN) {

    override fun getShowText(): String {
        val result = StringBuilder(50)
        result.append(worldManufacturerID).append(' ')
        result.append(vehicleDescriptorSection).append(' ')
        result.append(vehicleIdentifierSection).append('\n')
        if (countryCode != null) {
            result.append(countryCode).append(' ')
        }
        result.append(modelYear).append(' ')
        result.append(plantCode).append(' ')
        result.append(sequentialNumber).append('\n')
        return result.toString()
    }
}