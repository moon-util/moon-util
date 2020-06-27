package com.moon.core.time;

import com.moon.core.lang.IntUtil;
import com.moon.core.lang.SupportUtil;
import com.moon.core.util.function.IntBiFunction;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static java.time.LocalDateTime.ofInstant;

/**
 * 有些从方法定义对方法的作用一目了然的方法不再添加注释
 *
 * @author benshaoye
 */
public final class DateTimeUtil {

    private DateTimeUtil() { noInstanceError(); }

    public static long now() { return System.currentTimeMillis(); }

    public static LocalDate nowDate() { return LocalDate.now(); }

    public static LocalTime nowTime() { return LocalTime.now(); }

    public static LocalDateTime nowDateTime() { return LocalDateTime.now(); }
    
    /*
     始末时间
     */

    /**
     * 年初：当年第 0 纳秒时刻
     *
     * @param date
     *
     * @return
     */
    public static LocalDate startingOfYear(LocalDate date) {
        return toDate(IntUtil.toInts(date.getYear()));
    }

    /**
     * 年初：当年第 0 纳秒时刻
     *
     * @param datetime
     *
     * @return
     */
    public static LocalDateTime startingOfYear(LocalDateTime datetime) {
        return toDateTime(IntUtil.toInts(datetime.getYear()));
    }

    /**
     * 月初：当月第 0 纳秒时刻
     *
     * @param date
     *
     * @return
     */
    public static LocalDate startingOfMonth(LocalDate date) {
        return toDate(date.getYear(), date.getMonth().getValue());
    }

    /**
     * 月初：当月第 0 纳秒时刻
     *
     * @param datetime
     *
     * @return
     */
    public static LocalDateTime startingOfMonth(LocalDateTime datetime) {
        return toDateTime(datetime.getYear(), datetime.getMonth().getValue());
    }

    /**
     * 当天开始时间：当日第 0 纳秒时刻
     *
     * @param datetime
     *
     * @return
     */
    public static LocalDateTime startingOfDay(LocalDateTime datetime) {
        return toDateTime(datetime.getYear(), datetime.getMonth().getValue(), datetime.getDayOfMonth());
    }

    /**
     * 当前小时第 0 纳秒时刻
     *
     * @param time
     *
     * @return
     */
    public static LocalTime startingOfHour(LocalTime time) {
        return toTime(IntUtil.toInts(time.getHour()));
    }

    /**
     * 当前小时第 0 纳秒时刻
     *
     * @param datetime
     *
     * @return
     */
    public static LocalDateTime startingOfHour(LocalDateTime datetime) {
        return LocalDateTime.of(datetime.toLocalDate(), startingOfHour(datetime.toLocalTime()));
    }

    /**
     * 当前分钟第 0 纳秒时刻
     *
     * @param datetime
     *
     * @return
     */
    public static LocalDateTime startingOfMinute(LocalDateTime datetime) {
        return LocalDateTime.of(datetime.toLocalDate(), startingOfMinute(datetime.toLocalTime()));
    }

    /**
     * 当前分钟第 0 纳秒时刻
     *
     * @param time
     *
     * @return
     */
    public static LocalTime startingOfMinute(LocalTime time) {
        return toTime(time.getHour(), time.getMinute());
    }

    /**
     * 当前秒第 0 纳纳秒时刻
     *
     * @param time
     *
     * @return
     */
    public static LocalTime startingOfSecond(LocalTime time) {
        return toTime(time.getHour(), time.getMinute(), time.getSecond());
    }

    /**
     * 当前秒第 0 纳纳秒时刻
     *
     * @param datetime
     *
     * @return
     */
    public static LocalDateTime startingOfSecond(LocalDateTime datetime) {
        return LocalDateTime.of(datetime.toLocalDate(), startingOfSecond(datetime.toLocalTime()));
    }

    /**
     * 年末：当前年最后一纳秒时刻
     *
     * @param date
     *
     * @return
     */
    public static LocalDate endingOfYear(LocalDate date) {
        return toDate(IntUtil.toInts(date.getYear() + 1)).minusDays(1);
    }

    /**
     * 年末：当前年最后一纳秒时刻
     *
     * @param datetime
     *
     * @return
     */
    public static LocalDateTime endingOfYear(LocalDateTime datetime) {
        return toDateTime(IntUtil.toInts(datetime.getYear() + 1)).minusNanos(1);
    }

    /**
     * 月末：当前月最后一纳秒时刻
     *
     * @param date
     *
     * @return
     */
    public static LocalDate endingOfMonth(LocalDate date) {
        return toDate(date.getYear(), date.getMonth().getValue()).plusMonths(1).minusDays(1);
    }

    /**
     * 月末：当前月最后一纳秒时刻
     *
     * @param datetime
     *
     * @return
     */
    public static LocalDateTime endingOfMonth(LocalDateTime datetime) {
        return toDateTime(datetime.getYear(), datetime.getMonth().getValue()).plusMonths(1).minusNanos(1);
    }

    /**
     * 当日最后一纳秒时刻
     *
     * @param datetime
     *
     * @return
     */
    public static LocalDateTime endingOfDay(LocalDateTime datetime) {
        return toDateTime(datetime.getYear(), datetime.getMonth().getValue(), datetime.getDayOfMonth()).plusDays(1)
            .minusNanos(1);
    }

    /**
     * 当前小时最后一纳秒时刻
     *
     * @param time
     *
     * @return
     */
    public static LocalTime endingOfHour(LocalTime time) {
        return toTime(IntUtil.toInts(time.getHour())).plusHours(1).minusNanos(1);
    }

    /**
     * 当前小时最后一纳秒时刻
     *
     * @param datetime
     *
     * @return
     */
    public static LocalDateTime endingOfHour(LocalDateTime datetime) {
        return LocalDateTime.of(datetime.toLocalDate(), endingOfHour(datetime.toLocalTime()));
    }

    /**
     * 当前分钟最后一纳秒时刻
     *
     * @param time
     *
     * @return
     */
    public static LocalTime endingOfMinute(LocalTime time) {
        return toTime(time.getHour(), time.getMinute()).plusMinutes(1).minusNanos(1);
    }

    /**
     * 当前分钟最后一纳秒时刻
     *
     * @param datetime
     *
     * @return
     */
    public static LocalDateTime endingOfMinute(LocalDateTime datetime) {
        return LocalDateTime.of(datetime.toLocalDate(), endingOfMinute(datetime.toLocalTime()));
    }

    /**
     * 当前秒最后一纳秒时刻
     *
     * @param time
     *
     * @return
     */
    public static LocalTime endingOfSecond(LocalTime time) {
        return toTime(time.getHour(), time.getMinute(), time.getSecond()).plusSeconds(1).minusNanos(1);
    }

    /**
     * 当前秒最后一纳秒时刻
     *
     * @param datetime
     *
     * @return
     */
    public static LocalDateTime endingOfSecond(LocalDateTime datetime) {
        return LocalDateTime.of(datetime.toLocalDate(), endingOfSecond(datetime.toLocalTime()));
    }

    /*
     * ----------------------------------------------------------------------------------
     * formats
     * ----------------------------------------------------------------------------------
     */

    /**
     * 格式化日期，
     * @param accessor
     * @param pattern
     * @return
     */
    public static String format(TemporalAccessor accessor, String pattern) {
        return format(accessor, DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(TemporalAccessor accessor, DateTimeFormatter formatter) {
        return formatter.format(accessor);
    }

    /*
     * ----------------------------------------------------------------------------------
     * converters
     * ----------------------------------------------------------------------------------
     */

    public static LocalDateTime safeToDateTime(Instant instant) {
        return ofInstant(instant, ZoneId.systemDefault());
    }

    public static LocalDateTime safeToDateTime(Date date) {
        return safeToDateTime(date.toInstant());
    }

    public static LocalDateTime safeToDateTime(Calendar calendar) {
        return safeToDateTime(calendar.toInstant());
    }

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
        if (obj instanceof CharSequence) {
            return toDateTime((CharSequence) obj);
        }
        if (obj instanceof Number) {
            if (obj instanceof Long || obj instanceof Double) {
                return toDateTime(((Number) obj).longValue());
            }
            return toDateTime(new int[]{(Integer) obj});
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
        try {
            return toDateTime(SupportUtil.onlyOneItemOrSize(obj));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Can not cast to LocalDateTime of: %s", obj.toString()),
                e);
        }
    }

    /*
     * ----------------------------------------------------------------------------------
     * for each
     * ----------------------------------------------------------------------------------
     */

    public static void forEachYears(LocalDate begin, LocalDate end, IntBiFunction<LocalDate, Boolean> consumer) {
        for (; begin.isBefore(end) && consumer.apply(begin.getYear(), begin); begin = begin.plusYears(1)) {
        }
    }

    public static void forEachMonths(LocalDate begin, LocalDate end, IntBiFunction<LocalDate, Boolean> consumer) {
        for (; begin.isBefore(end) && consumer.apply(begin.getMonthValue(), begin); begin = begin.plusMonths(1)) {
        }
    }

    public static void forEachDays(LocalDate begin, LocalDate end, IntBiFunction<LocalDate, Boolean> consumer) {
        for (; begin.isBefore(end) && consumer.apply(begin.getDayOfMonth(), begin); begin = begin.plusDays(1)) {
        }
    }

    public static void forEachHours(LocalTime begin, LocalTime end, IntBiFunction<LocalTime, Boolean> consumer) {
        LocalDate now = LocalDate.now();
        LocalDateTime last = begin.isBefore(end) ? LocalDateTime.of(now, end) : LocalDateTime.of(now.plusDays(1), end);
        for (LocalDateTime start = LocalDateTime.of(now, begin);
            start.isBefore(last) && consumer.apply(start.getHour(), start.toLocalTime());
            start = start.plusHours(1)) {
        }
    }

    public static void forEachMinutes(LocalTime begin, LocalTime end, IntBiFunction<LocalTime, Boolean> consumer) {
        LocalDate now = LocalDate.now();
        LocalDateTime last = begin.isBefore(end) ? LocalDateTime.of(now, end) : LocalDateTime.of(now.plusDays(1), end);
        for (LocalDateTime start = LocalDateTime.of(now, begin);
            start.isBefore(last) && consumer.apply(start.getMinute(), start.toLocalTime());
            start = start.plusMinutes(1)) {
        }
    }

    public static void forEachSeconds(LocalTime begin, LocalTime end, IntBiFunction<LocalTime, Boolean> consumer) {
        LocalDate now = LocalDate.now();
        LocalDateTime last = begin.isBefore(end) ? LocalDateTime.of(now, end) : LocalDateTime.of(now.plusDays(1), end);
        for (LocalDateTime start = LocalDateTime.of(now, begin);
            start.isBefore(last) && consumer.apply(start.getSecond(), start.toLocalTime());
            start = start.plusSeconds(1)) {
        }
    }
}
