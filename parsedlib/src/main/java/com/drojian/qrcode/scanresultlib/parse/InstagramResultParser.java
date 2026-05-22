package com.drojian.qrcode.scanresultlib.parse;

import com.drojian.qrcode.baselib.ScanResultModel;
import com.drojian.qrcode.scanlib.scan.parse.format.ParseInstagramModel;

/**
 * @author yangfengfan 2019-09-12
 * <p>
 * Instagram类型解析
 */
public class InstagramResultParser extends ResultParser {

    @Override
    public ParseInstagramModel parse(ScanResultModel theResult) {
        String rawText = getMassagedText(theResult).trim();
        String KEY_WORD_ID = "instagram://user?username=";
        String KEY_WORD_URL = "instagram.com";
        boolean isInstagram = rawText.toLowerCase().startsWith(KEY_WORD_ID) || rawText.toLowerCase().contains(KEY_WORD_URL);
        if (!isInstagram) {
            return null;
        }
        return new ParseInstagramModel(rawText);
    }
}
