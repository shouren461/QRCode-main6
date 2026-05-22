package com.drojian.qrcode.scanresultlib

import android.app.Activity
import com.drojian.qrcode.baselib.ScanResultModel
import com.drojian.qrcode.scanlib.scan.parse.BaseParseModel
import com.drojian.qrcode.scanlib.scan.parse.ParsedFormat
import com.drojian.qrcode.scanlib.scan.parse.format.ParseAddressBookModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseBarCodeModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseCalendarModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseEmailModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseFacebookModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseGeoModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseISBNModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseInstagramModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParsePaypalModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseSMSModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseSpotifyModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseTelModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseTextModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseTwitterModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseURIModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseVINModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseViberModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseWhatsAppModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseWifiModel
import com.drojian.qrcode.scanlib.scan.parse.format.ParseYoutubeModel
import com.drojian.qrcode.scanresultlib.handler.AddressBookHandler
import com.drojian.qrcode.scanresultlib.handler.BarCodeHandler
import com.drojian.qrcode.scanresultlib.handler.CalendarHandler
import com.drojian.qrcode.scanresultlib.handler.EmailAddressHandler
import com.drojian.qrcode.scanresultlib.handler.FacebookHandler
import com.drojian.qrcode.scanresultlib.handler.GeoHandler
import com.drojian.qrcode.scanresultlib.handler.ISBNHandler
import com.drojian.qrcode.scanresultlib.handler.InstagramHandler
import com.drojian.qrcode.scanresultlib.handler.PaypalHandler
import com.drojian.qrcode.scanresultlib.handler.ProductHandler
import com.drojian.qrcode.scanresultlib.handler.SMSHandler
import com.drojian.qrcode.scanresultlib.handler.SpotifyHandler
import com.drojian.qrcode.scanresultlib.handler.TelHandler
import com.drojian.qrcode.scanresultlib.handler.TextHandler
import com.drojian.qrcode.scanresultlib.handler.TwitterHandler
import com.drojian.qrcode.scanresultlib.handler.URIHandler
import com.drojian.qrcode.scanresultlib.handler.VINHandler
import com.drojian.qrcode.scanresultlib.handler.ViberHandler
import com.drojian.qrcode.scanresultlib.handler.WhatsAppHandler
import com.drojian.qrcode.scanresultlib.handler.WifiHandler
import com.drojian.qrcode.scanresultlib.handler.YoutubeHandler
import com.drojian.qrcode.scanresultlib.parse.ResultParser

/**
 * @author yangfengfan 2020-09-03
 */
class ResultHandlerFactory {
    companion object {

        /**
         * @countryCode 国家代码
         */
        @JvmStatic
        fun makeResultHandler(activity: Activity, scanResultModel: ScanResultModel, resultHandlerConfig: ResultHandlerConfig): BaseResultHandler {
            val result = parseResult(scanResultModel)
            return try {
                when (result.parsedFormat) {
                    ParsedFormat.ADDRESSBOOK -> AddressBookHandler(activity, result as ParseAddressBookModel, resultHandlerConfig)
                    ParsedFormat.BarCode -> BarCodeHandler(activity, result as ParseBarCodeModel, resultHandlerConfig)
                    ParsedFormat.CALENDAR -> CalendarHandler(activity, result as ParseCalendarModel, resultHandlerConfig)
                    ParsedFormat.EMAIL_ADDRESS -> EmailAddressHandler(activity, result as ParseEmailModel, resultHandlerConfig)
                    ParsedFormat.FACEBOOK -> FacebookHandler(activity, result as ParseFacebookModel, resultHandlerConfig)
                    ParsedFormat.GEO -> GeoHandler(activity, result as ParseGeoModel, resultHandlerConfig)
                    ParsedFormat.INSTAGRAM -> InstagramHandler(activity, result as ParseInstagramModel, resultHandlerConfig)
                    ParsedFormat.ISBN -> ISBNHandler(activity, result as ParseISBNModel, resultHandlerConfig)
                    ParsedFormat.PAYPAL -> PaypalHandler(activity, result as ParsePaypalModel, resultHandlerConfig)
                    ParsedFormat.SPOTIFY -> SpotifyHandler(activity, result as ParseSpotifyModel, resultHandlerConfig)
                    ParsedFormat.PRODUCT -> ProductHandler(activity, result, resultHandlerConfig) // Product有两种类型：ParseProductModel 和 ParseExpandedProductModel
                    ParsedFormat.SMS -> SMSHandler(activity, result as ParseSMSModel, resultHandlerConfig)
                    ParsedFormat.TEL -> TelHandler(activity, result as ParseTelModel, resultHandlerConfig)
                    ParsedFormat.TWITTER -> TwitterHandler(activity, result as ParseTwitterModel, resultHandlerConfig)
                    ParsedFormat.URI -> URIHandler(activity, result as ParseURIModel, resultHandlerConfig)
                    ParsedFormat.VIBER -> ViberHandler(activity, result as ParseViberModel, resultHandlerConfig)
                    ParsedFormat.WHATSAPP -> WhatsAppHandler(activity, result as ParseWhatsAppModel, resultHandlerConfig)
                    ParsedFormat.WIFI -> WifiHandler(activity, result as ParseWifiModel, resultHandlerConfig)
                    ParsedFormat.YOUTUBE -> YoutubeHandler(activity, result as ParseYoutubeModel, resultHandlerConfig)
                    ParsedFormat.VIN -> VINHandler(activity, result as ParseVINModel, resultHandlerConfig)
                    else -> TextHandler(activity, result as ParseTextModel, resultHandlerConfig)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                TextHandler(activity, ParseTextModel(result.toString()), resultHandlerConfig)
            }
        }

        private fun parseResult(scanResultModel: ScanResultModel): BaseParseModel = ResultParser.parseResult(scanResultModel)

        @JvmStatic
        fun isBarCodeFormat(scanResultModel: ScanResultModel): Boolean {
            return when (scanResultModel.codeFormat) {
                com.drojian.qrcode.baselib.CodeFormat.CODE_39,
                com.drojian.qrcode.baselib.CodeFormat.CODE_93,
                com.drojian.qrcode.baselib.CodeFormat.CODE_128,
                com.drojian.qrcode.baselib.CodeFormat.CODABAR,
                com.drojian.qrcode.baselib.CodeFormat.ITF,
                com.drojian.qrcode.baselib.CodeFormat.RSS_14,
                com.drojian.qrcode.baselib.CodeFormat.RSS_EXPANDED -> true
                else -> false
            }
        }

        @JvmStatic
        fun isBarCodeFormat(codeFormat: com.drojian.qrcode.baselib.CodeFormat?): Boolean {
            return when (codeFormat) {
                com.drojian.qrcode.baselib.CodeFormat.CODE_39,
                com.drojian.qrcode.baselib.CodeFormat.CODE_93,
                com.drojian.qrcode.baselib.CodeFormat.CODE_128,
                com.drojian.qrcode.baselib.CodeFormat.CODABAR,
                com.drojian.qrcode.baselib.CodeFormat.ITF,
                com.drojian.qrcode.baselib.CodeFormat.RSS_14,
                com.drojian.qrcode.baselib.CodeFormat.RSS_EXPANDED -> true
                else -> false
            }
        }

    }

}