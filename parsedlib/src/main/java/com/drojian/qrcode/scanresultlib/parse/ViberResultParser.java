package com.drojian.qrcode.scanresultlib.parse;


import com.drojian.qrcode.baselib.ScanResultModel;
import com.drojian.qrcode.scanlib.scan.parse.format.ParseViberModel;

/**
 * @author yangfengfan 2019-09-12
 * <p>
 * Viber类型解析
 */
public class ViberResultParser extends ResultParser {

    @Override
    public ParseViberModel parse(ScanResultModel theResult) {
        String rawText = getMassagedText(theResult);
        String KEY_WORD_ID = "viber://add?number=";
        String KEY_WORD_URL = "viber.com";
        boolean isViber = rawText.toLowerCase().startsWith(KEY_WORD_ID) || rawText.toLowerCase().contains(KEY_WORD_URL);
        if (!isViber) {
            return null;
        }
        String title;
        if (rawText.toLowerCase().startsWith(KEY_WORD_ID)) {
            title = rawText.substring(KEY_WORD_ID.length());
        } else {
            title = rawText;
        }
        return new ParseViberModel(rawText, title);
    }
}
