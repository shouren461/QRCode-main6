package com.drojian.qrcode.createlib.create.format;

import android.text.TextUtils;

import java.util.regex.Pattern;

class CreateUtil {

    /**
     * 去除换行符
     */
    public static String removeNewLine(String note) {
        if (note == null) {
            return "";
        }
        return note.replace("\n", "").replace("\r", "");
    }

    /**
     * @return 是否只包括空格和换行
     */
    public static boolean isOnlySpace(CharSequence values) {
        if (TextUtils.isEmpty(values)) {
            return true;
        }
        return Pattern.compile("\\s*|\t|\r|\n").matcher(values).replaceAll("").length() <= 0;
    }

    /**
     * @return 是否为Youtube链接
     */
    public static boolean isYoutubeLink(String values) {
        if (values == null) {
            return false;
        }
        return values.toLowerCase().contains("youtube.com") || values.toLowerCase().contains("youtu.be");
    }

    /**
     * @return 是否为Twitter链接
     */
    public static boolean isTwitterLink(String value) {
        if (value == null) {
            return false;
        }
        return value.toLowerCase().contains("twitter.com") || value.toLowerCase().contains("x.com");
    }

    /**
     * @return 是否为Paypal链接
     */
    public static boolean isPaypalyLink(String value) {
        if (value == null) {
            return false;
        }
        return value.toLowerCase().contains("paypal.me");
    }

    /**
     * @return 是否为Instagram链接
     */
    public static boolean isInstagramLink(String value) {
        if (value == null) {
            return false;
        }
        return value.toLowerCase().contains("instagram.com");
    }

    /**
     * @return 是否为Facebook链接
     */
    public static boolean isFacebookLink(String value) {
        if (value == null) {
            return false;
        }
        return value.toLowerCase().contains("facebook.com");
    }
}
