package com.moon.core.time;

import com.moon.core.enums.Const;
import com.moon.core.lang.IntUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ParseSupportUtil;
import com.moon.core.util.function.TableIntConsumer;
import com.moon.core.util.function.TableIntFunction;
import com.moon.core.util.validator.ResidentID18Validator;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static java.time.LocalDateTime.ofInstant;

/**
 * JDK 8 time 工具类
 * <p>
 * 有些从方法定义对方法的作用一目了然的方法不再添加注释
 *
 * @author moonsky
 */
public final class DateTimeUtil {

    private DateTimeUtil() { noInstanceError(); }

    /**
     * 当前时间时间戳毫秒数
     *
     * @return 时间戳
     */
    public static long now() { return System.currentTimeMillis(); }

    /**
     * 获取当前日期
     *
     * @return 当前日期 LocalDate
     */
    public static LocalDate nowDate() { return LocalDate.now(); }

    /**
     * 获取当前时间
     *
     * @return 当前时间 LocalTime
     */
    public static LocalTime nowTime() { return LocalTime.now(); }

    /**
     * 获取当前时间
     *
     * @return 当前日期时间 LocalDateTime
     */
    public static LocalDateTime nowDateTime() { return LocalDateTime.now(); }

    /**
     * 默认当前时间，如果指定时间为 null 的话
     *
     * @param date 指定时间
     *
     * @return 如果指定时间为 null 就返回当前时间，否则返回字段时间
     */
    public static LocalTime nowIfNull(LocalTime date) { return (date == null ? nowTime() : date); }

    /**
     * 默认当前时间，如果指定时间为 null 的话
     *
     * @param date 指定时间
     *
     * @return 如果字段时间不为 null 就返回指定时间，否则返回当前时间
     */
    public static LocalDate nowIfNull(LocalDate date) { return (date == null ? nowDate() : date); }

    /**
     * 默认当前时间，如果指定时间为 null 的话
     *
     * @param date 指定时间
     *
     * @return 如果字段时间不为 null 就返回指定时间，否则返回当前时间
     */
    public static LocalDateTime nowIfNull(LocalDateTime date) { return (date == null ? nowDateTime() : date); }

    /**
     * date1、date2 是否是同一年份
     *
     * @param date1 date1
     * @param date2 date2
     *
     * @return date1、date2 是同一年份时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 date1 或 date2 为 null 时抛出异常
     */
    public static boolean isYearEquals(LocalDate date1, LocalDate date2) {
        return date1.getYear() == date2.getYear();
    }

    /**
     * date1 的年份是否在 date2 之前
     *
     * @param date1 date1
     * @param date2 date2
     *
     * @return 当 date1 的年份在 date2 的年份之前时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 date1 或 date2 为 null 时抛出异常
     */
    public static boolean isYearBefore(LocalDate date1, LocalDate date2) {
        return date1.getYear() < date2.getYear();
    }

    /**
     * date1 的年份是否在 date2 之后
     *
     * @param date1 date1
     * @param date2 date2
     *
     * @return 当 date1 的年份在 date2 的年份之后时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 date1 或 date2 为 null 时抛出异常
     */
    public static boolean isYearAfter(LocalDate date1, LocalDate date2) {
        return date1.getYear() > date2.getYear();
    }

    /**
     * date1、date2是否是同一月份
     *
     * @param date1 date1
     * @param date2 date2
     *
     * @return date1、date2是同一月份时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 date1 或 date2 为 null 时抛出异常
     */
    public static boolean isMonthEquals(LocalDate date1, LocalDate date2) {
        return (date1.getYear() == date2.getYear() && date1.getMonthValue() < date2.getMonthValue());
    }

    /**
     * date1 的月份是否在 date2 之前
     *
     * @param date1 date1
     * @param date2 date2
     *
     * @return 当 date1 的月份在 date2 的月份之前时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 date1 或 date2 为 null 时抛出异常
     */
    public static boolean isMonthBefore(LocalDate date1, LocalDate date2) {
        int year1 = date1.getYear(), year2 = date2.getYear();
        return year1 < year2 || (year1 == year2 && date1.getMonthValue() < date2.getMonthValue());
    }

    /**
     * date1 的月份是否在 date2 之后
     *
     * @param date1 date1
     * @param date2 date2
     *
     * @return 当 date1 的月份在 date2 的月份之后时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 date1 或 date2 为 null 时抛出异常
     */
    public static boolean isMonthAfter(LocalDate date1, LocalDate date2) {
        int year1 = date1.getYear(), year2 = date2.getYear();
        return year1 > year2 || (year1 == year2 && date1.getMonthValue() > date2.getMonthValue());
    }

    /**
     * date1、date2是否是同一日期
     *
     * @param date1 date1
     * @param date2 date2
     *
     * @return date1、date2是同一日期时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 date1 或 date2 为 null 时抛出异常
     */
    public static boolean isDateEquals(LocalDate date1, LocalDate date2) {
        return (date1.getYear() == date2.getYear() && date1.getDayOfYear() < date2.getDayOfYear());
    }

    /**
     * date1 的日期是否在 date2 之前
     *
     * @param date1 date1
     * @param date2 date2
     *
     * @return 当 date1 的日期在 date2 的日期之前时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 date1 或 date2 为 null 时抛出异常
     */
    public static boolean isDateBefore(LocalDate date1, LocalDate date2) {
        int year1 = date1.getYear(), year2 = date2.getYear();
        return year1 < year2 || (year1 == year2 && date1.getDayOfYear() < date2.getDayOfYear());
    }

    /**
     * date1 的日期是否在 date2 之后
     *
     * @param date1 date1
     * @param date2 date2
     *
     * @return 当 date1 的日期在 date2 的日期之后时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 date1 或 date2 为 null 时抛出异常
     */
    public static boolean isDateAfter(LocalDate date1, LocalDate date2) {
        int year1 = date1.getYear(), year2 = date2.getYear();
        return year1 > year2 || (year1 == year2 && date1.getDayOfYear() > date2.getDayOfYear());
    }

    /**
     * time1 的小时数是否与 time2 相同
     *
     * @param time1 time1
     * @param time2 time2
     *
     * @return 当 time1 的小时数是否与 time2 相同时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 time1 或 time2 为 null 时抛出异常
     */
    public static boolean isHourEquals(LocalTime time1, LocalTime time2) {
        return time1.getHour() == time2.getHour();
    }

    /**
     * datetime1 的小时数是否与 datetime 相同
     *
     * @param datetime1 datetime1
     * @param datetime2 datetime2
     *
     * @return 当 datetime1 的小时数是否与 datetime2 相同时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 datetime1 或 datetime2 为 null 时抛出异常
     */
    public static boolean isHourEquals(LocalDateTime datetime1, LocalDateTime datetime2) {
        return isDateEquals(datetime1.toLocalDate(), datetime2.toLocalDate())//
            && isHourEquals(datetime1.toLocalTime(), datetime2.toLocalTime());
    }

    /**
     * time1 的小时数是否在 time2 之前
     *
     * @param time1 time1
     * @param time2 time2
     *
     * @return 当 time1 的小时数是否在 time2 之前时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 time1 或 time2 为 null 时抛出异常
     */
    public static boolean isHourBefore(LocalTime time1, LocalTime time2) {
        return time1.getHour() < time2.getHour();
    }

    /**
     * datetime1 的小时数是否在 datetime2 之前
     *
     * @param datetime1 datetime1
     * @param datetime2 datetime2
     *
     * @return 当 datetime1 的小时数是否在 datetime2 之前时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 datetime1 或 datetime2 为 null 时抛出异常
     */
    public static boolean isHourBefore(LocalDateTime datetime1, LocalDateTime datetime2) {
        return isDateBefore(datetime1.toLocalDate(), datetime2.toLocalDate())//
            || (isDateEquals(datetime1.toLocalDate(), datetime2.toLocalDate())//
            && isHourBefore(datetime1.toLocalTime(), datetime2.toLocalTime()));
    }

    /**
     * time1 的小时数是否在 time2 之后
     *
     * @param time1 time1
     * @param time2 time2
     *
     * @return 当 time1 的小时数是否在 time2 之后时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 time1 或 time2 为 null 时抛出异常
     */
    public static boolean isHourAfter(LocalTime time1, LocalTime time2) {
        return time1.getHour() > time2.getHour();
    }

    /**
     * datetime1 的小时数是否在 datetime2 之后
     *
     * @param datetime1 datetime1
     * @param datetime2 datetime2
     *
     * @return 当 datetime1 的小时数是否在 datetime2 之后时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 datetime1 或 datetime2 为 null 时抛出异常
     */
    public static boolean isHourAfter(LocalDateTime datetime1, LocalDateTime datetime2) {
        return isDateAfter(datetime1.toLocalDate(), datetime2.toLocalDate())//
            || (isDateEquals(datetime1.toLocalDate(), datetime2.toLocalDate())//
            && isHourAfter(datetime1.toLocalTime(), datetime2.toLocalTime()));
    }

    /**
     * time1 的分钟数是否与 time2 相同
     *
     * @param time1 time1
     * @param time2 time2
     *
     * @return 当 time1 的小时数是否与 time2 相同时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 time1 或 time2 为 null 时抛出异常
     */
    public static boolean isMinuteEquals(LocalTime time1, LocalTime time2) {
        return (isHourEquals(time1, time2) && time1.getMinute() < time2.getMinute());
    }

    /**
     * datetime1 的分钟数是否与 datetime2 相同
     *
     * @param datetime1 datetime1
     * @param datetime2 datetime2
     *
     * @return 当 datetime1 的小时数是否与 datetime2 相同时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 datetime1 或 datetime2 为 null 时抛出异常
     */
    public static boolean isMinuteEquals(LocalDateTime datetime1, LocalDateTime datetime2) {
        return (isHourEquals(datetime1, datetime2) && datetime1.getMinute() == datetime2.getMinute());
    }

    /**
     * time1 的分钟数是否在 time2 之前
     *
     * @param time1 time1
     * @param time2 time2
     *
     * @return 当 time1 的分钟数是否在 time2 之前时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 time1 或 time2 为 null 时抛出异常
     */
    public static boolean isMinuteBefore(LocalTime time1, LocalTime time2) {
        int hour1 = time1.getHour(), hour2 = time2.getHour();
        return hour1 < hour2 || (hour1 == hour2 && time1.getMinute() < time2.getMinute());
    }

    /**
     * datetime1 的分钟数是否在 datetime2 之前
     *
     * @param datetime1 datetime1
     * @param datetime2 datetime2
     *
     * @return 当 datetime1 的分钟数是否在 datetime2 之前时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 datetime1 或 datetime2 为 null 时抛出异常
     */
    public static boolean isMinuteBefore(LocalDateTime datetime1, LocalDateTime datetime2) {
        return isHourBefore(datetime1, datetime2)//
            || (isHourEquals(datetime1, datetime2) && datetime1.getMinute() < datetime2.getMinute());
    }

    /**
     * time1 的分钟数是否在 time2 之后
     *
     * @param time1 time1
     * @param time2 time2
     *
     * @return 当 time1 的分钟数是否在 time2 之后时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 time1 或 time2 为 null 时抛出异常
     */
    public static boolean isMinuteAfter(LocalTime time1, LocalTime time2) {
        int hour1 = time1.getHour(), hour2 = time2.getHour();
        return hour1 > hour2 || (hour1 == hour2 && time1.getMinute() > time2.getMinute());
    }

    /**
     * datetime1 的分钟数是否在 datetime2 之后
     *
     * @param datetime1 datetime1
     * @param datetime2 datetime2
     *
     * @return 当 datetime1 的分钟数是否在 datetime2 之后时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 datetime1 或 datetime2 为 null 时抛出异常
     */
    public static boolean isMinuteAfter(LocalDateTime datetime1, LocalDateTime datetime2) {
        return isHourBefore(datetime1, datetime2)//
            || (isHourEquals(datetime1, datetime2) && datetime1.getMinute() > datetime2.getMinute());
    }

    /**
     * time1 的秒数是否与 time2 相同
     *
     * @param time1 time1
     * @param time2 time2
     *
     * @return 当 time1 的小时数是否与 time2 相同时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 time1 或 time2 为 null 时抛出异常
     */
    public static boolean isSecondEquals(LocalTime time1, LocalTime time2) {
        return (isMinuteEquals(time1, time2) && time1.getSecond() < time2.getSecond());
    }

    /**
     * datetime1 的秒数是否与 datetime2 相同
     *
     * @param datetime1 datetime1
     * @param datetime2 datetime2
     *
     * @return 当 datetime1 的小时数是否与 datetime2 相同时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 datetime1 或 datetime2 为 null 时抛出异常
     */
    public static boolean isSecondEquals(LocalDateTime datetime1, LocalDateTime datetime2) {
        return isMinuteEquals(datetime1, datetime2) && datetime1.getSecond() == datetime2.getSecond();
    }

    /**
     * time1 的秒数是否在 time2 之前
     *
     * @param time1 time1
     * @param time2 time2
     *
     * @return 当 time1 的秒数是否在 time2 之前时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 time1 或 time2 为 null 时抛出异常
     */
    @SuppressWarnings("all")
    public static boolean isSecondBefore(LocalTime time1, LocalTime time2) {
        int hour1 = time1.getHour(), hour2 = time2.getHour();
        if (hour1 < hour2) {
            return true;
        }
        if (hour1 > hour2) {
            return false;
        }
        int minute1 = time1.getMinute(), minute2 = time2.getMinute();
        return minute1 < minute2 || (minute1 == minute2 && time1.getSecond() < time2.getSecond());
    }

    /**
     * datetime1 的秒数是否在 datetime2 之前
     *
     * @param datetime1 datetime1
     * @param datetime2 datetime2
     *
     * @return 当 datetime1 的秒数是否在 datetime2 之前时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 datetime1 或 datetime2 为 null 时抛出异常
     */
    @SuppressWarnings("all")
    public static boolean isSecondBefore(LocalDateTime datetime1, LocalDateTime datetime2) {
        return isMinuteBefore(datetime1, datetime2)//
            || (isMinuteEquals(datetime1, datetime2) && datetime1.getSecond() < datetime2.getSecond());
    }

    /**
     * time1 的秒数是否在 time2 之后
     *
     * @param time1 time1
     * @param time2 time2
     *
     * @return 当 time1 的秒数是否在 time2 之后时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 time1 或 time2 为 null 时抛出异常
     */
    @SuppressWarnings("all")
    public static boolean isSecondAfter(LocalTime time1, LocalTime time2) {
        int hour1 = time1.getHour(), hour2 = time2.getHour();
        if (hour1 > hour2) {
            return true;
        }
        if (hour1 < hour2) {
            return false;
        }
        int minute1 = time1.getMinute(), minute2 = time2.getMinute();
        return minute1 > minute2 || (minute1 == minute2 && time1.getSecond() > time2.getSecond());
    }

    /**
     * datetime1 的秒数是否在 datetime2 之后
     *
     * @param datetime1 datetime1
     * @param datetime2 datetime2
     *
     * @return 当 datetime1 的秒数是否在 datetime2 之后时返回 true，否则返回 false
     *
     * @throws NullPointerException 当 datetime1 或 datetime2 为 null 时抛出异常
     */
    @SuppressWarnings("all")
    public static boolean isSecondAfter(LocalDateTime datetime1, LocalDateTime datetime2) {
        return isMinuteAfter(datetime1, datetime2)//
            || (isMinuteEquals(datetime1, datetime2) && datetime1.getSecond() > datetime2.getSecond());
    }

    public final static boolean isBeforeMonthDay(LocalDate date, LocalDate date2) {
        return isBeforeMonthDay(date, date2.getMonthValue(), date2.getDayOfMonth());
    }

    public final static boolean isBeforeMonthDay(LocalDate date, int month, int dayOfMonth) {
        int month1 = date.getMonthValue();
        return month1 > month || (month1 == month && date.getDayOfMonth() > dayOfMonth);
    }

    public final static boolean isAfterMonthDay(LocalDate date, int month, int dayOfMonth) {
        int month1 = date.getMonthValue();
        return month1 > month || (month1 == month && date.getDayOfMonth() > dayOfMonth);
    }

    /**
     * 获取一年的第几周
     *
     * @param date           日期
     * @param firstDayOfWeek 一周的第一天是星期几
     *
     * @return 返回指定日期是对应当前年的第几周
     *
     * @see LocalDateTime#toLocalDate() 转换 LocalDateTime 请先调用这个方法
     */
    public static int getWeekOfYear(LocalDate date, DayOfWeek firstDayOfWeek) {
        return date.get(WeekFields.of(firstDayOfWeek, 4).weekOfYear());
    }

    /**
     * 获取一年的第几周，设一周的第一天是星期一
     *
     * @param date 日期
     *
     * @return 返回指定日期是对应当前年的第几周
     *
     * @see LocalDateTime#toLocalDate() 转换 LocalDateTime 请先调用这个方法
     */
    public static int getWeekOfYearOnMonday(LocalDate date) {
        return getWeekOfYear(date, DayOfWeek.MONDAY);
    }

    /**
     * 获取一年的第几周，设一周的第一天是星期日
     *
     * @param date 日期
     *
     * @return 返回指定日期是对应当前年的第几周
     *
     * @see LocalDateTime#toLocalDate() 转换 LocalDateTime 请先调用这个方法
     */
    public static int getWeekOfYearOnSunday(LocalDate date) {
        return getWeekOfYear(date, DayOfWeek.SUNDAY);
    }

    /**
     * 获取一月的第几周
     *
     * @param date           日期
     * @param firstDayOfWeek 一周的第一天是星期几
     *
     * @return 返回指定日期是对应当前月份的第几周
     *
     * @see LocalDateTime#toLocalDate() 转换 LocalDateTime 请先调用这个方法
     */
    public static int getWeekOfMonth(LocalDate date, DayOfWeek firstDayOfWeek) {
        return date.get(WeekFields.of(firstDayOfWeek, 4).weekOfMonth());
    }

    /**
     * 获取一月的第几周，设一周的第一天是星期一
     *
     * @param date 日期
     *
     * @return 返回指定日期是对应当前月份的第几周
     *
     * @see LocalDateTime#toLocalDate() 转换 LocalDateTime 请先调用这个方法
     */
    public static int getWeekOfMonthOnMonday(LocalDate date) {
        return getWeekOfMonth(date, DayOfWeek.MONDAY);
    }

    /**
     * 获取一月的第几周，设一周的第一天是星期日
     *
     * @param date 日期
     *
     * @return 返回指定日期是对应当前月份的第几周
     *
     * @see LocalDateTime#toLocalDate() 转换 LocalDateTime 请先调用这个方法
     */
    public static int getWeekOfMonthOnSunday(LocalDate date) {
        return getWeekOfMonth(date, DayOfWeek.SUNDAY);
    }

    /**
     * 根据日期获取年龄（周岁）
     *
     * @param birthday 出生日期
     *
     * @return 周岁
     *
     * @see ResidentID18Validator#getAge()
     * @see LocalDateTime#toLocalDate() 不单独对 LocalDateTime 提供方法
     */
    public static int getAge(LocalDate birthday) { return getAge(birthday, LocalDate.now()); }

    /**
     * 返回生日到指定日期的周岁数
     *
     * @param birthday 出生日期
     * @param endDate  指定日期
     *
     * @return 周岁
     */
    public static int getAge(LocalDate birthday, LocalDate endDate) {
        int age = endDate.getYear() - birthday.getYear();
        return isBeforeMonthDay(birthday, endDate) ? age - 1 : age;
    }

    /**
     * 根据日期获取年龄（虚岁）
     *
     * @param date 日期
     *
     * @return 虚岁
     *
     * @see ResidentID18Validator#getNominalAge()
     * @see LocalDateTime#toLocalDate() 不单独对 LocalDateTime 提供方法
     */
    public static int getNominalAge(LocalDate date) { return getAge(date) + 1; }

    /*
     始末时间
     */

    /**
     * 年初：当年第 0 纳秒时刻
     *
     * @param date 指定日期
     *
     * @return 指定日期所在年份的最初时刻
     */
    public static LocalDate startOfYear(LocalDate date) { return toDate(IntUtil.toInts(date.getYear())); }

    /**
     * 年初：当年第 0 纳秒时刻
     *
     * @param datetime 指定日期时间
     *
     * @return 指定日期所在年份的最初时刻
     */
    public static LocalDateTime startOfYear(LocalDateTime datetime) {
        return toDateTime(IntUtil.toInts(datetime.getYear()));
    }

    /**
     * 月初：当月第 0 纳秒时刻
     *
     * @param date 指定日期
     *
     * @return 指定日期所在月份的最初时刻
     */
    public static LocalDate startOfMonth(LocalDate date) {
        return toDate(date.getYear(), date.getMonthValue());
    }

    /**
     * 月初：当月第 0 纳秒时刻
     *
     * @param datetime 指定日期
     *
     * @return 指定时间所在天的最初时刻
     */
    public static LocalDateTime startOfMonth(LocalDateTime datetime) {
        return toDateTime(datetime.getYear(), datetime.getMonthValue());
    }

    /**
     * 周初：当前日期所在周的第一天
     *
     * @param date           当前日期
     * @param firstDayOfWeek 星期几为一周的第一天
     *
     * @return 当前日期所在周的第一天
     */
    public static LocalDate startOfWeek(LocalDate date, DayOfWeek firstDayOfWeek) {
        int diffDays = date.getDayOfWeek().ordinal() - firstDayOfWeek.ordinal();
        return date.minusDays(diffDays < 0 ? diffDays + 7 : diffDays);
    }

    /**
     * 周初：当前日期所在周的第 0 秒时刻
     *
     * @param datetime       当前日期
     * @param firstDayOfWeek 星期几为一周的第一天
     *
     * @return 指定时间所在周的最初时刻
     */
    public static LocalDateTime startOfWeek(LocalDateTime datetime, DayOfWeek firstDayOfWeek) {
        int diffDays = datetime.getDayOfWeek().ordinal() - firstDayOfWeek.ordinal();
        return startOfDay(datetime.minusDays(diffDays < 0 ? diffDays + 7 : diffDays));
    }

    /**
     * 当天开始时间：当日第 0 纳秒时刻
     *
     * @param datetime 指定日期
     *
     * @return 指定时间所在天的最初时刻
     */
    public static LocalDateTime startOfDay(LocalDateTime datetime) {
        return toDateTime(datetime.getYear(), datetime.getMonth().getValue(), datetime.getDayOfMonth());
    }

    /**
     * 当前小时第 0 纳秒时刻
     *
     * @param time 指定日期
     *
     * @return 指定时间所在小时的最初时刻
     */
    public static LocalTime startOfHour(LocalTime time) {
        return toTime(IntUtil.toInts(time.getHour()));
    }

    /**
     * 当前小时第 0 纳秒时刻
     *
     * @param datetime 指定日期
     *
     * @return 指定时间所在小时的最初时刻
     */
    public static LocalDateTime startOfHour(LocalDateTime datetime) {
        return LocalDateTime.of(datetime.toLocalDate(), startOfHour(datetime.toLocalTime()));
    }

    /**
     * 当前分钟第 0 纳秒时刻
     *
     * @param datetime 指定日期
     *
     * @return 指定时间所在分钟的最初时刻
     */
    public static LocalDateTime startOfMinute(LocalDateTime datetime) {
        return LocalDateTime.of(datetime.toLocalDate(), startOfMinute(datetime.toLocalTime()));
    }

    /**
     * 当前分钟第 0 纳秒时刻
     *
     * @param time 指定时间
     *
     * @return 指定时间所在分钟的最初时刻
     */
    public static LocalTime startOfMinute(LocalTime time) {
        return toTime(time.getHour(), time.getMinute());
    }

    /**
     * 当前秒第 0 纳纳秒时刻
     *
     * @param time 指定时间
     *
     * @return 指定时间所在秒的最初时刻
     */
    public static LocalTime startOfSecond(LocalTime time) {
        return toTime(time.getHour(), time.getMinute(), time.getSecond());
    }

    /**
     * 当前秒第 0 纳纳秒时刻
     *
     * @param datetime 指定时间
     *
     * @return 指定时间所在秒的最初时刻
     */
    public static LocalDateTime startOfSecond(LocalDateTime datetime) {
        return LocalDateTime.of(datetime.toLocalDate(), startOfSecond(datetime.toLocalTime()));
    }

    /**
     * 年末：当前年最后一纳秒时刻
     *
     * @param date 指定日期
     *
     * @return 指定日期所在年份的最后时刻
     */
    public static LocalDate endOfYear(LocalDate date) {
        return toDate(IntUtil.toInts(date.getYear() + 1)).minusDays(1);
    }

    /**
     * 年末：当前年最后一纳秒时刻
     *
     * @param datetime 指定日期
     *
     * @return 指定日期所在年份的最后时刻
     */
    public static LocalDateTime endOfYear(LocalDateTime datetime) {
        return toDateTime(IntUtil.toInts(datetime.getYear() + 1)).minusNanos(1);
    }

    /**
     * 月末：当前月最后一纳秒时刻
     *
     * @param date 指定日期
     *
     * @return 指定日期所在月份的最后时刻
     */
    public static LocalDate endOfMonth(LocalDate date) {
        return toDate(date.getYear(), date.getMonth().getValue()).plusMonths(1).minusDays(1);
    }

    /**
     * 月末：当前月最后一纳秒时刻
     *
     * @param datetime 指定日期
     *
     * @return 指定日期所在月份的最后时刻
     */
    public static LocalDateTime endOfMonth(LocalDateTime datetime) {
        return toDateTime(datetime.getYear(), datetime.getMonth().getValue()).plusMonths(1).minusNanos(1);
    }

    /**
     * 周初：当前日期所在周的最后一天
     *
     * @param date           当前日期
     * @param firstDayOfWeek 星期几为一周的第一天
     *
     * @return 当前日期所在周的最后一天
     */
    public static LocalDate endOfWeek(LocalDate date, DayOfWeek firstDayOfWeek) {
        int diffDays = firstDayOfWeek.ordinal() - date.getDayOfWeek().ordinal();
        return date.plusDays(diffDays <= 0 ? diffDays + 6 : diffDays - 1);
    }

    /**
     * 周初：当前日期所在周的最后一纳秒时刻
     *
     * @param datetime       当前日期
     * @param firstDayOfWeek 星期几为一周的第一天
     *
     * @return 指定日期所在周的最后时刻
     */
    public static LocalDateTime endOfWeek(LocalDateTime datetime, DayOfWeek firstDayOfWeek) {
        int diffDays = firstDayOfWeek.ordinal() - datetime.getDayOfWeek().ordinal();
        return endOfDay(datetime.plusDays(diffDays <= 0 ? diffDays + 6 : diffDays - 1));
    }

    /**
     * 当日最后一纳秒时刻
     *
     * @param datetime 指定日期
     *
     * @return 指定日期所在天的最后时刻
     */
    public static LocalDateTime endOfDay(LocalDateTime datetime) {
        return toDateTime(datetime.getYear(), datetime.getMonth().getValue(), datetime.getDayOfMonth()).plusDays(1)
            .minusNanos(1);
    }

    /**
     * 当前小时最后一纳秒时刻
     *
     * @param time 指定时间
     *
     * @return 指定时间所在小时的最后时刻
     */
    public static LocalTime endOfHour(LocalTime time) {
        return toTime(IntUtil.toInts(time.getHour())).plusHours(1).minusNanos(1);
    }

    /**
     * 当前小时最后一纳秒时刻
     *
     * @param datetime 指定时间
     *
     * @return 指定时间所在小时的最后时刻
     */
    public static LocalDateTime endOfHour(LocalDateTime datetime) {
        return LocalDateTime.of(datetime.toLocalDate(), endOfHour(datetime.toLocalTime()));
    }

    /**
     * 当前分钟最后一纳秒时刻
     *
     * @param time 指定时间
     *
     * @return 指定时间所在分钟的最后时刻
     */
    public static LocalTime endOfMinute(LocalTime time) {
        return toTime(time.getHour(), time.getMinute()).plusMinutes(1).minusNanos(1);
    }

    /**
     * 当前分钟最后一纳秒时刻
     *
     * @param datetime 指定时间
     *
     * @return 指定时间所在分钟的最后时刻
     */
    public static LocalDateTime endOfMinute(LocalDateTime datetime) {
        return LocalDateTime.of(datetime.toLocalDate(), endOfMinute(datetime.toLocalTime()));
    }

    /**
     * 当前秒最后一纳秒时刻
     *
     * @param time 指定时间
     *
     * @return 指定时间所在秒的最后时刻
     */
    public static LocalTime endOfSecond(LocalTime time) {
        return toTime(time.getHour(), time.getMinute(), time.getSecond()).plusSeconds(1).minusNanos(1);
    }

    /**
     * 当前秒最后一纳秒时刻
     *
     * @param datetime 指定时间
     *
     * @return 指定时间所在秒的最后时刻
     */
    public static LocalDateTime endOfSecond(LocalDateTime datetime) {
        return LocalDateTime.of(datetime.toLocalDate(), endOfSecond(datetime.toLocalTime()));
    }

    /*
     * ----------------------------------------------------------------------------------
     * formats
     * ----------------------------------------------------------------------------------
     */

    /**
     * 格式化日期/时间
     *
     * @param accessor 日期/时间
     *
     * @return 格式化后的字符串
     */
    public static String format(TemporalAccessor accessor) { return format(accessor, Const.PATTERN); }

    /**
     * 格式化日期
     *
     * @param date 日期
     *
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date) { return format(date, Const.PATTERN_DATE); }

    /**
     * 格式化时间
     *
     * @param time 时间
     *
     * @return 格式化后的字符串
     */
    public static String format(LocalTime time) { return format(time, Const.PATTERN_TIME); }

    /**
     * 格式化日期/时间
     *
     * @param accessor 日期/时间
     * @param pattern  输出格式
     *
     * @return 格式化后的字符串
     */
    public static String format(TemporalAccessor accessor, String pattern) {
        return format(accessor, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 格式化日期/时间
     *
     * @param accessor  日期/时间
     * @param formatter 输出格式
     *
     * @return 格式化后的字符串
     */
    public static String format(TemporalAccessor accessor, DateTimeFormatter formatter) {
        return formatter.format(accessor);
    }

    /*
     * ----------------------------------------------------------------------------------
     * converters
     * ----------------------------------------------------------------------------------
     */

    public static LocalDateTime safeToDateTime(Instant instant) { return ofInstant(instant, ZoneId.systemDefault()); }

    public static LocalDateTime safeToDateTime(Date date) { return safeToDateTime(date.toInstant()); }

    public static LocalDateTime safeToDateTime(Calendar calendar) { return safeToDateTime(calendar.toInstant()); }

    public static Instant safeToInstant(Date date) { return date.toInstant(); }

    public static Instant safeToInstant(Calendar calendar) { return calendar.toInstant(); }

    public static Instant toInstant(long time) { return Instant.ofEpochMilli(time); }

    public static Instant toInstant(Date date) { return date == null ? null : date.toInstant(); }

    public static Instant toInstant(Calendar calendar) {
        return calendar == null ? null : calendar.toInstant();
    }

    public static LocalDate toDate(Calendar calendar) {
        return calendar == null ? null : safeToDateTime(calendar).toLocalDate();
    }

    public static LocalTime toTime(Calendar calendar) {
        return calendar == null ? null : safeToDateTime(calendar).toLocalTime();
    }

    public static LocalDateTime toDateTime(Calendar calendar) {
        return calendar == null ? null : safeToDateTime(calendar);
    }

    public static LocalDate toDate(Date date) {
        return date == null ? null : safeToDateTime(date).toLocalDate();
    }

    public static LocalTime toTime(Date date) {
        return date == null ? null : safeToDateTime(date).toLocalTime();
    }

    public static LocalDateTime toDateTime(Date date) {
        return date == null ? null : safeToDateTime(date);
    }

    public static LocalDateTime toDateTime(Instant instant) {
        return instant == null ? null : safeToDateTime(instant);
    }

    public static LocalDate toDate(CharSequence date) { return LocalDate.parse(date); }

    public static LocalTime toTime(CharSequence date) { return LocalTime.parse(date); }

    public static LocalDateTime toDateTime(CharSequence date) { return LocalDateTime.parse(date); }

    public static LocalDate toDate(long milliseconds) { return toDateTime(milliseconds).toLocalDate(); }

    public static LocalTime toTime(long milliseconds) { return toDateTime(milliseconds).toLocalTime(); }

    public static LocalDateTime toDateTime(long milliseconds) { return toDateTime(toInstant(milliseconds)); }

    public static LocalDate toDate(int... values) {
        int i = 0;
        switch (values.length) {
            case 0:
                return LocalDate.now();
            case 1:
                return LocalDate.of(values[i], 1, 1);
            case 2:
                return LocalDate.of(values[i++], values[i], 1);
            default:
                return LocalDate.of(values[i++], values[i++], values[i]);
        }
    }

    public static LocalTime toTime(int... values) {
        int i = 0;
        switch (values.length) {
            case 0:
                return LocalTime.now();
            case 1:
                return LocalTime.of(values[i], 0);
            case 2:
                return LocalTime.of(values[i++], values[i]);
            case 3:
                return LocalTime.of(values[i++], values[i++], values[i]);
            default:
                return LocalTime.of(values[i++], values[i++], values[i++], values[i]);
        }
    }

    @SuppressWarnings("all")
    public static LocalDateTime toDateTime(int... parts) {
        int[] vs = parts;
        int i = 0;
        switch (parts.length) {
            case 0:
                return LocalDateTime.now();
            case 1:
                return LocalDateTime.of(vs[i], 1, 1, 0, 0);
            case 2:
                return LocalDateTime.of(vs[i++], vs[i], 1, 0, 0);
            case 3:
                return LocalDateTime.of(vs[i++], vs[i++], vs[i], 0, 0);
            case 4:
                return LocalDateTime.of(vs[i++], vs[i++], vs[i++], vs[i], 0);
            case 5:
                return LocalDateTime.of(vs[i++], vs[i++], vs[i++], vs[i++], vs[i]);
            case 6:
                return LocalDateTime.of(vs[i++], vs[i++], vs[i++], vs[i++], vs[i++], vs[i]);
            default:
                return LocalDateTime.of(vs[i++], vs[i++], vs[i++], vs[i++], vs[i++], vs[i++], vs[i]);
        }
    }

    public static LocalDate toDate(Object obj) {
        return obj == null ? null : toDateTime(obj).toLocalDate();
    }

    public static LocalTime toTime(Object obj) {
        return obj == null ? null : toDateTime(obj).toLocalTime();
    }

    public static LocalDateTime toDateTime(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof LocalDateTime) {
            return (LocalDateTime) obj;
        }
        if (obj instanceof CharSequence) {
            return toDateTime((CharSequence) obj);
        }
        if (obj instanceof Number) {
            int maybeYear = ((Number) obj).intValue();
            if (maybeYear < 10000) {
                return toDateTime(new int[]{maybeYear});
            }
            return toDateTime(((Number) obj).longValue());
        }
        if (obj instanceof int[]) {
            return toDateTime((int[]) obj);
        }
        if (obj instanceof Date) {
            return toDateTime((Date) obj);
        }
        if (obj instanceof Calendar) {
            return toDateTime((Calendar) obj);
        }
        if (obj instanceof LocalDate) {
            LocalDate date = (LocalDate) obj;
            return toDateTime(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
        }
        if (obj instanceof LocalTime) {
            return LocalDateTime.of(LocalDate.now(), (LocalTime) obj);
        }
        try {
            return toDateTime(ParseSupportUtil.onlyOneItemOrSize(obj));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Can not cast to LocalDateTime of: %s", obj.toString()),
                e);
        }
    }

    /*
     * ----------------------------------------------------------------------------------
     * parse
     * ----------------------------------------------------------------------------------
     */

    public static LocalDateTime parseToDateTime(CharSequence dateString) {
        return dateString == null ? null : toDateTime(CalendarUtil.parseToCalendar(dateString.toString()));
    }

    public static LocalDate parseToDate(CharSequence dateString) {
        return dateString == null ? null : toDate(CalendarUtil.parseToCalendar(dateString.toString()));
    }

    public static LocalTime parseToTime(CharSequence dateString) {
        List<String> numerics = StringUtil.extractNumerics(dateString);
        return numerics.isEmpty() ? null : toTime(IntUtil.toInts(numerics, Integer::parseInt));
    }

    /*
     * ----------------------------------------------------------------------------------
     * reduce
     * ----------------------------------------------------------------------------------
     */

    /**
     * 聚合函数，从开始年份（含）至结束年份（不含），用年份比较，遍历处理每个年份数据
     * <p>
     * 如：
     * <pre>
     *     LocalDate begin = DateTimeUtil.toDate(2015);
     *     LocalDate end = DateTimeUtil.toDate(2019);
     *     List<String> values = DateTimeUtil.reduceYears(begin, end, (list, date, idx) -> {
     *         list.add(DateTimeUtil.format(date, "yyyy"));
     *         return list;
     *     }, new ArrayList());
     *     // values == {"2015","2016","2017","2018"}
     * </pre>
     *
     * @param begin      开始年份（含）
     * @param end        结束年份（不含）
     * @param reducer    聚合函数，接受三个参数：（总值，当前年份，索引），返回值作为下一次循环的总值或最终返回值
     * @param totalValue 聚合函数初始总值
     * @param <T>        返回值类型
     *
     * @return 执行完聚合函数后的总值
     */
    public static <T> T reduceYears(
        LocalDate begin, LocalDate end, TableIntFunction<? super T, LocalDate, T> reducer, T totalValue
    ) { return reduce(startOfYear(begin), startOfYear(end), b -> b.plusYears(1), reducer, totalValue); }

    /**
     * 聚合函数，从开始月份（含）至结束月份（不含），用月份比较，遍历处理每个月份数据
     * <p>
     * 如：
     * <pre>
     *     LocalDate begin = DateTimeUtil.toDate(2019, 02);
     *     LocalDate end = DateTimeUtil.toDate(2019, 05);
     *     List<String> values = DateTimeUtil.reduceMonths(begin, end, (list, date, idx) -> {
     *         list.add(DateTimeUtil.format(date, "yyyy-MM"));
     *         return list;
     *     }, new ArrayList());
     *     // values == {"2019-02","2019-03","2019-04"}
     * </pre>
     *
     * @param begin      开始月份（含）
     * @param end        结束月份（不含）
     * @param reducer    聚合函数，接受三个参数：（总值，当前月份，索引），返回值作为下一次循环的总值或最终返回值
     * @param totalValue 聚合函数初始总值
     * @param <T>        返回值类型
     *
     * @return 执行完聚合函数后的总值
     */
    public static <T> T reduceMonths(
        LocalDate begin, LocalDate end, TableIntFunction<? super T, LocalDate, T> reducer, T totalValue
    ) { return reduce(startOfMonth(begin), startOfMonth(end), b -> b.plusMonths(1), reducer, totalValue); }

    /**
     * 聚合函数，以周为单位逐步遍历增长处理
     *
     * @param begin      开始月份（含）
     * @param end        结束月份（不含）
     * @param reducer    聚合函数，接受三个参数：（总值，当前月份，索引），返回值作为下一次循环的总值或最终返回值
     * @param totalValue 聚合函数初始总值
     * @param <T>        返回值类型
     *
     * @return 执行完聚合函数后的总值
     */
    public static <T> T reduceWeeks(
        LocalDate begin, LocalDate end, TableIntFunction<? super T, LocalDate, T> reducer, T totalValue
    ) { return reduce(begin, end, b -> b.plusWeeks(1), reducer, totalValue); }

    /**
     * 聚合函数，从开始日（含）至结束日（不含），用日比较，遍历处理每个月份数据
     * <p>
     * 如：
     * <pre>
     *     LocalDate begin = DateTimeUtil.toDate(2019, 5, 1);
     *     LocalDate end = DateTimeUtil.toDate(2019, 5, 5);
     *     List<String> values = DateTimeUtil.reduceDays(begin, end, (list, date, idx) -> {
     *         list.add(DateTimeUtil.format(date, "yyyy-MM-dd"));
     *         return list;
     *     }, new ArrayList());
     *     // values == {"2019-05-01","2019-05-02","2019-05-03","2019-05-04"}
     * </pre>
     *
     * @param begin      开始日（含）
     * @param end        结束日（不含）
     * @param reducer    聚合函数，接受三个参数：（总值，当前月份，索引），返回值作为下一次循环的总值或最终返回值
     * @param totalValue 聚合函数初始总值
     * @param <T>        返回值类型
     *
     * @return 执行完聚合函数后的总值
     */
    public static <T> T reduceDays(
        LocalDate begin, LocalDate end, TableIntFunction<? super T, LocalDate, T> reducer, T totalValue
    ) { return reduce(begin, end, b -> b.plusDays(1), reducer, totalValue); }

    /**
     * 正序聚合函数：从前面的日期逐步增长到后面的日期；
     * <p>
     * 自定义增长粒度
     *
     * @param begin      开始日期
     * @param end        结束日期
     * @param reducer    聚合函数，接收三个参数：（总值，当前月份，索引），返回值作为下一次循环的总值或最终返回值
     * @param totalValue 聚合函数初始总值
     * @param addr       增长函数，用于自定义增长粒度
     * @param <T>        返回值类型
     *
     * @return 执行完聚合函数后的总值
     */
    public static <T> T reduce(
        LocalDate begin, LocalDate end, Function<LocalDate, LocalDate> addr,//
        TableIntFunction<? super T, LocalDate, T> reducer, T totalValue
    ) {
        for (int i = 0; begin.isBefore(end); begin = addr.apply(begin)) {
            totalValue = reducer.apply(totalValue, begin, i++);
        }
        return totalValue;
    }

    /**
     * 正序聚合函数：从前面的时间逐步增长到后面的时间；
     * <p>
     * 自定义增长粒度
     *
     * @param begin      开始时间
     * @param end        结束时间
     * @param reducer    聚合函数，接收三个参数：（总值，当前月份，索引），返回值作为下一次循环的总值或最终返回值
     * @param totalValue 聚合函数初始总值
     * @param addr       增长函数，用于自定义增长粒度
     * @param <T>        返回值类型
     *
     * @return 执行完聚合函数后的总值
     */
    public static <T> T reduce(
        LocalTime begin, LocalTime end, Function<LocalTime, LocalTime> addr,//
        TableIntFunction<? super T, LocalTime, T> reducer, T totalValue
    ) {
        for (int i = 0; begin.isBefore(end); begin = addr.apply(begin)) {
            totalValue = reducer.apply(totalValue, begin, i++);
        }
        return totalValue;
    }

    /**
     * 正序聚合函数：从前面的时间逐步增长到后面的时间；
     * <p>
     * 自定义增长粒度
     *
     * @param begin      开始时间
     * @param end        结束时间
     * @param reducer    聚合函数，接收三个参数：（总值，当前月份，索引），返回值作为下一次循环的总值或最终返回值
     * @param totalValue 聚合函数初始总值
     * @param addr       增长函数，用于自定义增长粒度
     * @param <T>        返回值类型
     *
     * @return 执行完聚合函数后的总值
     */
    public static <T> T reduce(
        LocalDateTime begin, LocalDateTime end, Function<LocalDateTime, LocalDateTime> addr,//
        TableIntFunction<? super T, LocalDateTime, T> reducer, T totalValue
    ) {
        for (int i = 0; begin.isBefore(end); begin = addr.apply(begin)) {
            totalValue = reducer.apply(totalValue, begin, i++);
        }
        return totalValue;
    }

    /**
     * 倒序聚合函数：从后面的日期逐步递减到前面的日期；
     * <p>
     * 自定义增长粒度
     *
     * @param begin      开始日期
     * @param end        结束日期
     * @param reducer    聚合函数，接收三个参数：（总值，当前月份，索引），返回值作为下一次循环的总值或最终返回值
     * @param totalValue 聚合函数初始总值
     * @param addr       递减函数，用于自定义递减粒度
     * @param <T>        返回值类型
     *
     * @return 执行完聚合函数后的总值
     */
    public static <T> T reduceReverse(
        LocalDate begin, LocalDate end, Function<LocalDate, LocalDate> addr,//
        TableIntConsumer<? super T, LocalDate> reducer, T totalValue
    ) {
        for (int i = 0; begin.isAfter(end); begin = addr.apply(begin)) {
            reducer.accept(totalValue, begin, i++);
        }
        return totalValue;
    }

    /**
     * 倒序聚合函数：从后面的时间逐步递减到前面的时间；
     * <p>
     * 自定义增长粒度
     *
     * @param begin      开始时间
     * @param end        结束时间
     * @param reducer    聚合函数，接收三个参数：（总值，当前月份，索引），返回值作为下一次循环的总值或最终返回值
     * @param totalValue 聚合函数初始总值
     * @param addr       递减函数，用于自定义递减粒度
     * @param <T>        返回值类型
     *
     * @return 执行完聚合函数后的总值
     */
    public static <T> T reduceReverse(
        LocalTime begin, LocalTime end, Function<LocalTime, LocalTime> addr,//
        TableIntConsumer<? super T, LocalTime> reducer, T totalValue
    ) {
        for (int i = 0; begin.isAfter(end); begin = addr.apply(begin)) {
            reducer.accept(totalValue, begin, i++);
        }
        return totalValue;
    }

    /**
     * 倒序聚合函数：从后面的时间逐步递减到前面的时间；
     * <p>
     * 自定义增长粒度
     *
     * @param begin      开始时间
     * @param end        结束时间
     * @param reducer    聚合函数，接收三个参数：（总值，当前月份，索引），返回值作为下一次循环的总值或最终返回值
     * @param totalValue 聚合函数初始总值
     * @param addr       递减函数，用于自定义递减粒度
     * @param <T>        返回值类型
     *
     * @return 执行完聚合函数后的总值
     */
    public static <T> T reduceReverse(
        LocalDateTime begin, LocalDateTime end, Function<LocalDateTime, LocalDateTime> addr,//
        TableIntConsumer<? super T, LocalDateTime> reducer, T totalValue
    ) {
        for (int i = 0; begin.isAfter(end); begin = addr.apply(begin)) {
            reducer.accept(totalValue, begin, i++);
        }
        return totalValue;
    }

    /*
     * ----------------------------------------------------------------------------------
     * for each
     * ----------------------------------------------------------------------------------
     */

    /**
     * 正序遍历：从前面的日期逐步增长到后面的时间；
     *
     * @param begin    开始日期
     * @param end      结束日期
     * @param addr     增长函数，自定义增长粒度
     * @param consumer 处理器
     */
    public static void forEach(
        LocalDate begin, LocalDate end, Function<LocalDate, LocalDate> addr, Consumer<LocalDate> consumer
    ) {
        for (; begin.isBefore(end); begin = addr.apply(begin)) {
            consumer.accept(begin);
        }
    }

    /**
     * 倒序遍历：从后面的时间逐步递减到前面的时间；
     *
     * @param begin    开始日期
     * @param end      结束日期
     * @param addr     增长函数，自定义增长粒度
     * @param consumer 处理器
     */
    public static void forEachReverse(
        LocalDate begin, LocalDate end, Function<LocalDate, LocalDate> addr, Consumer<LocalDate> consumer
    ) {
        for (; begin.isAfter(end); begin = addr.apply(begin)) {
            consumer.accept(begin);
        }
    }

    /**
     * 正序遍历：从前面的时间逐步增长到后面的时间；
     *
     * @param begin    开始时间
     * @param end      结束时间
     * @param addr     增长函数，自定义增长粒度
     * @param consumer 处理器
     */
    public static void forEach(
        LocalTime begin, LocalTime end, Function<LocalTime, LocalTime> addr, Consumer<LocalTime> consumer
    ) {
        for (; begin.isBefore(end); begin = addr.apply(begin)) {
            consumer.accept(begin);
        }
    }

    /**
     * 倒序遍历：从后面的时间逐步递减到前面的时间；
     *
     * @param begin    开始时间
     * @param end      结束时间
     * @param addr     增长函数，自定义增长粒度
     * @param consumer 处理器
     */
    public static void forEachReverse(
        LocalTime begin, LocalTime end, Function<LocalTime, LocalTime> addr, Consumer<LocalTime> consumer
    ) {
        for (; begin.isAfter(end); begin = addr.apply(begin)) {
            consumer.accept(begin);
        }
    }

    /**
     * 正序遍历：从前面的时间逐步增长到后面的时间；
     *
     * @param begin    开始时间
     * @param end      结束时间
     * @param addr     增长函数，自定义增长粒度
     * @param consumer 处理器
     */
    public static void forEach(
        LocalDateTime begin,
        LocalDateTime end,
        Function<LocalDateTime, LocalDateTime> addr,
        Consumer<LocalDateTime> consumer
    ) {
        for (; begin.isBefore(end); begin = addr.apply(begin)) {
            consumer.accept(begin);
        }
    }

    /**
     * 倒序遍历：从后面的时间逐步递减到前面的时间；
     *
     * @param begin    开始时间
     * @param end      结束时间
     * @param addr     增长函数，自定义增长粒度
     * @param consumer 处理器
     */
    public static void forEachReverse(
        LocalDateTime begin,
        LocalDateTime end,
        Function<LocalDateTime, LocalDateTime> addr,
        Consumer<LocalDateTime> consumer
    ) {
        for (; begin.isAfter(end); begin = addr.apply(begin)) {
            consumer.accept(begin);
        }
    }
}
