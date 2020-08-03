package com.moon.core.time;

import com.moon.core.enums.Const;
import com.moon.core.enums.EnumDescriptor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import static com.moon.core.util.FilterUtil.requireFind;

/**
 * @author moonsky
 */
public enum TimeZones implements EnumDescriptor {
    /**
     * Asia/Shanghai
     */
    ASIA_SHANGHAI("亚洲/上海"),
    /**
     * Africa/Cairo
     */
    AFRICA_CAIRO("非洲/开罗"),
    /**
     * America/St_Johns
     */
    AMERICA_ST$JOHNS("美洲/圣约翰斯"),
    /**
     * America/Puerto_Rico
     */
    AMERICA_PUERTO$RICO("美洲/波多黎各"),
    /**
     * America/Phoenix
     */
    AMERICA_PHOENIX("美洲/凤凰城"),
    /**
     * Asia/Karachi
     */
    ASIA_KARACHI("亚洲/卡拉奇"),
    /**
     * America/Anchorage
     */
    AMERICA_ANCHORAGE("美洲/安克雷奇"),
    /**
     * Asia/Dhaka
     */
    ASIA_DHAKA("亚洲/达卡"),
    /**
     * America/Chicago
     */
    AMERICA_CHICAGO("美洲/芝加哥"),
    /**
     * Asia/Tokyo
     */
    ASIA_TOKYO("亚洲/东京"),
    /**
     * Asia/Kolkata
     */
    ASIA_KOLKATA("亚洲/加尔各塔"),
    /**
     * America/Argentina/Buenos_Aires
     */
    AMERICA_ARGENTINA_BUENOS$AIRES("美洲/阿根廷/布宜诺斯艾利斯"),
    /**
     * Pacific/Auckland
     */
    PACIFIC_AUCKLAND("太平洋/奥克兰"),
    /**
     * Australia/Sydney
     */
    AUSTRALIA_SYDNEY("澳大利亚/悉尼"),
    /**
     * America/Sao_Paulo
     */
    AMERICA_SAO$PAULO("美洲/圣保罗"),
    /**
     * America/Los_Angeles
     */
    AMERICA_LOS$ANGELES("美洲/洛杉矶"),
    /**
     * Australia/Darwin
     */
    AUSTRALIA_DARWIN("澳大利亚/达尔文"),
    /**
     * Pacific/Guadalcanal
     */
    PACIFIC_GUADALCANAL("太平洋/瓜达尔卡纳尔岛"),
    /**
     * Asia/Ho_Chi_Minh
     */
    ASIA_HO$CHI$MINH("亚洲/胡志明"),
    /**
     * Africa/Harare
     */
    AFRICA_HARARE("非洲/哈拉雷"),
    /**
     * Europe/Paris
     */
    EUROPE_PARIS("欧洲/巴黎"),
    /**
     * Africa/Addis_Ababa
     */
    AFRICA_ADDIS$ABABA("非洲/亚的斯亚贝巴"),
    /**
     * America/Indiana/Indianapolis
     */
    AMERICA_INDIANA_INDIANAPOLIS("美洲/印第安纳/印第安纳波利斯"),
    /**
     * Pacific/Apia
     */
    PACIFIC_APIA("太平洋/阿皮亚"),
    /**
     * Asia/Yerevan
     */
    ASIA_YEREVAN("亚洲/埃里温"),
    /**
     * default
     */
    DEFAULT(null) {
        @Override
        public final String getChineseText() { return This.DFT.getChineseText(); }

        @Override
        public final String getEnglishText() { return This.DFT.getEnglishText(); }
    },
    ;

    private final String CHINESE_TEXT;

    private final static class This {

        static final boolean inChina = This.isLocal(ASIA_SHANGHAI);
        final static TimeZones DFT = requireFind(TimeZones.values(), This::isLocal);

        static boolean isLocal(TimeZones zone) {
            Date date = new Date();
            String zoned = zone.zonedFormat(Const.PATTERN).format(date);
            return get(Const.PATTERN).format(date).equals(zoned);
        }

        static DateFormat get(String pattern) { return new SimpleDateFormat(pattern); }
    }

    TimeZones(String text) { this.CHINESE_TEXT = text; }

    final DateFormat zonedFormat(String pattern){
        DateFormat format = This.get(pattern);
        if (this != DEFAULT) {
            format.setTimeZone(getTimeZone());
        }
        return format;
    }

    /**
     * 获取时区
     *
     * @return 时区
     */
    public TimeZone getTimeZone() { return TimeZone.getTimeZone(getEnglishText()); }

    /**
     * 获取时区 ID
     *
     * @return ZoneId
     */
    public ZoneId getZoneId() { return getTimeZone().toZoneId(); }

    /**
     * 获取文本描述信息
     *
     * @return 文本描述信息
     */
    @Override
    public final String getText() { return This.inChina ? getChineseText() : getEnglishText(); }

    /**
     * 以中文方式获取文本描述
     *
     * @return 中文方式文本描述
     */
    public String getChineseText() { return CHINESE_TEXT; }

    /**
     * 以英文方式获取文本描述
     *
     * @return 英文方式文本描述
     */
    public String getEnglishText() {
        char[] chars = name().toCharArray();
        boolean isBegin = true;
        char ch;
        for (int i = 0, len = chars.length; i < len; i++) {
            ch = chars[i];
            if (i == 0 || isBegin) {
                chars[i] = Character.toUpperCase(ch);
                isBegin = false;
            } else if (ch == '_') {
                isBegin = true;
                chars[i] = '/';
            } else if (ch == '$') {
                isBegin = true;
                chars[i] = '_';
            } else {
                chars[i] = Character.toLowerCase(ch);
            }
        }
        return new String(chars);
    }
}
