package com.drojian.qrcode.scanresultlib.parse;


import com.drojian.qrcode.baselib.ScanResultModel;
import com.drojian.qrcode.scanlib.scan.parse.format.ParsePaypalModel;

/**
 * @author yangfengfan 2019-09-12
 *
 * Paypal类型解析
 */
public class PaypalResultParser extends ResultParser {

    @Override
    public ParsePaypalModel parse(ScanResultModel theResult) {
        String rawText = getMassagedText(theResult).trim();
        String KEY_WORD_URL2 = "paypal.com";
        String KEY_WORD_URL1 = "paypal.me";
        boolean isPaypal = rawText.toLowerCase().contains(KEY_WORD_URL1) || rawText.toLowerCase().contains(KEY_WORD_URL2);
        if ( !isPaypal ) {
            return null;
        }

        return new ParsePaypalModel(rawText);
    }
}
