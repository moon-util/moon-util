package com.moon.core.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.*;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public final class DateUtil extends CalendarUtil {

    public final static String yyyy_MM_dd_HH_mm_ss_SSS = "yyyy-MM-dd HH:mm:ss SSS";
    public final static String yyyy_MM_dd_hh_mm_ss_SSS = "yyyy-MM-dd hh:mm:ss SSS";
    public final static String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public final static String yyyy_MM_dd_hh_mm_ss = "yyyy-MM-dd hh:mm:ss";
    public final static String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
    public final static String yyyy_MM_dd_hh_mm = "yyyy-MM-dd hh:mm";
    public final static String yyyy_MM_dd_HH = "yyyy-MM-dd HH";
    public final static String yyyy_MM_dd_hh = "yyyy-MM-dd hh";
    public final static String yyyy_MM_dd = "yyyy-MM-dd";
    public final static String yyyy_MM = "yyyy-MM";
    public final static String yyyy = "yyyy";

    private DateUtil() { super(); }

    /*
     * -------------------------------------------------------------------------
     * now
     * -------------------------------------------------------------------------
     */

    public final static Date nowDate() { return new Date(); }

    public final static java.sql.Date nowSqlDate() { return new java.sql.Date(now()); }

    public final static Time nowSqlTime() { return new java.sql.Time(now()); }

    public final static Timestamp nowTimestamp() { return new Timestamp(now()); }

    /*
     * -------------------------------------------------------------------------
     * clears
     * -------------------------------------------------------------------------
     */

    public final static Date clearTime(Date value) {
        Calendar current = toCalendar(value);
        current.set(HOUR_OF_DAY, 0);
        current.set(MINUTE, 0);
        current.set(SECOND, 0);
        current.set(MILLISECOND, 0);
        return current.getTime();
    }

    public final static Date clearMilliseconds(Date value) { return setMillisecond(toCalendar(value), 0).getTime(); }

    public final static Date setYear(Date value, int amount) { return set(toCalendar(value), YEAR, amount).getTime(); }

    public final static Date setMonth(Date value, int amount) {
        return set(toCalendar(value), MONTH, amount).getTime();
    }

    public final static Date setDayOfMonth(Date value, int amount) {
        return set(toCalendar(value), DAY_OF_MONTH, amount).getTime();
    }

    public final static Date setHour(Date value, int amount) {
        return set(toCalendar(value), HOUR_OF_DAY, amount).getTime();
    }

    public final static Date setMinute(Date value, int amount) {
        return set(toCalendar(value), MINUTE, amount).getTime();
    }

    public final static Date setSecond(Date value, int amount) {
        return set(toCalendar(value), SECOND, amount).getTime();
    }

    public final static Date setMillisecond(Date value, int amount) {
        return set(toCalendar(toCalendar(value)), MILLISECOND, amount).getTime();
    }

    public final static int getYear(Date value) { return get(toCalendar(value), YEAR); }

    public final static int getMonth(Date value) { return get(toCalendar(value), MONTH); }

    public final static int getDayOfMonth(Date value) { return get(toCalendar(value), DAY_OF_MONTH); }

    public final static int getHour(Date value) { return get(toCalendar(value), HOUR_OF_DAY); }

    public final static int getMinute(Date value) { return get(toCalendar(value), MINUTE); }

    public final static int getSecond(Date value) { return get(toCalendar(value), SECOND); }

    public final static int getMillisecond(Date value) { return get(toCalendar(value), MILLISECOND); }

    /*
     * -------------------------------------------------------------------------
     * copies
     * -------------------------------------------------------------------------
     */

    public final static Date copy(Date value) { return toDate(toCalendar(value)); }

    /*
     * -------------------------------------------------------------------------
     * parsers
     * -------------------------------------------------------------------------
     */


    public static Date parse(String dateString, String patten) { return parse(dateString, new SimpleDateFormat(patten)); }

    public static Date parse(String dateString, DateFormat patten) {
        try {
            return patten.parse(dateString);
        } catch (ParseException | NullPointerException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /*
     * -------------------------------------------------------------------------
     * converters
     * -------------------------------------------------------------------------
     */

    public static Time toSqlTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Time) {
            return (Time) value;
        }
        try {
            return new Time(toCalendar(value).getTimeInMillis());
        } catch (Throwable t) {
            throw new IllegalArgumentException("can not converter to java.sql.Time of value: " + value);
        }
    }

    public static Timestamp toTimestamp(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Timestamp) {
            return (Timestamp) value;
        }
        try {
            return new Timestamp(toCalendar(value).getTimeInMillis());
        } catch (Throwable t) {
            throw new IllegalArgumentException("can not converter to java.sql.Timestamp of value: " + value);
        }
    }

    public static java.sql.Date toSqlDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof java.sql.Date) {
            return (java.sql.Date) value;
        }
        try {
            return new java.sql.Date(toCalendar(value).getTimeInMillis());
        } catch (Throwable t) {
            throw new IllegalArgumentException("can not converter to java.sql.Date of value: " + value);
        }
    }

    /*
     * to java.util.Date ============================================
     */

    public static Date toDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return (Date) value;
        }
        try {
            return toCalendar(value).getTime();
        } catch (Throwable t) {
            throw new IllegalArgumentException("can not converter to java.util.Date of value: " + value);
        }
    }

    /**
     * 解析成 Date 日期
     *
     * @param arguments 用 int 表示的年、月、日、时、分、秒、毫秒等一个或多个字段按顺序传入
     */
    public static Date toDate(int... arguments) { return toDate(toCalendar(arguments)); }

    /**
     * java.util.Calendar to java.util.Date
     *
     * @param value
     * @return
     */
    public final static Date toDate(Calendar value) { return value == null ? null : value.getTime(); }

    /**
     * 解析成 Date 日期
     *
     * @param dateString 要求符合格式 "yyyy-MM-dd HH:mm:ss SSS" 的一个或多个字段（超出部分将忽略）
     */
    public static Date toDate(String dateString) { return toDate(parseToCalendar(dateString)); }

    /**
     * 解析成 Date 日期
     *
     * @param arguments 用 String 表示的年、月、日、时、分、秒、毫秒等一个或多个字段按顺序传入
     */
    public static Date toDate(String... arguments) { return toDate(toCalendar(arguments)); }
}
