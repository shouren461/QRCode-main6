package com.drojian.qrcode.scanresultlib.supplement;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Handles any locale-specific logic for the client.
 *
 * @author Sean Owen
 */
public final class LocaleManager {

    private static final String DEFAULT_TLD = "com";

    /**
     * Locales (well, countries) where Google web search is available.
     * These should be kept in sync with our translations.
     */
    private static final Map<String, String> GOOGLE_COUNTRY_TLD;

    static {
        GOOGLE_COUNTRY_TLD = new HashMap<>();
        GOOGLE_COUNTRY_TLD.put("AR", "com.ar"); // ARGENTINA
        GOOGLE_COUNTRY_TLD.put("AU", "com.au"); // AUSTRALIA
        GOOGLE_COUNTRY_TLD.put("BR", "com.br"); // BRAZIL
        GOOGLE_COUNTRY_TLD.put("BG", "bg"); // BULGARIA
        GOOGLE_COUNTRY_TLD.put(Locale.CANADA.getCountry(), "ca");
        GOOGLE_COUNTRY_TLD.put(Locale.CHINA.getCountry(), "cn");
        GOOGLE_COUNTRY_TLD.put("CZ", "cz"); // CZECH REPUBLIC
        GOOGLE_COUNTRY_TLD.put("DK", "dk"); // DENMARK
        GOOGLE_COUNTRY_TLD.put("FI", "fi"); // FINLAND
        GOOGLE_COUNTRY_TLD.put(Locale.FRANCE.getCountry(), "fr");
        GOOGLE_COUNTRY_TLD.put(Locale.GERMANY.getCountry(), "de");
        GOOGLE_COUNTRY_TLD.put("GR", "gr"); // GREECE
        GOOGLE_COUNTRY_TLD.put("HU", "hu"); // HUNGARY
        GOOGLE_COUNTRY_TLD.put("ID", "co.id"); // INDONESIA
        GOOGLE_COUNTRY_TLD.put("IL", "co.il"); // ISRAEL
        GOOGLE_COUNTRY_TLD.put(Locale.ITALY.getCountry(), "it");
        GOOGLE_COUNTRY_TLD.put(Locale.JAPAN.getCountry(), "co.jp");
        GOOGLE_COUNTRY_TLD.put(Locale.KOREA.getCountry(), "co.kr");
        GOOGLE_COUNTRY_TLD.put("NL", "nl"); // NETHERLANDS
        GOOGLE_COUNTRY_TLD.put("PL", "pl"); // POLAND
        GOOGLE_COUNTRY_TLD.put("PT", "pt"); // PORTUGAL
        GOOGLE_COUNTRY_TLD.put("RO", "ro"); // ROMANIA
        GOOGLE_COUNTRY_TLD.put("RU", "ru"); // RUSSIA
        GOOGLE_COUNTRY_TLD.put("SK", "sk"); // SLOVAK REPUBLIC
        GOOGLE_COUNTRY_TLD.put("SI", "si"); // SLOVENIA
        GOOGLE_COUNTRY_TLD.put("ES", "es"); // SPAIN
        GOOGLE_COUNTRY_TLD.put("SE", "se"); // SWEDEN
        GOOGLE_COUNTRY_TLD.put("CH", "ch"); // SWITZERLAND
        GOOGLE_COUNTRY_TLD.put(Locale.TAIWAN.getCountry(), "tw");
        GOOGLE_COUNTRY_TLD.put("TR", "com.tr"); // TURKEY
        GOOGLE_COUNTRY_TLD.put("UA", "com.ua"); // UKRAINE
        GOOGLE_COUNTRY_TLD.put(Locale.UK.getCountry(), "co.uk");
        GOOGLE_COUNTRY_TLD.put(Locale.US.getCountry(), "com");
    }

    /**
     * Google Product Search for mobile is available in fewer countries than web search. See here:
     * http://support.google.com/merchants/bin/answer.py?hl=en-GB&answer=160619
     */
    private static final Map<String, String> GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD;

    static {
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD = new HashMap<>();
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put("AU", "com.au"); // AUSTRALIA
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.FRANCE.getCountry(), "fr");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.GERMANY.getCountry(), "de");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.ITALY.getCountry(), "it");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.JAPAN.getCountry(), "co.jp");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put("NL", "nl"); // NETHERLANDS
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put("ES", "es"); // SPAIN
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put("CH", "ch"); // SWITZERLAND
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.UK.getCountry(), "co.uk");
        GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD.put(Locale.US.getCountry(), "com");
    }

    /**
     * Book search is offered everywhere that web search is available.
     */
    private static final Map<String, String> GOOGLE_BOOK_SEARCH_COUNTRY_TLD = GOOGLE_COUNTRY_TLD;


    private LocaleManager() {
    }

    public static String getProductSearchCountryTLD() {
        return doGetTLD(GOOGLE_PRODUCT_SEARCH_COUNTRY_TLD);
    }

    public static String getBookSearchCountryTLD() {
        return doGetTLD(GOOGLE_BOOK_SEARCH_COUNTRY_TLD);
    }

    private static String getSystemCountry() {
        Locale locale = Locale.getDefault();
        return locale.getCountry();
    }

    private static String doGetTLD(Map<String, String> map) {
        String tld = map.get(getSystemCountry());
        return tld == null ? DEFAULT_TLD : tld;
    }

}
