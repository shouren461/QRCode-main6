package com.drojian.qrcode.scanresultlib.parse;

import com.drojian.qrcode.baselib.ScanResultModel;
import com.drojian.qrcode.scanlib.scan.parse.format.ParseSpotifyModel;

/**
 * @author yangfengfan 2019-09-12
 * <p>
 * Spotify类型解析
 */
public class SpotifyResultParser extends ResultParser {

    @Override
    public ParseSpotifyModel parse(ScanResultModel theResult) {
        String rawText = getMassagedText(theResult);
        // 判断是不是FaceBook类型
        // 判断是否为Spotify的关键词
        String KEY_WORD = "spotify:search:";
        boolean isSpotify = rawText.toLowerCase().startsWith(KEY_WORD);
        if (!isSpotify) {
            return null;
        }
        return new ParseSpotifyModel(rawText);
    }
}
