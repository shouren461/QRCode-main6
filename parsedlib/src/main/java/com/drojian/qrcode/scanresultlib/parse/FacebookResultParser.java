package com.drojian.qrcode.scanresultlib.parse;

import com.drojian.qrcode.baselib.ScanResultModel;
import com.drojian.qrcode.scanlib.scan.parse.format.ParseFacebookModel;

/**
 * @author yangfengfan 2019-09-11
 * <p>
 * Facebook 类型解析
 */
public class FacebookResultParser extends ResultParser {

    @Override
    public ParseFacebookModel parse(ScanResultModel theResult) {
        String rawText = getMassagedText(theResult).trim();
        String KEY_WORD_ID = "fb://profile/";
        String KEY_WORD_URL = "facebook.com";
        boolean isFaceBook = rawText.toLowerCase().startsWith(KEY_WORD_ID) || rawText.toLowerCase().contains(KEY_WORD_URL);
        if (!isFaceBook) {
            return null;
        }
        String title;
        if (rawText.toLowerCase().startsWith(KEY_WORD_ID)) {
            title = rawText.substring(KEY_WORD_ID.length());
        } else {
            title = rawText;
        }

        return new ParseFacebookModel(rawText);
    }
}
