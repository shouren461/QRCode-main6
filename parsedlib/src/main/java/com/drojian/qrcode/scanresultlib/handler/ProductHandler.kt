package com.drojian.qrcode.scanresultlib.handler

import android.app.Activity
import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseExpandedProductModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseProductModel
import com.drojian.qrcode.scanresultlib.BaseResultHandler
import com.drojian.qrcode.scanresultlib.ResultHandlerConfig
import com.drojian.qrcode.scanresultlib.button.ParseAction
import com.drojian.qrcode.scanresultlib.util.HandlerUtils
import com.drojian.qrcode.scanresultlib.util.HandlerUtils.amazonSearch
import com.drojian.qrcode.scanresultlib.util.HandlerUtils.ebaySearch
import com.drojian.qrcode.scanresultlib.util.HandlerUtils.rakutenSearch
import com.drojian.qrcode.scanresultlib.util.HandlerUtils.webSearch
import com.drojian.qrcode.scanresultlib.util.HandlerUtils.yahooSearch

/**
 * @author yangfengfan 2020-10-19
 */
class ProductHandler(activity: Activity, val parsedModel: BaseParseModel, resultHandlerConfig: ResultHandlerConfig) :
    BaseResultHandler(activity, parsedModel, resultHandlerConfig) {

    override val parseActionList: Array<ParseAction> = when (resultHandlerConfig.countryCode) {
        "US" -> {
            arrayOf(
                ParseAction.WEB_SEARCH,
                ParseAction.AMAZON,
                ParseAction.EBAY,
                ParseAction.COPY,
                ParseAction.SHARE,
            )
        }
        "HK" -> {
            arrayOf(
                ParseAction.WEB_SEARCH,
                ParseAction.AMAZON,
                ParseAction.COPY,
                ParseAction.SHARE,
            )
        }
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
                ParseAction.AMAZON,
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
            ParseAction.PRODUCT_SEARCH, ParseAction.SHOP_NOW -> {
                HandlerUtils.openProductSearch(this, getProductIDFromResult(parsedModel))
            }
            ParseAction.WEB_SEARCH -> {
                webSearch(this, getProductIDFromResult(parsedModel))
            }
            ParseAction.AMAZON -> {
                amazonSearch(this, getProductIDFromResult(parsedModel))
            }
            ParseAction.EBAY -> {
                ebaySearch(this, getProductIDFromResult(parsedModel))
            }
            ParseAction.YAHOO -> {
                yahooSearch(this, getProductIDFromResult(parsedModel))
            }
            ParseAction.RAKUTEN -> {
                rakutenSearch(this, getProductIDFromResult(parsedModel))
            }
            else -> super.handleButtonPress(action)
        }
    }

    override fun getDisplayList() = arrayListOf(SimpleResult(SimpleResult.CONTENT_TYPE_BARCODE, getDisplayContent()))

    private fun getProductIDFromResult(parseModel: BaseParseModel): String? {
        if (parseModel is ParseProductModel) {
            return parseModel.normalizedProductID
        }
        if (parseModel is ParseExpandedProductModel) {
            return parseModel.rawText
        }
        throw IllegalArgumentException(parseModel.javaClass.toString())
    }

}