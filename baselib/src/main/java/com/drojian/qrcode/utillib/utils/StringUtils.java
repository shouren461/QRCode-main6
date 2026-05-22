package com.drojian.qrcode.utillib.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static boolean isOnlySpace(String values) {

        String res = "";
        if (values != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(values);
            res = m.replaceAll("");
        }
        return res.length() <= 0;
    }

    public static boolean isValidUrl(String url) {
        return !TextUtils.isEmpty(url) && url.matches(Patterns.WEB_URL.pattern());
    }

}
