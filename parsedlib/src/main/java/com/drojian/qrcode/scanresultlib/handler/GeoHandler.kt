package com.drojian.qrcode.scanresultlib.handler

import android.app.Activity
import com.drojian.qrcode.scanlib.scan.parse.format.ParseGeoModel
import com.drojian.qrcode.scanresultlib.BaseResultHandler
import com.drojian.qrcode.scanresultlib.R
import com.drojian.qrcode.scanresultlib.ResultHandlerConfig
import com.drojian.qrcode.scanresultlib.button.ParseAction
import com.drojian.qrcode.scanresultlib.util.HandlerUtils

/**
 * @author yangfengfan 2020-10-19
 */
class GeoHandler(activity: Activity, private val parseModel: ParseGeoModel, resultHandlerConfig: ResultHandlerConfig) : BaseResultHandler(activity, parseModel,resultHandlerConfig) {

    override val parseActionList: Array<ParseAction> = arrayOf(
        ParseAction.SHOW_ON_MAP,
        ParseAction.NAVIGATION,
        ParseAction.COPY,
        ParseAction.SHARE,
    )



    override fun handleButtonPress(action: ParseAction) {
        when (action) {
            ParseAction.SHOW_ON_MAP -> {
                HandlerUtils.openMap(activity, parseModel.getGeoURI())
            }
            ParseAction.NAVIGATION -> {
                HandlerUtils.getDirections(activity, parseModel.latitude, parseModel.longitude, parseModel.getGeoURI())
            }
            else -> super.handleButtonPress(action)
        }
    }

    override fun getDisplayList(): List<SimpleResult> {
        val list = arrayListOf(
            SimpleResult(R.string.parse_result_content_lng, parseModel.longitude.toString()),
            SimpleResult(R.string.parse_result_content_lat, parseModel.latitude.toString())
        )
        if (!parseModel.query.isNullOrEmpty() && !parseModel.query.isNullOrBlank()) {
            list.add(SimpleResult(R.string.parse_result_content_address, parseModel.query.toString()))
        }
        return list
    }

}