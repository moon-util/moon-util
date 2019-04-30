package com.moon.core.util;

import com.moon.core.enums.Const;
import com.moon.core.lang.LongUtil;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static java.util.Calendar.*;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public final class DateUtil {

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

    private final static int[] PARSE_FIELD_OF_CALENDAR = {YEAR, MONTH, DAY_OF_MONTH,
        HOUR_OF_DAY, MINUTE, SECOND, MILLISECOND};

    private DateUtil() {
        noInstanceError();
    }

    /*
     * -------------------------------------------------------------------------
     * now
     * -------------------------------------------------------------------------
     */

    public final static long now() {
        return System.currentTimeMillis();
    }

    public final static Date nowDate() {
        return new Date();
    }

    public final static java.sql.Date nowSqlDate() {
        return new java.sql.Date(now());
    }

    public final static Time nowSqlTime() {
        return new java.sql.Time(now());
    }

    public final static Timestamp nowTimestamp() {
        return new Timestamp(now());
    }

    public final static Calendar nowCalendar() {
        return Calendar.getInstance();
    }

    /*
     * -------------------------------------------------------------------------
     * clears
     * -------------------------------------------------------------------------
     */

    public final static Calendar clearTime(Calendar value) {
        Calendar current = copy(value);
        current.set(HOUR_OF_DAY, 0);
        current.set(MINUTE, 0);
        current.set(SECOND, 0);
        current.set(MILLISECOND, 0);
        return current;
    }

    public final static Date clearTime(Date value) {
        Calendar current = toCalendar(value);
        current.set(HOUR_OF_DAY, 0);
        current.set(MINUTE, 0);
        current.set(SECOND, 0);
        current.set(MILLISECOND, 0);
        return current.getTime();
    }

    public final static Calendar clearMilliseconds(Calendar calendar) {
        return setMillisecond(calendar, 0);
    }

    public final static Date clearMilliseconds(Date value) {
        return setMillisecond(toCalendar(value), 0).getTime();
    }

    /*
     * -------------------------------------------------------------------------
     * next and prev
     * -------------------------------------------------------------------------
     */

    public final static Calendar nextYear(Calendar value) {
        return plusYears(value, 1);
    }

    public final static Calendar nextMonth(Calendar value) {
        return plusMonths(value, 1);
    }

    public final static Calendar nextDay(Calendar value) {
        return plusDays(value, 1);
    }

    public final static Calendar nextHour(Calendar value) {
        return plusHours(value, 1);
    }

    public final static Calendar nextMinute(Calendar value) {
        return plusMinutes(value, 1);
    }

    public final static Calendar nextSecond(Calendar value) {
        return plusSeconds(value, 1);
    }

    public final static Calendar nextMillisecond(Calendar value) {
        return plusMilliSeconds(value, 1);
    }

    public final static Calendar prevYear(Calendar value) {
        return minusYears(value, 1);
    }

    public final static Calendar prevMonth(Calendar value) {
        return minusMonths(value, 1);
    }

    public final static Calendar prevDay(Calendar value) {
        return minusDays(value, 1);
    }

    public final static Calendar prevHour(Calendar value) {
        return minusHours(value, 1);
    }

    public final static Calendar prevMinute(Calendar value) {
        return minusMinutes(value, 1);
    }

    public final static Calendar prevSecond(Calendar value) {
        return minusSeconds(value, 1);
    }

    public final static Calendar prevMillisecond(Calendar value) {
        return minusMilliSeconds(value, 1);
    }

    /*
     * -------------------------------------------------------------------------
     * plus and minus
     * -------------------------------------------------------------------------
     */

    public final static Calendar plusYears(Calendar value, int amount) {
        return setYear(value, getYear(value) + amount);
    }

    public final static Calendar plusMonths(Calendar value, int amount) {
        return setMonth(value, getMonth(value) + amount);
    }

    public final static Calendar plusDays(Calendar value, int amount) {
        return setDayOfMonth(value, getDayOfMonth(value) + amount);
    }

    public final static Calendar plusHours(Calendar value, int amount) {
        return setHour(value, getHour(value) + amount);
    }

    public final static Calendar plusMinutes(Calendar value, int amount) {
        return setMinute(value, getMinute(value) + amount);
    }

    public final static Calendar plusSeconds(Calendar value, int amount) {
        return setSecond(value, getSecond(value) + amount);
    }

    public final static Calendar plusMilliSeconds(Calendar value, int amount) {
        return setMillisecond(value, getMillisecond(value) + amount);
    }

    public final static Calendar minusYears(Calendar value, int amount) {
        return setYear(value, getYear(value) - amount);
    }

    public final static Calendar minusMonths(Calendar value, int amount) {
        return setMonth(value, getMonth(value) - amount);
    }

    public final static Calendar minusDays(Calendar value, int amount) {
        return setDayOfMonth(value, getDayOfMonth(value) - amount);
    }

    public final static Calendar minusHours(Calendar value, int amount) {
        return setHour(value, getHour(value) - amount);
    }

    public final static Calendar minusMinutes(Calendar value, int amount) {
        return setMonth(value, getMinute(value) - amount);
    }

    public final static Calendar minusSeconds(Calendar value, int amount) {
        return setSecond(value, getSecond(value) - amount);
    }

    public final static Calendar minusMilliSeconds(Calendar value, int amount) {
        return setMillisecond(value, getMillisecond(value) - amount);
    }

    /*
     * -------------------------------------------------------------------------
     * getters and setters
     * -------------------------------------------------------------------------
     */

    public final static Calendar setYear(Calendar value, int amount) {
        return set(value, YEAR, amount);
    }

    public final static Calendar setMonth(Calendar value, int amount) {
        return set(value, MONTH, amount);
    }

    public final static Calendar setDayOfMonth(Calendar value, int amount) {
        return set(value, DAY_OF_MONTH, amount);
    }

    public final static Calendar setHour(Calendar value, int amount) {
        return set(value, HOUR_OF_DAY, amount);
    }

    public final static Calendar setMinute(Calendar value, int amount) {
        return set(value, MINUTE, amount);
    }

    public final static Calendar setSecond(Calendar value, int amount) {
        return set(value, SECOND, amount);
    }

    public final static Calendar setMillisecond(Calendar value, int amount) {
        return set(value, MILLISECOND, amount);
    }

    public final static Date setYear(Date value, int amount) {
        return set(toCalendar(value), YEAR, amount).getTime();
    }

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

    public final static Calendar set(Calendar value, int field, int amount) {
        Calendar current = copy(value);
        current.set(field, field == MONTH ? amount - 1 : amount);
        return current;
    }

    public final static int getYear(Calendar value) {
        return get(value, YEAR);
    }

    public final static int getMonth(Calendar value) {
        return get(value, MONTH);
    }

    public final static int getDayOfMonth(Calendar value) {
        return get(value, DAY_OF_MONTH);
    }

    public final static int getHour(Calendar value) {
        return get(value, HOUR_OF_DAY);
    }

    public final static int getMinute(Calendar value) {
        return get(value, MINUTE);
    }

    public final static int getSecond(Calendar value) {
        return get(value, SECOND);
    }

    public final static int getMillisecond(Calendar value) {
        return get(value, MILLISECOND);
    }

    public final static int getYear(Date value) {
        return get(toCalendar(value), YEAR);
    }

    public final static int getMonth(Date value) {
        return get(toCalendar(value), MONTH);
    }

    public final static int getDayOfMonth(Date value) {
        return get(toCalendar(value), DAY_OF_MONTH);
    }

    public final static int getHour(Date value) {
        return get(toCalendar(value), HOUR_OF_DAY);
    }

    public final static int getMinute(Date value) {
        return get(toCalendar(value), MINUTE);
    }

    public final static int getSecond(Date value) {
        return get(toCalendar(value), SECOND);
    }

    public final static int getMillisecond(Date value) {
        return get(toCalendar(value), MILLISECOND);
    }

    public final static int get(Calendar cal, int field) {
        return field == MONTH ? cal.get(field) + 1 : cal.get(field);
    }

    /*
     * -------------------------------------------------------------------------
     * copies
     * -------------------------------------------------------------------------
     */

    public final static Calendar copy(Calendar value) {
        return toCalendar(value.getTimeInMillis());
    }

    public final static Date copy(Date value) {
        return toDate(toCalendar(value));
    }

    /*
     * -------------------------------------------------------------------------
     * formatter
     * -------------------------------------------------------------------------
     */

    public final static String format() {
        return format(yyyy_MM_dd_HH_mm_ss);
    }

    public final static String format(String pattern) {
        return format(new Date(), pattern);
    }

    public final static String format(Date date, String pattern) {
        return format(date, new SimpleDateFormat(pattern));
    }

    public final static String format(Date date, DateFormat formatter) {
        return formatter.format(date);
    }

    public final static String format(Date date) {
        return format(date, yyyy_MM_dd_HH_mm_ss);
    }

    public final static String format(Calendar date) {
        return format(date.getTime());
    }

    public final static String format(Calendar date, String pattern) {
        return format(date.getTime(), pattern);
    }

    public final static String format(Calendar date, DateFormat formatter) {
        return format(date.getTime(), formatter);
    }

    /*
     * -------------------------------------------------------------------------
     * parsers
     * -------------------------------------------------------------------------
     */

    /**
     * 解析成 Calendar 日期
     *
     * @param dateString 要求符合格式 "yyyy-MM-dd HH:mm:ss SSS" 的一个或多个字段（超出部分将忽略）
     */
    public static Calendar parseToCalendar(String dateString) {
        dateString = dateString == null ? Const.EMPTY : dateString.trim();
        int strLen = dateString.length();
        int idx = 0;
        if (strLen > idx) {

            List<String> fieldsValue = new ArrayList();
            StringBuilder sb = new StringBuilder();
            char ch;
            boolean moreBlank = false;

            do {
                ch = dateString.charAt(idx++);
                if (ch > 47 && ch < 58) {
                    sb.append(ch);
                    moreBlank = false;
                } else if (!moreBlank) {
                    fieldsValue.add(sb.toString());
                    sb.setLength(0);
                    moreBlank = true;
                }
            } while (strLen > idx);

            if (sb.length() > 0) {
                fieldsValue.add(sb.toString());
            }

            int size = fieldsValue.size();
            if (size == 0) {
                throw new IllegalArgumentException("Must input date string.");
            }
            int length = PARSE_FIELD_OF_CALENDAR.length;

            length = size > length ? length : size;
            Calendar calendar = Calendar.getInstance();
            calendar.clear();

            for (int i = 0; i < length; i++) {
                int currField = PARSE_FIELD_OF_CALENDAR[i];
                if (currField == MONTH) {
                    calendar.set(currField, Integer.parseInt(fieldsValue.get(i)) - 1);
                } else {
                    calendar.set(currField, Integer.parseInt(fieldsValue.get(i)));
                }
            }

            return calendar;
        }

        return null;
    }

    public static Date parse(String dateString, String patten) {
        return parse(dateString, new SimpleDateFormat(patten));
    }

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
    public static Date toDate(int... arguments) {
        return toDate(toCalendar(arguments));
    }

    /**
     * java.util.Calendar to java.util.Date
     *
     * @param value
     * @return
     */
    public final static Date toDate(Calendar value) {
        return value == null ? null : value.getTime();
    }

    /**
     * 解析成 Date 日期
     *
     * @param dateString 要求符合格式 "yyyy-MM-dd HH:mm:ss SSS" 的一个或多个字段（超出部分将忽略）
     */
    public static Date toDate(String dateString) {
        return toDate(parseToCalendar(dateString));
    }

    /**
     * 解析成 Date 日期
     *
     * @param arguments 用 String 表示的年、月、日、时、分、秒、毫秒等一个或多个字段按顺序传入
     */
    public static Date toDate(String... arguments) {
        return toDate(toCalendar(arguments));
    }

    /*
     * toCalendar ============================================
     */

    /**
     * 解析成 Calendar 日期
     *
     * @param arguments 用 String 表示的年、月、日、时、分、秒、毫秒等一个或多个字段按顺序传入
     */
    public static Calendar toCalendar(String... arguments) {
        int size = arguments.length;
        int length = PARSE_FIELD_OF_CALENDAR.length;
        size = size > length ? length : size;
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        int i = 0;
        for (; i < size; i++) {
            int currField = PARSE_FIELD_OF_CALENDAR[i];
            if (currField == MONTH) {
                calendar.set(currField, Integer.parseInt(arguments[i]) - 1);
            } else {
                calendar.set(currField, Integer.parseInt(arguments[i]));
            }
        }
        for (; i < length; i++) {
            int currField = PARSE_FIELD_OF_CALENDAR[i];
            if (currField == DAY_OF_MONTH) {
                calendar.set(currField, 1);
            } else {
                calendar.set(currField, 0);
            }
        }
        return calendar;
    }

    public final static Calendar toCalendar(Number value) {
        return value instanceof Long ? toCalendar(value.longValue()) : toCalendar(value.intValue());
    }

    public final static Calendar toCalendar(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return calendar;
    }

    public final static Calendar toCalendar(Date date) {
        return toCalendar(date.getTime());
    }

    public final static Calendar toCalendar(LocalDate date) {
        return toCalendar(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0);
    }

    public final static Calendar toCalendar(LocalDateTime date) {
        return toCalendar(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), date.getHour(), date.getMinute(), date.getSecond());
    }

    public final static Calendar toCalendar(int... values) {
        final int len = values.length, max = 6;
        int i = 0;
        Calendar calendar = Calendar.getInstance();
        switch (len) {
            case 0:
                return calendar;
            case 1:
                calendar.set(YEAR, values[i++]);
                break;
            case 2:
                calendar.set(YEAR, values[i++]);
                calendar.set(MONTH, values[i++] - 1);
                break;
            case 3:
                calendar.set(values[i++], values[i++] - 1, values[i++]);
                break;
            case 4:
                int minutes = calendar.get(MINUTE);
                calendar.set(values[i++], values[i++] - 1, values[i++], values[i++], 0);
                calendar.set(MINUTE, minutes);
                break;
            case 5:
                calendar.set(values[i++], values[i++] - 1, values[i++], values[i++], values[i++]);
                break;
            case 6:
                calendar.set(values[i++], values[i++] - 1, values[i++], values[i++], values[i++], values[i++]);
            default:
                if (len > max) {
                    calendar.set(MILLISECOND, values[i++]);
                }
                break;
        }
        return calendar;
    }

    public static Calendar toCalendar(CharSequence value) {
        String temp = value.toString();
        if (DetectUtil.isNumeric(temp)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(LongUtil.toLong(temp));
            return calendar;
        }
        return parseToCalendar(value.toString());
    }

    public static Calendar toCalendar(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Calendar) {
            return ((Calendar) value);
        }
        if (value instanceof Date) {
            return toCalendar((Date) value);
        }
        if (value instanceof Number) {
            return toCalendar((Number) value);
        }
        if (value instanceof LocalDate) {
            return toCalendar((LocalDate) value);
        }
        if (value instanceof LocalDateTime) {
            return toCalendar((LocalDateTime) value);
        }
        if (value instanceof CharSequence) {
            return toCalendar((CharSequence) value);
        }
        if (value instanceof int[]) {
            return toCalendar((int[]) value);
        }
        if (value instanceof String[]) {
            return toCalendar((String[]) value);
        }
        throw new IllegalArgumentException("can not converter to java.util.CharSequence of value: " + value);
    }
}
