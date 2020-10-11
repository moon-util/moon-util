package com.moon.core.time;

import com.moon.core.enums.EnumDescriptor;

import java.time.MonthDay;

/**
 * 十二星座
 *
 * @author moonsky
 */
public enum Constellation implements EnumDescriptor {
    /**
     * 白羊座，金牛座，双子座，巨蟹座，狮子座，处女座，天秤座，天蝎座，射手座，摩羯座，水瓶座，双鱼座
     */
    ARIES("Aries", "白羊座", "03-21", "04-19"),
    TAURUS("Taurus", "金牛座", "04-20", "05-20"),
    GEMINI("Gemini", "双子座", "05-21", "06-21"),
    CANCER("Cancer", "巨蟹座", "06-22", "07-22"),
    LEO("Leo", "狮子座", "07-23", "08-22"),
    VIRGO("Virgo", "处女座", "08-23", "09-22"),
    LIBRA("Libra", "天秤座", "09-23", "10-23"),
    SCORPIO("Scorpio", "天蝎座", "10-24", "11-22"),
    SAGITTARIUS("Sagittarius", "射手座", "11-23", "12-21"),
    CAPRICORN("Capricorn", "摩羯座", "12-22", "01-19"),
    AQUARIUS("Aquarius", "水瓶座", "01-20", "02-18"),
    PISCES("Pisces", "双鱼座", "02-19", "03-20");

    private final String text;
    private final String name;
    private final int startMonth;
    private final int startDayOfMonth;
    private final int endMonth;
    private final int endDayOfMonth;

    Constellation(String text, String name, String startDate, String endDate) {
        this.text = text;
        this.name = name;
        String[] start = startDate.split("-");
        String[] end = endDate.split("-");
        this.startMonth = Integer.parseInt(start[0]);
        this.startDayOfMonth = Integer.parseInt(start[1]);
        this.endMonth = Integer.parseInt(end[0]);
        this.endDayOfMonth = Integer.parseInt(end[1]);
    }

    public String getChineseText() { return name; }

    /**
     * 枚举信息
     *
     * @return 枚举信息
     */
    @Override
    public String getText() { return text; }

    public boolean isMatches(DateTime datetime) {
        int month = datetime.getMonthValue();
        int day = datetime.getDayOfMonth();
        MonthDay start = MonthDay.of(startMonth, startDayOfMonth);
        MonthDay end = MonthDay.of(endMonth, endDayOfMonth);
        MonthDay date = MonthDay.of(month, day);
        return start.equals(date) || end.equals(date) || (date.isAfter(start) && date.isBefore(end));
    }

    public static Constellation of(int month, int dayOfMonth) {
        switch (month) {
            case 1:
                return dayOfMonth < 20 ? CAPRICORN : AQUARIUS;
            case 2:
                return dayOfMonth < 19 ? AQUARIUS : PISCES;
            case 3:
                return dayOfMonth < 21 ? PISCES : ARIES;
            case 4:
                return dayOfMonth < 20 ? ARIES : TAURUS;
            case 5:
                return dayOfMonth < 21 ? TAURUS : GEMINI;
            case 6:
                return dayOfMonth < 22 ? GEMINI : CANCER;
            case 7:
                return dayOfMonth < 23 ? CANCER : LEO;
            case 8:
                return dayOfMonth < 23 ? LEO : VIRGO;
            case 9:
                return dayOfMonth < 23 ? VIRGO : LIBRA;
            case 10:
                return dayOfMonth < 24 ? LIBRA : SCORPIO;
            case 11:
                return dayOfMonth < 23 ? SCORPIO : SAGITTARIUS;
            case 12:
                return dayOfMonth < 22 ? SAGITTARIUS : CAPRICORN;
            default:
                throw new IllegalStateException("Invalid month value of: " + month);
        }
    }
}
