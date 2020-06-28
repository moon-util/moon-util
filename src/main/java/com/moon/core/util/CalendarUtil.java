package com.moon.core.util;

import com.moon.core.enums.Const;
import com.moon.core.lang.LongUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static java.lang.Integer.parseInt;
import static java.util.Calendar.*;

/**
 * @author benshaoye
 */
public class CalendarUtil {

    protected CalendarUtil() { noInstanceError(); }

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
    public final static String HH_mm_ss = "HH:mm:ss";
    public final static String HH_mm = "HH:mm";

    public final static String DATE_PATTERN = yyyy_MM_dd;

    public final static String MONTH_PATTERN = yyyy_MM;

    public final static String TIME_PATTERN = HH_mm_ss;

    public final static String PATTERN = yyyy_MM_dd_HH_mm_ss;

    private final static String[] PATTERNS = {
        yyyy, yyyy_MM, yyyy_MM_dd, yyyy_MM_dd_HH, yyyy_MM_dd_HH_mm, yyyy_MM_dd_HH_mm_ss, yyyy_MM_dd_HH_mm_ss_SSS,
    };

    private final static int[] PARSE_FIELD_OF_CALENDAR = {
        YEAR, MONTH, DAY_OF_MONTH, HOUR_OF_DAY, MINUTE, SECOND, MILLISECOND
    };

    final static Calendar current() { return getInstance(); }

    /*
     * -------------------------------------------------------------------------
     * is
     * -------------------------------------------------------------------------
     */

    public final static boolean isToday(Calendar value) { return value != null && isSameDay(value, current()); }

    public final static boolean isSameDay(Calendar value, Calendar other) {
        return value != null &&
            getYear(value) == getYear(other) &&
            getMonth(value) == getMonth(other) &&
            getDayOfMonth(value) == getDayOfMonth(other);
    }

    public final static boolean isSameTime(Calendar value, Calendar other) {
        return value != null &&
            getYear(value) == getYear(other) &&
            getMonth(value) == getMonth(other) &&
            getDayOfMonth(value) == getDayOfMonth(other) &&
            getHour(value) == getHour(other) &&
            getMinute(value) == getMinute(other) &&
            getSecond(value) == getSecond(other);
    }

    public final static boolean isBefore(Calendar value, Calendar other) {
        return value != null && value.getTimeInMillis() < other.getTimeInMillis();
    }

    public final static boolean isAfter(Calendar value, Calendar other) {
        return value != null && value.getTimeInMillis() > other.getTimeInMillis();
    }

    public final static boolean isBeforeNow(Calendar value) { return isBefore(value, current()); }

    public final static boolean isAfterNow(Calendar value) { return isAfter(value, current()); }

    /*
     * -------------------------------------------------------------------------
     * now
     * -------------------------------------------------------------------------
     */

    public final static int nowYear() { return getYear(current()); }

    public final static int nowMonth() { return getMonth(current()); }

    public final static int nowDayOfMonth() { return getDayOfMonth(current()); }

    public final static int nowDayOfYear() { return getDayOfYear(current()); }

    public final static int nowHours() { return getHour(current()); }

    public final static int nowMinutes() { return getMinute(current()); }

    public final static int nowSeconds() { return getSecond(current()); }

    public final static int nowMilliseconds() { return getMillisecond(current()); }

    public final static Calendar nowCalendar() { return current(); }

    public final static long now() { return System.currentTimeMillis(); }

    /**
     * 默认当前时间，如果指定时间为 null 的话
     *
     * @param calendar 指定时间
     *
     * @return
     */
    public final static Calendar nowIfNull(Calendar calendar) {
        return calendar == null ? current() : calendar;
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

    public final static Calendar clearMilliseconds(Calendar calendar) { return setMillisecond(calendar, 0); }

    /*
     * -------------------------------------------------------------------------
     * copies
     * -------------------------------------------------------------------------
     */

    public final static Calendar copy(Calendar value) { return toCalendar(value.getTimeInMillis()); }

    /*
     * -------------------------------------------------------------------------
     * next and prev
     * -------------------------------------------------------------------------
     */

    public final static Calendar nextYear(Calendar value) { return plusYears(value, 1); }

    public final static Calendar nextMonth(Calendar value) { return plusMonths(value, 1); }

    public final static Calendar nextDay(Calendar value) { return plusDays(value, 1); }

    public final static Calendar nextHour(Calendar value) { return plusHours(value, 1); }

    public final static Calendar nextMinute(Calendar value) { return plusMinutes(value, 1); }

    public final static Calendar nextSecond(Calendar value) { return plusSeconds(value, 1); }

    public final static Calendar nextMillisecond(Calendar value) { return plusMilliSeconds(value, 1); }

    public final static Calendar prevYear(Calendar value) { return minusYears(value, 1); }

    public final static Calendar prevMonth(Calendar value) { return minusMonths(value, 1); }

    public final static Calendar prevDay(Calendar value) { return minusDays(value, 1); }

    public final static Calendar prevHour(Calendar value) { return minusHours(value, 1); }

    public final static Calendar prevMinute(Calendar value) { return minusMinutes(value, 1); }

    public final static Calendar prevSecond(Calendar value) { return minusSeconds(value, 1); }

    public final static Calendar prevMillisecond(Calendar value) { return minusMilliSeconds(value, 1); }

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

    public final static Calendar setYear(Calendar value, int amount) { return set(value, YEAR, amount); }

    public final static Calendar setMonth(Calendar value, int amount) { return set(value, MONTH, amount); }

    public final static Calendar setDayOfMonth(Calendar value, int amount) { return set(value, DAY_OF_MONTH, amount); }

    public final static Calendar setHour(Calendar value, int amount) { return set(value, HOUR_OF_DAY, amount); }

    public final static Calendar setMinute(Calendar value, int amount) { return set(value, MINUTE, amount); }

    public final static Calendar setSecond(Calendar value, int amount) { return set(value, SECOND, amount); }

    public final static Calendar setMillisecond(Calendar value, int amount) { return set(value, MILLISECOND, amount); }

    /**
     * 获取年份
     *
     * @param value Calendar
     *
     * @return 年份
     */
    public final static int getYear(Calendar value) { return get(value, YEAR); }

    /**
     * 获取月份；
     * <p>
     * 注在{@link Date}或{@link Calendar}中直接获取到的月份比实际的要少一月，即当前是 6 月获取到的实际是 5 月；
     * 此工具类针对月份做了统一处理，是几月获取到的就是几月
     *
     * @param value Calendar 对象
     *
     * @return 月份
     */
    public final static int getMonth(Calendar value) { return get(value, MONTH); }

    /**
     * 获取星期
     *
     * @param value Calendar 对象
     *
     * @return 星期枚举
     */
    public final static int getDayOfWeek(Calendar value) { return get(value, DAY_OF_WEEK); }

    /**
     * 一月中的第 N 天。如一月一日 => 第 1 天；一月 31 日是第 31 天；二月 1 日是 2 月的第 1 天
     *
     * @param value Calendar 对象
     *
     * @return 月中的第几天
     */
    public final static int getDayOfMonth(Calendar value) { return get(value, DAY_OF_MONTH); }

    /**
     * 一年中的第 N 天。如一月一日 => 第 1 天；一月 31 日是第 31 天；二月 1 日是第 32 天
     *
     * @param value Calendar 对象
     *
     * @return 年中的第几日
     */
    public final static int getDayOfYear(Calendar value) { return get(value, DAY_OF_YEAR); }

    /**
     * 获取小时
     *
     * @param value
     *
     * @return
     */
    public final static int getHour(Calendar value) { return get(value, HOUR_OF_DAY); }

    /**
     * 获取分钟
     *
     * @param value
     *
     * @return
     */
    public final static int getMinute(Calendar value) { return get(value, MINUTE); }

    /**
     * 获取秒数
     *
     * @param value
     *
     * @return
     */
    public final static int getSecond(Calendar value) { return get(value, SECOND); }

    /**
     * 获取毫秒数
     *
     * @param value
     *
     * @return
     */
    public final static int getMillisecond(Calendar value) { return get(value, MILLISECOND); }

    public final static Calendar set(Calendar value, int field, int amount) {
        Calendar copied = copy(value);
        copied.set(field, field == MONTH ? amount - 1 : amount);
        return copied;
    }

    public final static int get(Calendar cal, int field) {
        return field == MONTH ? cal.get(field) + 1 : cal.get(field);
    }

    /*
     * -------------------------------------------------------------------------
     * formatter
     * -------------------------------------------------------------------------
     */

    public final static DateFormat toFormat(String pattern) { return new SimpleDateFormat(pattern); }

    public final static String format(DateFormat pattern, int... values) { return format(toCalendar(values), pattern); }

    public final static String format(String pattern, int... values) { return format(toFormat(pattern), values); }

    public final static String format(int... values) { return format(PATTERNS[values.length - 1], values); }

    public final static String format(DateFormat pattern, String... values) {
        return format(toCalendar(values), pattern);
    }

    public final static String format(String... values) {
        return format(toFormat(PATTERNS[values.length - 1]), values);
    }

    public final static String format() { return format(PATTERN); }

    public final static String format(String pattern) { return pattern == null ? null : format(new Date(), pattern); }

    public final static String format(Date date, String pattern) {
        return date == null ? null : format(date, toFormat(pattern));
    }

    public final static String format(Date date, DateFormat formatter) {
        return date == null ? null : formatter.format(date);
    }

    public final static String format(Date date) { return date == null ? null : format(date, PATTERN); }

    public final static String format(Calendar date) { return date == null ? null : format(date.getTime()); }

    public final static String format(Calendar date, String pattern) {
        return date == null ? null : format(date.getTime(), pattern);
    }

    public final static String format(Calendar date, DateFormat formatter) {
        return date == null ? null : format(date.getTime(), formatter);
    }

    public final static String toYyyyMM(Date date) { return format(date, yyyy_MM); }

    public final static String toYyyyMMDd(Date date) { return format(date, yyyy_MM_dd); }

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

            List<String> fieldsValue = new ArrayList<>();
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

            length = Math.min(size, length);
            Calendar calendar = getInstance();
            calendar.clear();

            for (int i = 0; i < length; i++) {
                int currField = PARSE_FIELD_OF_CALENDAR[i];
                if (currField == MONTH) {
                    calendar.set(currField, parseInt(fieldsValue.get(i)) - 1);
                } else {
                    calendar.set(currField, parseInt(fieldsValue.get(i)));
                }
            }

            return calendar;
        }

        return null;
    }

    /*
     * toCalendar ============================================
     */

    /**
     * 解析成 Calendar 日期
     *
     * @param values 用 String 表示的年、月、日、时、分、秒、毫秒等一个或多个字段按顺序传入
     */
    public final static Calendar toCalendar(String... values) {
        if (values == null) { return null; }
        int size = values.length;
        int length = PARSE_FIELD_OF_CALENDAR.length;
        size = Math.min(size, length);
        Calendar calendar = getInstance();
        calendar.clear();
        int i = 0;
        for (; i < size; i++) {
            int currField = PARSE_FIELD_OF_CALENDAR[i];
            if (currField == MONTH) {
                calendar.set(currField, parseInt(values[i]) - 1);
            } else {
                calendar.set(currField, parseInt(values[i]));
            }
        }
        for (; i < length; i++) {
            int field = PARSE_FIELD_OF_CALENDAR[i];
            calendar.set(field, field == DAY_OF_MONTH ? 1 : 0);
        }
        return calendar;
    }

    public final static Calendar toCalendar(Number num) { return num == null ? null : toCalendar(num.longValue()); }

    public final static Calendar toCalendar(long timeMillis) {
        Calendar calendar = getInstance();
        calendar.setTimeInMillis(timeMillis);
        return calendar;
    }

    public final static Calendar toCalendar(Date date) { return date == null ? null : toCalendar(date.getTime()); }

    public final static Calendar toCalendar(LocalDate date, LocalTime time) {
        return toCalendar(date.getYear(), date.getDayOfMonth(), date.getDayOfMonth(), time.getHour(), time.getMinute(),
            time.getSecond());
    }

    public final static Calendar toCalendar(LocalTime time) {
        return time == null ? null : toCalendar(LocalDate.now(), time);
    }

    public final static Calendar toCalendar(LocalDate date) {
        return date == null ? null : toCalendar(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0);
    }

    public final static Calendar toCalendar(LocalDateTime date) {
        return date == null ? null
            : toCalendar(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), date.getHour(), date.getMinute(),
                date.getSecond());
    }

    public final static Calendar toCalendar(int... values) {
        if (values == null) { return null; }
        final int len = values.length, max = 6;
        int i = 0;
        Calendar calendar = current();
        switch (len) {
            case 0:
                return calendar;
            case 1:
                calendar.set(YEAR, values[i]);
                break;
            case 2:
                calendar.set(YEAR, values[i++]);
                calendar.set(MONTH, values[i] - 1);
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

    public final static Calendar toCalendar(CharSequence value) {
        if (value == null) { return null; }
        String temp = value.toString();
        return TestUtil.isDigit(temp) ? toCalendar(LongUtil.toLong(temp)) : parseToCalendar(temp);
    }

    public final static Calendar toCalendar(Object value) {
        if (value == null) { return null; }
        if (value instanceof Calendar) { return ((Calendar) value); }
        if (value instanceof Date) { return toCalendar((Date) value); }
        if (value instanceof Number) { return toCalendar(((Number) value).longValue()); }
        if (value instanceof LocalDate) { return toCalendar((LocalDate) value); }
        if (value instanceof LocalDateTime) { return toCalendar((LocalDateTime) value); }
        if (value instanceof LocalTime) { return toCalendar((LocalTime) value); }
        if (value instanceof CharSequence) { return toCalendar((CharSequence) value); }
        if (value instanceof int[]) { return toCalendar((int[]) value); }
        if (value instanceof String[]) { return toCalendar((String[]) value); }
        throw new IllegalArgumentException("can not converter to java.lang.CharSequence of value: " + value);
    }
}
