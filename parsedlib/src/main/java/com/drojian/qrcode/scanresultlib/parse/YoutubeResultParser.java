package com.drojian.qrcode.scanresultlib.parse;

import com.drojian.qrcode.baselib.ScanResultModel;
import com.drojian.qrcode.scanlib.scan.parse.format.ParseYoutubeModel;

/**
 * @author yangfengfan 2019-09-12
 * <p>
 * Youtube类型解析
 */
public class YoutubeResultParser extends ResultParser {

    @Override
    public ParseYoutubeModel parse(ScanResultModel theResult) {
        String rawText = getMassagedText(theResult).trim();
        String KEY_WORD_URL1 = "youtube.com";
        String KWY_WORD_URL2 = "youtu.be";
        boolean isYoutube = rawText.toLowerCase().contains(KEY_WORD_URL1) || rawText.toLowerCase().contains(KWY_WORD_URL2);
        if (!isYoutube) {
            return null;
        }
        if (!URIResultParser.isBasicallyValidURI(rawText) || URIResultParser.isPossiblyMaliciousURI(rawText)) {
            return null;
        }
        return new ParseYoutubeModel(rawText);
    }
}
