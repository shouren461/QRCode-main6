package com.drojian.qrcode.scanresultlib.parse;

import com.drojian.qrcode.baselib.ScanResultModel;
import com.drojian.qrcode.scanlib.scan.parse.format.ParseWhatsAppModel;

/**
 * @author yangfengfan 2019-09-12
 * <p>
 * WhatsApp类型解析
 */
public class WhatsAppResultParser extends ResultParser {

    @Override
    public ParseWhatsAppModel parse(ScanResultModel theResult) {
        String rawText = getMassagedText(theResult);
        // 判断是不是WhatsApp类型
        String KEY_WORD_URL = "whatsapp.com";
        // 判断是否为WhatsApp类型的关键词
        String KEY_WORD_ID = "whatsapp://send?phone=";
        boolean isWhatsApp = rawText.toLowerCase().startsWith(KEY_WORD_ID) || rawText.toLowerCase().contains(KEY_WORD_URL);
        if (!isWhatsApp) {
            return null;
        }
        String title;
        if (rawText.toLowerCase().startsWith(KEY_WORD_ID)) {
            title = rawText.substring(KEY_WORD_ID.length());
        } else {
            title = rawText;
        }

        return new ParseWhatsAppModel(rawText, title);
    }
}
