package com.drojian.qrcode.scanresultlib.handler

import android.app.Activity
import com.drojian.qrcode.scanlib.scan.parse.format.ParseISBNModel
import com.drojian.qrcode.scanresultlib.BaseResultHandler
import com.drojian.qrcode.scanresultlib.ResultHandlerConfig
import com.drojian.qrcode.scanresultlib.button.ParseAction
import com.drojian.qrcode.scanresultlib.util.HandlerUtils

/**
 * @author yangfengfan 2020-10-19
 */
class ISBNHandler(activity: Activity, val parsedModel: ParseISBNModel, resultHandlerConfig: ResultHandlerConfig) :
    BaseResultHandler(activity, parsedModel, resultHandlerConfig) {

    override val parseActionList: Array<ParseAction> = when (resultHandlerConfig.countryCode) {
        "JP" -> {
            arrayOf(
                ParseAction.YAHOO,
                ParseAction.RAKUTEN,
                ParseAction.BOOK_SEARCH,
                ParseAction.COPY,
                ParseAction.SHARE,
            )
        }
        "IN" -> {
            arrayOf(
                ParseAction.WEB_SEARCH,
                ParseAction.AMAZON,
                ParseAction.PRODUCT_SEARCH,
                ParseAction.COPY,
                ParseAction.SHARE,
            )
        }
        "DE" -> {
            arrayOf(
                ParseAction.WEB_SEARCH,
                ParseAction.AMAZON,
                ParseAction.BOOK_SEARCH,
                ParseAction.COPY,
                ParseAction.SHARE,
            )
        }
        "IQ" -> {
            arrayOf(
                ParseAction.WEB_SEARCH,
                ParseAction.BOOK_SEARCH,
                ParseAction.AMAZON,
                ParseAction.COPY,
                ParseAction.SHARE,
            )
        }
        else -> {
            arrayOf(
                ParseAction.BOOK_SEARCH,
                ParseAction.WEB_SEARCH,
                ParseAction.PRODUCT_SEARCH,
                ParseAction.COPY,
                ParseAction.SHARE,
            )
        }
    }


    override fun handleButtonPress(action: ParseAction) {
        when (action) {
            ParseAction.PRODUCT_SEARCH -> {
                HandlerUtils.openProductSearch(this, parsedModel.isbn)
            }
            ParseAction.BOOK_SEARCH -> {
                HandlerUtils.openBookSearch(this, parsedModel.isbn)
            }
            ParseAction.WEB_SEARCH -> {
                HandlerUtils.webSearch(this, parsedModel.isbn)
            }
            ParseAction.YAHOO -> {
                HandlerUtils.yahooSearch(this, parsedModel.isbn)
            }
            ParseAction.RAKUTEN -> {
                HandlerUtils.rakutenSearch(this, parsedModel.isbn)
            }
            ParseAction.AMAZON -> {
                HandlerUtils.amazonSearch(this, parsedModel.isbn)
            }
            else -> super.handleButtonPress(action)
        }
    }

    override fun getDisplayList() = arrayListOf(SimpleResult(SimpleResult.CONTENT_TYPE_NUM, parsedModel.isbn))
}