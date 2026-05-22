package com.drojian.qrcode.scanresultlib.handler

import android.app.Activity
import com.drojian.qrcode.scanlib.scan.parse.format.ParseBarCodeModel
import com.drojian.qrcode.scanresultlib.BaseResultHandler
import com.drojian.qrcode.scanresultlib.ResultHandlerConfig
import com.drojian.qrcode.scanresultlib.button.ParseAction
import com.drojian.qrcode.scanresultlib.util.HandlerUtils

/**
 * @author yangfengfan 2020-10-19
 */
class BarCodeHandler(activity: Activity, val parsedModel: ParseBarCodeModel, resultHandlerConfig: ResultHandlerConfig) :
    BaseResultHandler(activity, parsedModel, resultHandlerConfig) {


    override val parseActionList: Array<ParseAction> = when (resultHandlerConfig.countryCode) {
        "JP" -> {
            arrayOf(
                ParseAction.YAHOO,
                ParseAction.RAKUTEN,
                ParseAction.WEB_SEARCH,
                ParseAction.COPY,
                ParseAction.SHARE,
            )
        }
        "DE" -> {
            arrayOf(
                ParseAction.WEB_SEARCH,
                ParseAction.EBAY,
                ParseAction.COPY,
                ParseAction.SHARE,
            )
        }
        "MX" -> {
            arrayOf(
                ParseAction.WEB_SEARCH,
                ParseAction.PRODUCT_SEARCH,
                ParseAction.COPY,
                ParseAction.SHARE,
            )
        }
        else -> {
            arrayOf(
                ParseAction.WEB_SEARCH,
                ParseAction.COPY,
                ParseAction.SHARE,
            )
        }
    }


    override fun handleButtonPress(action: ParseAction) {
        when (action) {
            ParseAction.SHOP_NOW -> {
                HandlerUtils.openProductSearch(this, parsedModel.text)
            }

            ParseAction.WEB_SEARCH -> {
                HandlerUtils.webSearch(this, parsedModel.text)
            }

            ParseAction.PRODUCT_SEARCH -> {
                HandlerUtils.openProductSearch(this, parsedModel.text)
            }

            ParseAction.YAHOO -> {
                HandlerUtils.yahooSearch(this, parsedModel.text)
            }

            ParseAction.RAKUTEN -> {
                HandlerUtils.rakutenSearch(this, parsedModel.text)
            }

            ParseAction.EBAY -> {
                HandlerUtils.ebaySearch(this, parsedModel.text)
            }

            else -> super.handleButtonPress(action)
        }
    }

    override fun getDisplayList() = arrayListOf(SimpleResult(SimpleResult.CONTENT_TYPE_BARCODE, getDisplayContent()))

}