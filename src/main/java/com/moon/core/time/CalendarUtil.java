package com.moon.core.time;

import com.moon.core.enums.IntTesters;
import com.moon.core.enums.Testers;
import com.moon.core.lang.LongUtil;
import com.moon.core.util.TestUtil;
import com.moon.core.util.validator.ResidentID18Validator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.LongFunction;
import java.util.function.Supplier;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static java.lang.Integer.parseInt;
import static java.util.Calendar.*;

/**
 * 1. 本类所有返回{@link Calendar}的方法均是返回新对象，而不是在原有对象上操作
 * <p>
 * 2. 本类针对 MONTH 字段做了人性化处理，在标准 Calendar 中， MONTH 字段的 5，实际上对应的是 6 月，这里作人性化
 * 处理之后，你只需要考虑想要设置的值，比如：“我想给 Calendar 设置为 5 月” ==> {@code CalendarUtil.setMonth(5);}即可;
 * <p>
 * 如：
 * <pre>
 *     Calendar calendar = Calendar.getInstance();
 *     Calendar resultCalendar = CalendarUtil.nextYear(calendar);
 *     assertTrue(calendar != resultCalendar); // 传入对象和返回对象是不同的
 * </pre>
 *
 * @author moonsky
 */
public class CalendarUtil {

    private final static class ImportedJoda {

        final static boolean IMPORTED_JODA;

        static {
            boolean importedJoda;
            try {
                org.joda.time.DateTime.now();
                importedJoda = true;
            } catch (Throwable t) {
                importedJoda = false;
            }
            IMPORTED_JODA = importedJoda;
        }
    }

    protected CalendarUtil() { noInstanceError(); }

    /**
     * 最小毫秒数：1970-01-01 08:00:00 000
     */
    public final static long MIN_TIME_IN_MILLIS = 0;

    /**
     * 最大毫秒数：9999-12-31 23:59:59 999
     */
    public final static long MAX_TIME_IN_MILLIS = 253402271999999L;

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
    private final static int[] PARSE_FIELD_INITIALIZE_VALUES = {-1, 1, 1, 0, 0, 0, 0};

    final static Calendar getInstance() { return Calendar.getInstance(); }

    /**
     * 是否引入 joda 时间
     *
     * @return
     */
    public final static boolean isImportedJodaTime() { return ImportedJoda.IMPORTED_JODA; }

    /*
     * -------------------------------------------------------------------------
     * is
     * -------------------------------------------------------------------------
     */

    public final static boolean isToday(Calendar value) { return value != null && isSameDay(value, getInstance()); }

    public final static boolean isSameDay(Calendar value, Calendar other) {
        return value != null && getYear(value) == getYear(other) && getMonth(value) == getMonth(other) && getDayOfMonth(
            value) == getDayOfMonth(other);
    }

    public final static boolean isSameTime(Calendar value, Calendar other) {
        return value != null && getYear(value) == getYear(other) && getMonth(value) == getMonth(other) && getDayOfMonth(
            value) == getDayOfMonth(other) && getHour(value) == getHour(other) && getMinute(value) == getMinute(other) && getSecond(
            value) == getSecond(other);
    }

    public final static boolean isBefore(Calendar value, Calendar other) {
        return value != null && value.getTimeInMillis() < other.getTimeInMillis();
    }

    public final static boolean isAfter(Calendar value, Calendar other) {
        return value != null && value.getTimeInMillis() > other.getTimeInMillis();
    }

    public final static boolean isBeforeNow(Calendar value) { return isBefore(value, getInstance()); }

    public final static boolean isAfterNow(Calendar value) { return isAfter(value, getInstance()); }

    public final static boolean isEqualsMonthDay(Calendar calendar1, Calendar calendar2) {
        return getMonth(calendar1) == getMonth(calendar2) && getDayOfMonth(calendar1) == getDayOfMonth(calendar2);
    }

    public final static boolean isBeforeMonthDay(Calendar calendar1, Calendar calendar2) {
        return isBeforeMonthDay(calendar1, getMonth(calendar2), getDayOfMonth(calendar2));
    }

    public final static boolean isBeforeMonthDay(Calendar calendar1, int month, int dayOfMonth) {
        int month1 = getMonth(calendar1);
        return month1 > month || (month1 == month && getDayOfMonth(calendar1) > dayOfMonth);
    }

    public final static boolean isAfterMonthDay(Calendar calendar1, Calendar calendar2) {
        return isAfterMonthDay(calendar1, getMonth(calendar2), getDayOfMonth(calendar2));
    }

    public final static boolean isAfterMonthDay(Calendar calendar1, int month, int dayOfMonth) {
        int month1 = getMonth(calendar1);
        return month1 < month || (month1 == month && getDayOfMonth(calendar1) < dayOfMonth);
    }

    /*
     * -------------------------------------------------------------------------
     * now
     * -------------------------------------------------------------------------
     */

    public final static int nowYear() { return getYear(getInstance()); }

    public final static int nowMonth() { return getMonth(getInstance()); }

    public final static int nowDayOfMonth() { return getDayOfMonth(getInstance()); }

    public final static int nowDayOfYear() { return getDayOfYear(getInstance()); }

    public final static int nowHour() { return getHour(getInstance()); }

    public final static int nowMinute() { return getMinute(getInstance()); }

    public final static int nowSecond() { return getSecond(getInstance()); }

    public final static int nowMillisecond() { return getMillisecond(getInstance()); }

    public final static Calendar getCalendar() { return getInstance(); }

    /**
     * 当前时间戳（毫秒数）
     *
     * @return 返回当前时刻时间戳
     */
    public final static long nowTimeMillis() { return System.currentTimeMillis(); }

    /**
     * 当前时间戳（秒数）
     *
     * @return 时间戳（秒数）
     */
    public final static long nowTimeSeconds() { return nowTimeMillis() / 1000; }

    /**
     * 默认当前时间，如果指定时间为 null 的话
     *
     * @param calendar 指定时间
     *
     * @return
     */
    public final static Calendar nowIfNull(Calendar calendar) {
        return calendar == null ? getInstance() : calendar;
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

    public final static Calendar startOfSecond(Calendar calendar) { return setMillisecond(calendar, 0); }

    public final static Calendar startOfDay(Calendar calendar) {
        return toCalendar(getYear(calendar), getMonth(calendar), getDayOfMonth(calendar));
    }

    public final static Calendar startOfMonth(Calendar calendar) {
        return toCalendar(getYear(calendar), getMonth(calendar));
    }

    public final static Calendar startOfYear(Calendar calendar) { return toCalendar(getYear(calendar)); }

    public final static Calendar endOfYear(Calendar calendar) {
        return new DateTime(calendar).endOfYear().originCalendar();
    }

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
        return setHourOfDay(value, getHour(value) + amount);
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
        return setHourOfDay(value, getHour(value) - amount);
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

    public final static Calendar setHourOfDay(Calendar value, int amount) { return set(value, HOUR_OF_DAY, amount); }

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
     * @param value Calendar 对象
     *
     * @return 小时数
     */
    public final static int getHour(Calendar value) { return get(value, HOUR_OF_DAY); }

    /**
     * 获取分钟
     *
     * @param value Calendar 对象
     *
     * @return 分钟数
     */
    public final static int getMinute(Calendar value) { return get(value, MINUTE); }

    /**
     * 获取秒数
     *
     * @param value Calendar 对象
     *
     * @return 秒数
     */
    public final static int getSecond(Calendar value) { return get(value, SECOND); }

    /**
     * 获取毫秒数
     *
     * @param value Calendar 对象
     *
     * @return 毫秒数
     */
    public final static int getMillisecond(Calendar value) { return get(value, MILLISECOND); }

    /**
     * 根据日期获取年龄（周岁）
     *
     * @param birthday 出生日期
     *
     * @return 周岁
     *
     * @see ResidentID18Validator#getAge()
     */
    public final static int getAge(Calendar birthday) { return getAge(birthday, getInstance()); }

    /**
     * 根据日期获取年龄（虚岁）
     *
     * @param birthday 出生日期
     *
     * @return 虚岁
     *
     * @see ResidentID18Validator#getNominalAge()
     */
    public final static int getNominalAge(Calendar birthday) { return getAge(birthday) + 1; }

    /**
     * 返回生日到指定日期的周岁数
     *
     * @param birthday 出生日期
     * @param endDate  指定日期
     *
     * @return 周岁
     */
    public final static int getAge(Calendar birthday, Calendar endDate) {
        int age = getYear(endDate) - getYear(birthday);
        return isBeforeMonthDay(birthday, endDate) ? age - 1 : age;
    }

    /**
     * 返回生日到指定日期的周虚岁
     *
     * @param birthday 出生日期
     * @param endDate  指定日期
     *
     * @return 虚岁
     */
    public final static int getNominalAge(Calendar birthday, Calendar endDate) { return getAge(birthday, endDate) + 1; }

    /**
     * 设置日期指定字段值，总是返回一个新对象
     * <p>
     * 注意 month 字段人性化处理：在 Calendar 标准处理中，设置的月份是实际月份加一，即如果设置 5 月，实际设置到日期的是 6 月；
     * 这个方法对这进行了处理，设置的是实际月份，即：如果设置 5 月，实际设置到日期的就是 5 月
     *
     * @param value  日期
     * @param field  字段
     * @param amount 字段值
     *
     * @return 设置新值后的新对象
     */
    public final static Calendar set(Calendar value, int field, int amount) {
        return originSetField(copy(value), field, amount);
    }

    /**
     * 非复制设置字段值，同样针对 month 字段做了人性化处理
     *
     * @param value  calendar
     * @param field  field
     * @param amount 差值
     *
     * @return calendar
     */
    public final static Calendar originSetField(Calendar value, int field, int amount) {
        value.set(field, field == MONTH ? amount - 1 : amount);
        return value;
    }

    /**
     * 获取 Calendar 指定字段值
     * <p>
     * 注意 month 字段人性化处理：在 Calendar 标准处理中，获取到的月份是实际月份减一，即如果现在是 5 月，获取到的是 4 月；
     * 这个方法对这进行了处理，获取到的是实际月份，即：即如果现在是 5 月，获取到的就是 5 月
     *
     * @param cal   日期
     * @param field 字段
     *
     * @return 字段值
     */
    public final static int get(Calendar cal, int field) {
        return field == MONTH ? cal.get(field) + 1 : cal.get(field);
    }

    /*
     * -------------------------------------------------------------------------
     * formatter
     * -------------------------------------------------------------------------
     */

    /**
     * 返回指定模式的 DateFormat
     *
     * @param pattern 模式
     *
     * @return DateFormat
     */
    public final static DateFormat toFormat(String pattern) { return new SimpleDateFormat(pattern); }

    /**
     * 按模式 yyyy-MM-dd HH:mm:ss 格式化当前日期
     *
     * @return 格式化后的字符串
     */
    public final static String format() { return format(PATTERN); }

    /**
     * 按指定模式格式化当前日期
     *
     * @param pattern 日期格式
     *
     * @return 格式化后的日期字符串
     */
    public final static String format(String pattern) { return pattern == null ? null : format(new Date(), pattern); }

    /**
     * 按指定模式格式化日期
     *
     * @param date    将要格式化的日期
     * @param pattern 日期格式
     *
     * @return 格式化后的日期字符串
     */
    public final static String format(Date date, String pattern) {
        return date == null ? null : format(date, toFormat(pattern));
    }

    /**
     * 按指定模式格式化日期
     *
     * @param date      将要格式化的日期
     * @param formatter 日期格式
     *
     * @return 格式化后的日期字符串
     */
    public final static String format(Date date, DateFormat formatter) {
        return date == null ? null : formatter.format(date);
    }

    /**
     * 按模式 yyyy-MM-dd HH:mm:ss 格式化指定日期
     *
     * @param date 将要格式化的日期
     *
     * @return 格式化后的日期字符串
     */
    public final static String format(Date date) { return date == null ? null : format(date, PATTERN); }

    /**
     * 按模式 yyyy-MM-dd HH:mm:ss 格式化指定日期
     *
     * @param date 将要格式化的日期
     *
     * @return 格式化后的日期字符串
     */
    public final static String format(Calendar date) { return date == null ? null : format(date.getTime()); }

    /**
     * 按指定模式格式化日期
     *
     * @param date    将要格式化的日期
     * @param pattern 日期格式
     *
     * @return 格式化后的日期字符串
     */
    public final static String format(Calendar date, String pattern) {
        return date == null ? null : format(date.getTime(), pattern);
    }

    /**
     * 按指定模式格式化日期
     *
     * @param date      将要格式化的日期
     * @param formatter 日期格式
     *
     * @return 格式化后的日期字符串
     */
    public final static String format(Calendar date, DateFormat formatter) {
        return date == null ? null : format(date.getTime(), formatter);
    }

    /**
     * 将指定日期格式化成年月字符串
     *
     * @param date 将要格式化的日期
     *
     * @return 格式化后的年月字符串
     */
    public final static String toYearMonth(Date date) { return format(date, yyyy_MM); }

    /**
     * 将指定日期格式化成年月日字符串
     *
     * @param date 将要格式化的日期
     *
     * @return 格式化后的年月日字符串
     */
    public final static String toYearMonthDay(Date date) { return format(date, yyyy_MM_dd); }

    /*
     * -------------------------------------------------------------------------
     * parsers
     * -------------------------------------------------------------------------
     */


    /**
     * 解析成 Calendar 日期
     * <p>
     * 要求符合格式 "yyyy-MM-dd HH:mm:ss SSS" 的一个或多个字段；
     * 1. 超出部分将忽略
     * 2. 不足部分将用该字段的默认值填充，比如：月、日填充为 1，时间的各字段填充为 0
     *
     * @param datetimeString 符合格式 "yyyy-MM-dd HH:mm:ss SSS" 一个或多个字段的字符串
     *
     * @see #extractDateTimeFields(CharSequence)
     */
    public static Calendar parseToCalendar(String datetimeString) {
        return transformDateTimeString(datetimeString, CalendarUtil::toCalendar, CalendarUtil::toCalendar);
    }

    /**
     * 提取日期各个字段值
     * <p>
     * 要求符合格式：yyyy-MM-dd HH:mm:ss SSS 的一个或多个字段，并且每个字段的位数与格式匹配(具体参考下面示例)
     * <ul>
     * <li>1. 超出部分将忽略</li>
     * <li>2. 不足部分用该字段初始值填充，月日填充 1，时间各字段填充 0；</li>
     * <li>3. 如果各字段之间有有效的非数字符号（汉字、-、/、: 、. 等）间隔，间隔符号之间的数字就是字段值，格式无特殊要求</li>
     * <li>4. 如果只有连续的数字则严格要求年份是 4 位，月日时分秒字段都是两位，最后紧接的最多三位连续数字是毫秒数</li>
     * </ul>
     * 示例：
     * <pre>
     * |-----------------------------------------------------------------------------------|
     * | Date String                 | Year | Month | Day | Hour | Minute | Second | Micro |
     * |-----------------------------|------|-------|-----|------|--------|--------|-------|
     * | 1980年02月03日08时09分59秒23  | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 1980年2月3日8时9分59秒23      | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 1980年02月03日08095923       | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 1980-02-03 08:09:59.23      | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 1980-02/03 08.09*59 23      | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 1980 02 03 08 09 59 23      | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 1980 023 08 09 59 23        | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 19802 3 08 09 59 23         | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 198023 08 09 59 23          | 1980 | 23    | 08  | 09   | 59     | 23     | 00    | 注
     * | 1980 02 03 08 09 59         | 1980 | 02    | 03  | 08   | 09     | 59     | 00    |
     * | 1980020308095923            | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 1980 020308 095923          | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 19800203                    | 1980 | 02    | 03  | 00   | 00     | 00     | 00    | 注
     * | 日期19800203时间080959毫秒23  | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * |-----------------------------|------|-------|-----|------|--------|--------|-------|
     * | 999                         | 999  | 01    | 01  | 00   | 00     | 00     | 00    |
     * | 1                           | 1    | 01    | 01  | 00   | 00     | 00     | 00    |
     * |-----------------------------------------------------------------------------------|
     * </pre>
     *
     * @param datetimeString 输入的日期字符串
     *
     * @return 日期年、月、日、时、分、秒、毫秒各字段的值或如果字符串中不包含任何数字时返回 null
     */
    public final static int[] extractDateTimeFields(CharSequence datetimeString) {
        return doExtractDateTimeFields(datetimeString, DATETIME_BUILDER, FIELDS_LENGTH_DATE_TIME, 4);
    }

    private final static Supplier<int[]> DATETIME_BUILDER = () -> new int[]{1970, 1, 1, 0, 0, 0, 0};
    private final static byte[] FIELDS_LENGTH_DATE_TIME = {4, 2, 2, 2, 2, 2, 3};
    private final static byte[] FIELDS_LENGTH_TIME = {2, 2, 2, 3};

    /**
     * 提取时间各个字段值，参考{@link #extractDateTimeFields(CharSequence)}
     * <p>
     * 要求符合格式：HH:mm:ss SSS 的一个或多个字段，并且每个字段的位数一致
     *
     * @param timeString 时间字符串
     *
     * @return 时间时、分、秒、毫秒各字段的值或如果字符串中不包含任何数字时返回 null
     */
    public final static int[] extractTimeFields(CharSequence timeString) {
        return doExtractDateTimeFields(timeString, () -> new int[]{0, 0, 0, 0}, FIELDS_LENGTH_TIME, 3);
    }

    /**
     * 提取时间日期各字段值
     *
     * @param datetimeString          日期时间字符串
     * @param initResultFieldsCreator 返回值各字段初始化构造器
     * @param maxLengthOfEachField    各个字段的最大长度（这个数组的长度要和{@code initResultFieldsCreator}返回值长度相同）
     * @param maxLengthInAllFields    所有字段长度的最大值，这个是{@code maxLengthOfEachField}里的最大值，手动输入，避免一次循环判断
     *
     * @return 日期各字段值组成的数组
     */
    protected static int[] doExtractDateTimeFields(
        CharSequence datetimeString,
        Supplier<int[]> initResultFieldsCreator,
        byte[] maxLengthOfEachField,
        int maxLengthInAllFields
    ) {
        return doExtractFields(datetimeString,
            IntTesters.DIGIT,
            initResultFieldsCreator,
            maxLengthOfEachField,
            maxLengthInAllFields);
    }

    /**
     * 提取时间日期各字段值
     *
     * @param datetimeString          日期时间字符串
     * @param tester                  判断器，判断是否符合条件，符合条件的字符属于字段的组成部分
     * @param initResultFieldsCreator 返回值各字段初始化构造器
     * @param maxLengthOfEachField    各个字段的最大长度（这个数组的长度要和{@code initResultFieldsCreator}返回值长度相同）
     * @param maxLengthInAllFields    所有字段长度的最大值，这个是{@code maxLengthOfEachField}里的最大值，手动输入，避免一次循环判断
     *
     * @return 日期各字段值组成的数组
     */
    protected static int[] doExtractFields(
        CharSequence datetimeString,
        IntPredicate tester,
        Supplier<int[]> initResultFieldsCreator,
        byte[] maxLengthOfEachField,
        int maxLengthInAllFields
    ) {
        if (datetimeString == null || datetimeString.length() == 0) {
            return null;
        }
        char[] chars = datetimeString.toString().toCharArray(), field = new char[maxLengthInAllFields];
        int fieldIdx = 0, valuesIdx = 0, strLen = chars.length, strIdx = skipNonNumeric(chars, 0, strLen);
        if (strIdx == strLen) {
            return null;
        }
        final int[] values = initResultFieldsCreator.get();
        int last = Math.min(strLen, maxLengthOfEachField[0]), vLastIdx = values.length - 1;

        for (char ch; strIdx < strLen; ) {
            if (tester.test(ch = chars[strIdx++])) {
                field[fieldIdx++] = ch;
                if (fieldIdx < last) {
                    continue;
                }
            }
            values[valuesIdx++] = toInt(field, fieldIdx);
            if (valuesIdx > vLastIdx) {
                return values;
            }
            strIdx = skipNonNumeric(chars, strIdx, strLen);
            last = maxLengthOfEachField[valuesIdx];
            fieldIdx = 0;
        }
        if (valuesIdx == 0) {
            return null;
        }
        if (fieldIdx > 0) {
            values[valuesIdx] = toInt(field, fieldIdx);
        }
        return values;
    }

    private final static int toInt(char[] chars, int lastIdx) {
        return Integer.parseInt(new String(chars, 0, lastIdx));
    }

    /**
     * 是否是合法的日期字段列表
     *
     * @param values 日期各字段值按顺序列表
     *
     * @return 日期各字段值符合要求范围值时返回 true，否则返回 false
     */
    public final static boolean isValidDateTimeFields(int... values) {
        return isValidDateTimeFields(0, 9999, false, values);
    }

    /**
     * 是否是合法的日期字段列表
     *
     * @param minYear    最小年份
     * @param maxYear    最大年份
     * @param leapSecond 是否闰秒（通常都是 false）
     * @param values     日期各字段值按顺序列表
     *
     * @return 日期各字段值符合要求范围值时返回 true，否则返回 false
     */
    public final static boolean isValidDateTimeFields(int minYear, int maxYear, boolean leapSecond, int... values) {
        int field, length = Math.max(values == null ? 0 : values.length, 7);
        switch (length) {
            case 0:
                return false;
            case 7:
                // 毫秒
                if ((field = values[6]) < 0 || field > 999) {
                    return false;
                }
            case 6:
                // 秒
                if ((field = values[5]) < 0 || field > (leapSecond ? 60 : 59)) {
                    return false;
                }
            case 5:
                // 分
                if ((field = values[4]) < 0 || field > 59) {
                    return false;
                }
            case 4:
                // 时
                if ((field = values[3]) < 0 || field > 23) {
                    return false;
                }
            case 3:
                // 日
                if ((field = values[1]) < 1 || field > 12) {
                    return false;
                } else {
                    int lengthOfMonth = Month.of(field).length(Year.isLeap(values[0]));
                    if ((field = values[2]) < 1 || field > lengthOfMonth) {
                        return false;
                    }
                }
            case 2:
                // 月
                if ((field = values[1]) < 1 || field > 12) {
                    return false;
                }
            case 1:
                // 年
                if ((field = values[0]) < minYear || field > maxYear) {
                    return false;
                }
        }
        return true;
    }

    /**
     * 从 startIdx 开始跳过所有非数字字符，返回下一个数字字符的索引，如果超过 chars 长度，则返回 charsLen
     *
     * @param chars    原始 chars
     * @param startIdx 其实索引
     * @param charsLen chars 长度
     *
     * @return 下一个数字字符索引或 charsLen
     */
    private final static int skipNonNumeric(char[] chars, int startIdx, int charsLen) {
        for (char ch; startIdx < charsLen; startIdx++) {
            if ((ch = chars[startIdx]) > 47 && ch < 58) {
                return startIdx;
            }
        }
        return charsLen;
    }

    /*
     * toCalendar ============================================
     */

    /**
     * 解析成 Calendar 日期
     * <p>
     * 要求用 String 表示的年、月、日、时、分、秒、毫秒等一个或多个字段按顺序传入
     * 1. 超出部分将忽略
     * 2. 不足部分将用该字段的默认值填充，比如：月、日填充为 1，时间的各字段填充为 0
     *
     * @param values 符合年、月、日、时、分、秒、毫秒顺序的字段
     */
    public final static Calendar toCalendar(String... values) {
        int valuesLen = values == null ? 0 : values.length;
        if (valuesLen < 1) { return null; }
        int length = PARSE_FIELD_OF_CALENDAR.length;
        int size = Math.min(valuesLen, length);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        int i = 0;
        for (int minLen = Math.min(size, PARSE_FIELD_OF_CALENDAR.length); i < minLen; i++) {
            originSetField(calendar, PARSE_FIELD_OF_CALENDAR[i], parseInt(values[i]));
        }
        for (; i < length; i++) {
            originSetField(calendar, PARSE_FIELD_OF_CALENDAR[i], PARSE_FIELD_INITIALIZE_VALUES[i]);
        }
        return calendar;
    }

    public final static Calendar toCalendar(int... values) {
        int size = values == null ? 0 : values.length;
        if (size < 1) { return null; }
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        int i = 0;
        for (int minLen = Math.min(size, PARSE_FIELD_OF_CALENDAR.length); i < minLen; i++) {
            originSetField(calendar, PARSE_FIELD_OF_CALENDAR[i], values[i]);
        }
        int length = PARSE_FIELD_OF_CALENDAR.length;
        for (; i < length; i++) {
            originSetField(calendar, PARSE_FIELD_OF_CALENDAR[i], PARSE_FIELD_INITIALIZE_VALUES[i]);
        }
        return calendar;
    }

    public final static Calendar toCalendar(Number num) { return num == null ? null : toCalendar(num.longValue()); }

    public final static Calendar toCalendar(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        return calendar;
    }

    public final static Calendar toCalendar(Date date) { return date == null ? null : toCalendar(date.getTime()); }

    public final static Calendar toCalendar(LocalDate date, LocalTime time) {
        return overrideCalendar(getInstance(), date, time);
    }

    public final static Calendar toCalendar(LocalTime time) { return toCalendar(LocalDate.now(), time); }

    public final static Calendar toCalendar(LocalDate date) {
        return overrideCalendar(getInstance(), date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0, 0);
    }

    public final static Calendar toCalendar(LocalDateTime datetime) {
        return overrideCalendar(getInstance(), datetime);
    }

    /**
     * 这里不需要检查 null：因为{@code extractDateTimeFields}、{@code isValidDateTimeFields}、
     * {@code TestUtil.isDigit}、{@code StringUtil.isAllMatches}均有检查
     *
     * @param datetimeString
     * @param fieldsValueTransformer
     * @param timestampTransformer
     * @param <T>
     *
     * @return
     */
    final static <T> T transformDateTimeString(
        CharSequence datetimeString, Function<int[], T> fieldsValueTransformer, LongFunction<T> timestampTransformer
    ) {
        int[] fields = extractDateTimeFields(datetimeString);
        if (isValidDateTimeFields(fields)) {
            return fieldsValueTransformer.apply(fields);
        } else if (TestUtil.isDigit(datetimeString)) {
            return timestampTransformer.apply(Long.parseLong(datetimeString.toString()));
        }
        throw new DateTimeParseException("不识别日期格式: " + datetimeString, datetimeString, 0);
    }

    /**
     * 暂时没有使用这种方式，增加复杂度
     * @param timestamp
     * @param fieldsValueTransformer
     * @param timestampTransformer
     * @param <T>
     * @return
     */
    @SuppressWarnings("all")
    final static <T> T transformLongTimestamp(
        long timestamp, Function<int[], T> fieldsValueTransformer, LongFunction<T> timestampTransformer
    ) {
        if (timestamp < 10000) {
            int[] values = {(int) timestamp};
            return fieldsValueTransformer.apply(values);
        }
        return timestampTransformer.apply(timestamp);
    }

    /**
     * 字符串解析为 Calendar
     * 1. 首先认为是符合：yyyy-MM-dd HH:mm:ss SSS 一个或多个字段的日期字符串
     * 2. 其次如果字符串是连续数字，则认为是时间戳毫秒数解析
     * <p>
     * 3. 如果是连续数字，且是日期字符串类似 yyyyMMddHHmmssSSS 的一个或多个字段值，请手动调用方法{@link #parseToCalendar(String)}
     *
     * @param value 日期时间字符串
     *
     * @return Calendar 对象
     *
     * @see #transformDateTimeString(CharSequence, Function, LongFunction)
     */
    public final static Calendar toCalendar(CharSequence value) {
        return transformDateTimeString(value, CalendarUtil::toCalendar, CalendarUtil::toCalendar);
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
        throw new IllegalArgumentException("can not converter to java.util.Calendar of value: " + value);
    }

    /*
     * -----------------------------------------------------------------------------------
     * 覆盖 Calendar 字段
     * -----------------------------------------------------------------------------------
     */

    public final static Calendar overrideCalendar(Calendar calendar, int... fieldsValue) {
        if (fieldsValue == null) { return null; }
        final int len = fieldsValue.length, max = 6;
        int i = 0;
        switch (len) {
            case 0:
                return calendar;
            case 1:
                calendar.set(YEAR, fieldsValue[i]);
                break;
            case 2:
                calendar.set(YEAR, fieldsValue[i++]);
                calendar.set(MONTH, fieldsValue[i] - 1);
                break;
            case 3:
                calendar.set(fieldsValue[i++], fieldsValue[i++] - 1, fieldsValue[i]);
                break;
            case 4:
                int minute = calendar.get(MINUTE);
                calendar.set(fieldsValue[i++], fieldsValue[i++] - 1, fieldsValue[i++], fieldsValue[i], minute);
                break;
            case 5:
                calendar.set(fieldsValue[i++],
                    fieldsValue[i++] - 1,
                    fieldsValue[i++],
                    fieldsValue[i++],
                    fieldsValue[i++]);
                break;
            default:
                calendar.set(fieldsValue[i++],
                    fieldsValue[i++] - 1,
                    fieldsValue[i++],
                    fieldsValue[i++],
                    fieldsValue[i++],
                    fieldsValue[i++]);
                if (len > max) {
                    calendar.set(MILLISECOND, fieldsValue[i]);
                }
                break;
        }
        return calendar;
    }

    public final static Calendar overrideCalendar(Calendar calendar, LocalDateTime date) {
        return overrideCalendar(calendar, date.toLocalDate(), date.toLocalTime());
    }

    public final static Calendar overrideCalendar(Calendar calendar, LocalDate date, LocalTime time) {
        return overrideCalendar(calendar,
            date.getYear(),
            date.getMonthValue(),
            date.getDayOfMonth(),
            time.getHour(),
            time.getMinute(),
            time.getSecond(),
            time.getNano() / 1000000);
    }
}
