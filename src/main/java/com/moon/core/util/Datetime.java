package com.moon.core.util;

import com.moon.core.lang.IntUtil;

import java.sql.Timestamp;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static com.moon.core.util.DatetimeField.*;

/**
 * 写这个的时候在想很多判断是否有必要每种日期数据类型都有必要写出来？
 * 想想还是觉得有必要，这里不写很多时候实际业务中还是会单独写！所有这里会有很多{@code is}开头的方法
 *
 * 命名为{@code Datetime}小写“t”为了不与{@code org.joda.time.DateTime}冲突
 *
 * @author moonsky
 */
public final class Datetime extends Date {

    /**
     * 纳秒
     */
    public final static int MAX_NANOSECOND = 999999999;
    public final static int MIN_NANOSECOND = 0;
    /**
     * 毫秒
     */
    public final static int MAX_MILLISECOND = 999;
    public final static int MIN_MILLISECOND = 0;
    /**
     * 闰秒
     */
    public final static int MAX_LEAP_SECOND = 60;
    /**
     * 秒
     */
    public final static int MAX_SECOND = 59;
    public final static int MIN_SECOND = 0;
    /**
     * 分钟
     */
    public final static int MAX_MINUTE = 59;
    public final static int MIN_MINUTE = 0;
    /**
     * 小时
     */
    public final static int MAX_HOUR = 23;
    public final static int MIN_HOUR = 0;
    /**
     * 日期 31
     */
    public final static int MAX_DAY_31 = 31;
    /**
     * 日期 30
     */
    public final static int MAX_DAY_30 = 30;
    /**
     * 日期 29
     */
    public final static int MAX_DAY_29 = 29;
    /**
     * 日期 28
     */
    public final static int MAX_DAY_28 = 28;
    public final static int MIN_DAY = 1;
    /**
     * 月份
     */
    public final static int MAX_MONTH = 12;
    public final static int MIN_MONTH = 1;

    private volatile Calendar calendar;

    private final boolean immutable;

    private Datetime obtainReturning(Datetime datetime) {
        return immutable ? new Datetime(datetime) : this;
    }

    private Datetime(Calendar calendar, boolean immutable) {
        super(calendar.getTimeInMillis());
        this.calendar = calendar;
        this.immutable = immutable;
    }

    public Datetime(long timeMillis) { this(DateUtil.toCalendar(timeMillis)); }

    public Datetime(Calendar calendar) { this(calendar, false); }

    public Datetime(String date) { this(DateUtil.toCalendar(date)); }

    public Datetime(CharSequence date) { this(DateUtil.toCalendar(date)); }

    public Datetime(Date date) { this((date == null ? new Date() : date).getTime()); }

    public Datetime(LocalDate localDate) { this(DateUtil.toCalendar(localDate)); }

    public Datetime(LocalDateTime datetime) { this(DateUtil.toCalendar(datetime)); }

    public Datetime(Datetime datetime) { this(datetime.calendar, datetime.immutable); }

    public Datetime(Instant instant) { this(instant.toEpochMilli()); }

    public Datetime(org.joda.time.DateTime datetime) { this(datetime.toDate()); }

    public Datetime(org.joda.time.LocalDate date) { this(date.toDate()); }

    public Datetime(org.joda.time.LocalDateTime datetime) { this(datetime.toDate()); }

    public Datetime() { this(new Date()); }

    public static Datetime now() { return new Datetime(); }

    public static Datetime of() { return now(); }

    public static Datetime of(long timeOfMillis) { return new Datetime(timeOfMillis); }

    public static Datetime of(CharSequence dateStr) { return new Datetime(dateStr); }

    public static Datetime of(int... fields) { return new Datetime(DateUtil.toCalendar(fields)); }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * ~ 构造器结束 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /*
     * *********************************************************************
     * getter & setter
     * *********************************************************************
     */

    /**
     * 是否是不可变对象
     *
     * @return 不可变对象每次返回的是新对象，否则操作和返回的是新对象
     */
    private boolean isImmutable() { return immutable; }

    /**
     * 设置为不可变数据，不可变数据每次操作均返回新对象
     *
     * @see #obtainReturning(Datetime)
     */
    private Datetime withImmutable() { return new Datetime(this.calendar, true); }

    /**
     * 设置为可变数据，可变数据操作的是同一个对象
     *
     * @see #obtainReturning(Datetime)
     */
    private Datetime withMutable() { return new Datetime(this.calendar, false); }

    /*
     * *********************************************************************
     * 操作器
     * *********************************************************************
     */

    /**
     * 返回当前年有多少天
     *
     * @return 闰年 366 天，其他年份 365 天
     */
    public int getYearLength() { return isLeapYear() ? 366 : 365; }

    /**
     * 返回当前年份
     *
     * @return 年份
     */
    public int getYearValue() { return getField(YEAR); }

    /**
     * 返回季度
     *
     * @return 季度
     */
    public int getQuarterValue() { return getMonthValue() / 3 + 1; }

    /**
     * 返回当前月份
     *
     * @return 月份
     */
    public int getMonthValue() { return getField(MONTH); }

    /**
     * 返回当前是一年中的第 N 个星期
     *
     * @return N
     */
    public int getWeekOfYear() { return getField(DatetimeField.WEEK_OF_YEAR); }

    /**
     * 返回当前日期是当前月中的第 N 个星期
     *
     * @return N
     */
    public int getWeekOfMonth() { return getField(DatetimeField.WEEK_OF_MONTH); }

    /**
     * 返回当前是一年中的第 N 天
     *
     * @return N
     */
    public int getDayOfYear() { return getField(DatetimeField.DAY_OF_YEAR); }

    /**
     * 返回当前日期是当前月中的第 N 天
     *
     * @return N
     */
    public int getDayOfMonth() { return getField(DAY_OF_MONTH); }

    /**
     * 返回当前日期是当前周中的第 N 天
     *
     * @return N
     */
    public int getDayOfWeek() { return getField(DatetimeField.DAY_OF_WEEK); }

    /**
     * 返回 24 小时制的小时数
     *
     * @return 小时数
     */
    public int getHourOfDay() { return getField(HOUR_OF_DAY); }

    /**
     * 返回当前时间是“今天”的第 N 分钟
     *
     * @return N
     */
    public int getMinuteOfDay() { return getHourOfDay() * 60 + getMinuteOfHour(); }

    /**
     * 返回当前时间的分钟数
     *
     * @return 分钟数
     */
    public int getMinuteOfHour() { return getField(MINUTE); }

    /**
     * 返回当前时间是“今天”的第 N 秒
     *
     * @return N
     */
    public int getSecondOfDay() { return getHourOfDay() * 3600 + getSecondOfHour(); }

    /**
     * 返回当前时间在当前小时中的秒数
     *
     * @return 当前小时的第 N 秒
     */
    public int getSecondOfHour() { return getMinuteOfHour() * 60 + getSecondOfMinute(); }

    /**
     * 秒数
     *
     * @return 秒数
     */
    public int getSecondOfMinute() { return getField(SECOND); }

    /**
     * 当前毫秒数是今天的第 N 毫秒
     *
     * @return N
     */
    public int getMillisOfDay() { return getSecondOfDay() * 1000 + getMillisOfSecond(); }

    /**
     * 当前毫秒数是在当前小时的第 N 毫秒
     *
     * @return N
     */
    public int getMillisOfHour() { return getSecondOfHour() * 1000 + getMillisOfSecond(); }

    /**
     * 当前毫秒数是在当前分钟的第 N 毫秒
     *
     * @return N
     */
    public int getMillisOfMinute() { return getSecondOfMinute() * 1000 + getMillisOfSecond(); }

    /**
     * 毫秒数
     *
     * @return 毫秒数
     */
    public int getMillisOfSecond() { return getField(MILLISECOND); }

    /**
     * 获取纳秒数
     *
     * @return 当前时间的纳秒字段值(在当前秒内)
     */
    public int getNanoOfSecond() { return getMillisOfSecond() * 1000000; }

    /**
     * 当前时间戳毫秒数
     *
     * @return 时间戳
     */
    @Override
    public long getTime() { return obtainCalendar().getTimeInMillis(); }

    /**
     * 获取指定字段的值
     *
     * @param field 字段
     *
     * @return 字段值
     */
    public int getField(DatetimeField field) { return getField(field.value); }

    /**
     * 获取指定字段值
     *
     * @param field 字段
     *
     * @return 字段值
     */
    public int getField(int field) { return obtainCalendar().get(field); }

    /*
     * ****************************************************************************
     * * 设置日期数据 ***************************************************************
     * ****************************************************************************
     */

    /**
     * 设置秒数
     *
     * @param value 秒数
     *
     * @return 当前对象
     */
    public Datetime withSecond(int value) { return withField(SECOND, value); }

    /**
     * 设置分钟数
     *
     * @param value 分钟数
     *
     * @return 当前对象
     */
    public Datetime withMinute(int value) { return withField(MINUTE, value); }

    /**
     * 设置小时数（12 小时制）
     *
     * @param value 小时数
     *
     * @return 当前对象
     */
    public Datetime withHour(int value) { return withField(DatetimeField.HOUR, value); }

    /**
     * 设置小时数（24 小时制）
     *
     * @param value 小时数
     *
     * @return 当前对象
     */
    public Datetime withHourOfDay(int value) { return withField(HOUR_OF_DAY, value); }

    /**
     * 设置当前周中的星期
     *
     * @param value 星期
     *
     * @return 当前对象
     */
    public Datetime withDayOfWeek(int value) { return withField(DatetimeField.DAY_OF_WEEK, value); }

    /**
     * 设置当月的第几天
     *
     * @param value 日期
     *
     * @return 当前对象
     */
    public Datetime withDayOfMonth(int value) { return withField(DAY_OF_MONTH, value); }

    /**
     * 设置当前年的第几天
     *
     * @param value 天数
     *
     * @return 当前对象
     */
    public Datetime withDayOfYear(int value) { return withField(DatetimeField.DAY_OF_YEAR, value); }

    /**
     * 设置当前月份
     *
     * @param value 月份
     *
     * @return 当前对象
     */
    public Datetime withMonth(int value) { return withField(MONTH, value); }

    /**
     * 设置年份
     *
     * @param value 年份
     *
     * @return 当前对象
     */
    public Datetime withYear(int value) { return withField(YEAR, value); }

    /**
     * 设置指定字段值
     *
     * @param field 字段
     * @param value 字段值
     *
     * @return 当前对象
     */
    public Datetime withField(DatetimeField field, int value) { return withField(field.value, value); }

    /**
     * 设置指定字段值
     *
     * @param field 字段
     * @param value 字段值
     *
     * @return 当前对象
     */
    public Datetime withField(int field, int value) {
        obtainCalendar().set(field, field == Calendar.MONTH ? value - 1 : value);
        return obtainReturning(this);
    }

    /*
     * ****************************************************************************
     * * 日期加法操作 ***************************************************************
     * ****************************************************************************
     */

    public Datetime plusYears(int amount) { return plusField(YEAR, amount); }

    public Datetime plusMonths(int amount) { return plusField(MONTH, amount); }

    public Datetime plusWeeks(int amount) { return plusField(DAY_OF_MONTH, amount); }

    public Datetime plusDays(int amount) { return plusField(DAY_OF_MONTH, amount); }

    public Datetime plusHours(int amount) { return plusField(HOUR_OF_DAY, amount); }

    public Datetime plusMinutes(int amount) { return plusField(MINUTE, amount); }

    public Datetime plusSeconds(int amount) { return plusField(SECOND, amount); }

    public Datetime plusMillis(int amount) { return plusField(MILLISECOND, amount); }

    public Datetime plusField(DatetimeField field, int amount) {
        plusField(field.value, amount);
        return this;
    }

    public Datetime plusField(int field, int amount) {
        obtainCalendar().add(field, amount);
        return obtainReturning(this);
    }

    /*
     * ****************************************************************************
     * * 日期减法操作 ***************************************************************
     * ****************************************************************************
     */

    public Datetime minusYears(int amount) { return minusField(YEAR, amount); }

    public Datetime minusMonths(int amount) { return minusField(MONTH, amount); }

    public Datetime minusWeeks(int amount) { return minusField(DAY_OF_MONTH, amount); }

    public Datetime minusDays(int amount) { return minusField(DAY_OF_MONTH, amount); }

    public Datetime minusHours(int amount) { return minusField(HOUR_OF_DAY, amount); }

    public Datetime minusMinutes(int amount) { return minusField(MINUTE, amount); }

    public Datetime minusSeconds(int amount) { return minusField(SECOND, amount); }

    public Datetime minusMillis(int amount) { return minusField(MILLISECOND, amount); }

    public Datetime minusField(DatetimeField field, int amount) {
        minusField(field.value, amount);
        return this;
    }

    public Datetime minusField(int field, int amount) {
        obtainCalendar().add(field, -amount);
        return obtainReturning(this);
    }

    private Calendar obtainCalendar() { return calendar; }

    /*
     * ****************************************************************************
     * * 判断器 ********************************************************************
     * ****************************************************************************
     */

    /**
     * 是否是上午
     *
     * @return 24 小时制 12 点以前，返回 true，否则返回false
     */
    public boolean isAm() { return obtainCalendar().get(Calendar.AM_PM) == 0; }

    /**
     * 是否是下午
     *
     * @return 24 小时制 12 点以后，返回 true，否则返回false
     */
    public boolean isPm() { return obtainCalendar().get(Calendar.AM_PM) == 1; }

    /**
     * 是否是工作日
     *
     * @return 周一至周五返回 true，周六或周天返回 false
     */
    public boolean isWeekday() { return !isWeekend(); }

    /**
     * 是否是周末
     *
     * @return 周六、周日返回 true，周一至周五返回 false
     */
    public boolean isWeekend() {
        final int dayOfWeek = getDayOfWeek();
        return dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY;
    }

    /**
     * 是否是闰年
     *
     * @return 如果是闰年返回 true，否则 false
     */
    public boolean isLeapYear() { return Year.isLeap(getYearValue()); }

    public boolean isYearOf(int year) { return getYearValue() == year; }

    public boolean isYearBefore(int year) { return getYearValue() < year; }

    public boolean isYearAfter(int year) { return getYearValue() > year; }

    public boolean isYearOf(Datetime date) { return date != null && isYearOf(date.getYearValue()); }

    public boolean isYearBefore(Datetime date) { return date != null && isYearBefore(date.getYearValue()); }

    public boolean isYearAfter(Datetime date) { return date != null && isYearAfter(date.getYearValue()); }

    public boolean isMonthOf(int year, int month) { return isYearOf(year) && getMonthValue() == month; }

    public boolean isMonthBefore(int year, int month) {
        return isYearBefore(year) || (isYearOf(year) && getMonthValue() < month);
    }

    public boolean isMonthAfter(int year, int month) {
        return isYearAfter(year) || (isYearOf(year) && getMonthValue() > month);
    }

    public boolean isMonthOf(Datetime date) {
        return date != null && isMonthOf(date.getYearValue(), date.getMonthValue());
    }

    public boolean isMonthBefore(Datetime date) {
        return date != null && isMonthBefore(date.getYearValue(), date.getMonthValue());
    }

    public boolean isMonthAfter(Datetime date) {
        return date != null && isMonthAfter(date.getYearValue(), date.getMonthValue());
    }

    public boolean isDateOf(int year, int month, int dayOfMonth) {
        return isMonthOf(year, month) && getDayOfMonth() == dayOfMonth;
    }

    public boolean isDateBefore(int year, int month, int dayOfMonth) {
        return isMonthBefore(year, month) || (isMonthOf(year, month) && getDayOfMonth() < dayOfMonth);
    }

    public boolean isDateAfter(int year, int month, int dayOfMonth) {
        return isMonthAfter(year, month) || (isMonthOf(year, month) && getDayOfMonth() > dayOfMonth);
    }

    public boolean isDateOf(Datetime date) {
        return date != null && isDateOf(date.getYearValue(), date.getMonthValue(), date.getDayOfMonth());
    }

    public boolean isDateBefore(Datetime date) {
        return date != null && isDateBefore(date.getYearValue(), date.getMonthValue(), date.getDayOfMonth());
    }

    public boolean isDateAfter(Datetime date) {
        return date != null && isDateAfter(date.getYearValue(), date.getMonthValue(), date.getDayOfMonth());
    }

    public boolean isHourOf(int hour) { return getHourOfDay() == hour; }

    public boolean isHourBefore(int hour) { return getHourOfDay() < hour; }

    public boolean isHourAfter(int hour) { return getHourOfDay() > hour; }

    public boolean isHourOf(Datetime hour) { return hour != null && isHourOf(hour.getHourOfDay()); }

    public boolean isHourBefore(Datetime hour) { return hour != null && isHourBefore(hour.getHourOfDay()); }

    public boolean isHourAfter(Datetime hour) { return hour != null && isHourAfter(hour.getHourOfDay()); }

    public boolean isMinuteOf(int hour, int minute) { return isHourOf(hour) && getMinuteOfHour() == minute; }

    public boolean isMinuteBefore(int hour, int minute) {
        return isHourBefore(hour) || (isHourOf(hour) && getMinuteOfHour() < minute);
    }

    public boolean isMinuteAfter(int hour, int minute) {
        return isHourAfter(hour) || (isHourOf(hour) && getMinuteOfHour() > minute);
    }

    public boolean isMinuteOf(Datetime datetime) {
        return datetime != null && isMinuteOf(datetime.getHourOfDay(), datetime.getMinuteOfHour());
    }

    public boolean isMinuteBefore(Datetime datetime) {
        return datetime != null && isMinuteBefore(datetime.getHourOfDay(), datetime.getMinuteOfHour());
    }

    public boolean isMinuteAfter(Datetime datetime) {
        return datetime != null && isMinuteAfter(datetime.getHourOfDay(), datetime.getMinuteOfHour());
    }

    public boolean isSecondOf(int hour, int minute, int second) {
        return isMinuteOf(hour, minute) && getSecondOfMinute() == second;
    }

    public boolean isSecondBefore(int hour, int minute, int second) {
        return isMinuteBefore(hour, minute) || (isMinuteOf(hour, minute) && getSecondOfMinute() < second);
    }

    public boolean isSecondAfter(int hour, int minute, int second) {
        return isMinuteAfter(hour, minute) || (isMinuteOf(hour, minute) && getSecondOfMinute() > second);
    }

    public boolean isSecondOf(Datetime time) {
        return time != null && isSecondOf(time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute());
    }

    public boolean isSecondBefore(Datetime time) {
        return time != null && isSecondBefore(time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute());
    }

    public boolean isSecondAfter(Datetime time) {
        return time != null && isSecondAfter(time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute());
    }

    private final static DatetimeField[] FIELDS = {
        YEAR, MONTH, DAY_OF_MONTH, HOUR_OF_DAY, MINUTE, SECOND, MILLISECOND
    };

    public boolean isDatetimeOf(Datetime datetime) { return equals(datetime); }

    public boolean isDatetimeBefore(Datetime datetime) { return before(datetime); }

    public boolean isDatetimeAfter(Datetime datetime) { return after(datetime); }

    public boolean isDatetimeOf(int... values) {
        if (values == null) {
            return false;
        }
        DatetimeField[] fields = FIELDS;
        final int length = Math.min(values.length, fields.length);
        for (int i = 0; i < length; i++) {
            if (getField(fields[i]) != values[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean isDatetimeBefore(int... values) {
        if (values == null) {
            return false;
        }
        DatetimeField[] fields = FIELDS;
        final int length = Math.min(values.length, fields.length);
        for (int i = 0; i < length; i++) {
            int thisVal = getField(fields[i]);
            int thatVal = values[i];
            if (thisVal < thatVal) {
                return true;
            }
            if (thisVal > thatVal) {
                return false;
            }
        }
        return false;
    }

    public boolean isDatetimeAfter(int... values) {
        if (values == null) {
            return false;
        }
        DatetimeField[] fields = FIELDS;
        final int length = Math.min(values.length, fields.length);
        for (int i = 0; i < length; i++) {
            int thisVal = getField(fields[i]);
            int thatVal = values[i];
            if (thisVal < thatVal) {
                return false;
            }
            if (thisVal > thatVal) {
                return true;
            }
        }
        return false;
    }

    public boolean isDatetimeOf(String... values) {
        if (values == null) {
            return false;
        }
        DatetimeField[] fields = FIELDS;
        final int length = Math.min(values.length, fields.length);
        for (int i = 0; i < length; i++) {
            if (Objects.equals(getField(fields[i]), Integer.parseInt(values[i]))) {
                return false;
            }
        }
        return true;
    }

    public boolean isDatetimeBefore(String... values) {
        return values == null ? false : isDatetimeBefore(IntUtil.toInts(values));
    }

    public boolean isDatetimeAfter(String... values) {
        return values == null ? false : isDatetimeAfter(IntUtil.toInts(values));
    }

    /*
     * ****************************************************************************
     * * 转换器 ********************************************************************
     * ****************************************************************************
     */

    @Override
    public Instant toInstant() { return obtainCalendar().toInstant(); }

    public Calendar toCalendar() { return DateUtil.copy(obtainCalendar()); }

    public Date toDate() { return obtainCalendar().getTime(); }

    public java.sql.Date toSqlDate() { return new java.sql.Date(getTime()); }

    public Timestamp toTimestamp() { return new Timestamp(getTime()); }

    public LocalTime toLocalTime() {
        return LocalTime.of(getHourOfDay(), getMinuteOfHour(), getSecondOfMinute(), getNanoOfSecond());
    }

    public LocalDate toLocalDate() {
        return LocalDate.of(getYearValue(), getMonthValue(), getDayOfMonth());
    }

    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.of(toLocalDate(), toLocalTime());
    }

    public org.joda.time.LocalDateTime toJodaLocalDateTime() {
        return org.joda.time.LocalDateTime.fromCalendarFields(obtainCalendar());
    }

    public org.joda.time.DateTime toJodaDateTime() {
        return toJodaLocalDateTime().toDateTime();
    }

    public org.joda.time.LocalDate toJodaLocalDate() {
        return org.joda.time.LocalDate.fromCalendarFields(obtainCalendar());
    }

    public org.joda.time.LocalTime toJodaLocalTime() {
        return org.joda.time.LocalTime.fromCalendarFields(obtainCalendar());
    }

    /*
     *************************************************************************
    override methods on java.util.Date
     *************************************************************************
     */

    @Override
    public void setTime(long time) {
        obtainCalendar().setTimeInMillis(time);
    }

    @Override
    public boolean before(Date when) { return compareTo(when) < 0; }

    @Override
    public boolean after(Date when) { return compareTo(when) > 0; }

    @Override
    public int compareTo(Date anotherDate) {
        long thisTime = getTime();
        long thatTime = anotherDate.getTime();
        return thisTime > thatTime ? 1 : (thisTime == thatTime ? 0 : -1);
    }

    @Override
    public int hashCode() {
        long ht = this.getTime();
        return (int) ht ^ (int) (ht >> 32);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Datetime && ((Datetime) obj).getTime() == getTime();
    }

    @Override
    public Datetime clone() { return new Datetime(this); }

    @Override
    public String toString() { return toString(DateUtil.PATTERN); }

    public String toString(String pattern) {
        return DateUtil.format(obtainCalendar().getTime(), pattern);
    }

    /*
     *************************************************************************
    Supports for @Deprecated methods on java.util.Date
     *************************************************************************
     */

    public Datetime(int year, int month) {
        this(year, month, 1);
    }

    public Datetime(int year, int month, int date) {
        this(year, month, date, 0, 0);
    }

    public Datetime(int year, int month, int date, int hrs, int min) {
        this(year, month, date, hrs, min, 0);
    }

    public Datetime(int year, int month, int date, int hrs, int min, int sec) {
        this(DateUtil.toCalendar(year, month, date, hrs, min, sec));
    }

    @Override
    @Deprecated
    public int getMonth() { return getMonthValue(); }

    @Override
    @Deprecated
    public int getYear() { return getYearValue(); }

    @Override
    @Deprecated
    public void setYear(int year) { withYear(year); }

    @Override
    @Deprecated
    public void setMonth(int month) { withMonth(month); }

    @Override
    @Deprecated
    public int getDate() { return getDayOfMonth(); }

    @Override
    @Deprecated
    public void setDate(int date) { withDayOfMonth(date); }

    @Override
    @Deprecated
    public int getDay() { return getDayOfWeek(); }

    @Override
    @Deprecated
    public int getHours() { return getHourOfDay(); }

    @Override
    @Deprecated
    public void setHours(int hours) { withHourOfDay(hours); }

    @Override
    @Deprecated
    public int getMinutes() { return getMinuteOfHour(); }

    @Override
    @Deprecated
    public void setMinutes(int minutes) { withMinute(minutes); }

    @Override
    @Deprecated
    public int getSeconds() { return getSecondOfMinute(); }

    @Override
    @Deprecated
    public void setSeconds(int seconds) { withSecond(seconds); }

    @Override
    @Deprecated
    public String toLocaleString() { return toString(); }

    @Override
    @Deprecated
    public String toGMTString() { return toString(); }

    @Override
    @Deprecated
    public int getTimezoneOffset() {
        Calendar calendar = obtainCalendar();
        return -(calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / 60000;
    }
}
