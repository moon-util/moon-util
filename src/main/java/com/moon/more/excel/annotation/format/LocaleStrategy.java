package com.moon.more.excel.annotation.format;

import java.util.Locale;

import static java.util.Locale.Category.FORMAT;

/**
 * @author benshaoye
 * @see java.util.Locale
 */
public enum LocaleStrategy {
    /**
     * 默认
     */
    DEFAULT(Locale.getDefault(FORMAT)),


    ENGLISH(Locale.ENGLISH),


    FRENCH(Locale.FRENCH),


    GERMAN(Locale.GERMAN),


    ITALIAN(Locale.ITALIAN),


    JAPANESE(Locale.JAPANESE),


    KOREAN(Locale.KOREAN),


    CHINESE(Locale.CHINESE),


    SIMPLIFIED_CHINESE(Locale.SIMPLIFIED_CHINESE),


    TRADITIONAL_CHINESE(Locale.TRADITIONAL_CHINESE),


    FRANCE(Locale.FRANCE),


    GERMANY(Locale.GERMANY),


    ITALY(Locale.ITALY),


    JAPAN(Locale.JAPAN),


    KOREA(Locale.KOREA),


    CHINA(Locale.CHINA),


    PRC(Locale.PRC),


    TAIWAN(Locale.TAIWAN),


    UK(Locale.UK),


    US(Locale.US),


    CANADA(Locale.CANADA),


    CANADA_FRENCH(Locale.CANADA_FRENCH),
    ;

    private final Locale locale;

    LocaleStrategy(Locale locale) { this.locale = locale; }

    public Locale getLocale() { return locale; }
}
