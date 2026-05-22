package com.drojian.qrcode.scanresultlib.parse;

import com.drojian.qrcode.baselib.ScanResultModel;
import com.drojian.qrcode.scanlib.scan.parse.format.ParseTwitterModel;

/**
 * @author yangfengfan 2019-09-12
 * <p>
 * Twitter类型解析
 */
public class TwitterResultParser extends ResultParser {
    // 判断是否为Twitter类型的关键词
    private final String KEY_WORD_ID = "twitter://user?screen_name=";
    private final String KEY_WORD_ID_X = "x://user?screen_name=";
    private final String KEY_WORD_URL = "twitter.com";
    private final String KEY_WORD_URL_X = "x.com";

    @Override
    public ParseTwitterModel parse(ScanResultModel theResult) {
        String rawText = getMassagedText(theResult).trim();
        // 判读是不是Twitter类型
        boolean isTwitter = rawText.toLowerCase().startsWith(KEY_WORD_ID) || rawText.toLowerCase().startsWith(KEY_WORD_ID_X) ||
                rawText.toLowerCase().contains(KEY_WORD_URL) || rawText.toLowerCase().contains(KEY_WORD_URL_X);
        if (!isTwitter) {
            return null;
        }

        return new ParseTwitterModel(rawText);
    }
}
