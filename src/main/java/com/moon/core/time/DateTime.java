package com.moon.core.time;

import com.moon.core.lang.IntUtil;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.function.ToIntBiFunction;

import static com.moon.core.time.CalendarUtil.copy;
import static com.moon.core.time.CalendarUtil.overrideCalendar;
import static com.moon.core.time.DateTimeField.*;
import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.NANO_OF_DAY;

/**
 * 写这个的时候在想很多判断是否有必要每种日期数据类型都有必要写出来？
 * 想想还是觉得有必要，这里不写很多时候实际业务中还是会单独写！所有这里会有很多{@code is}开头的方法
 * <p>
 * 命名为{@code Datetime}小写“t”为了不与{@code org.joda.time.DateTime}冲突
 * <p>
 * 可变对象可不可变对象切换：{@link #asImmutable()}{@link #asMutable()}
 * <p>
 * 默认星期一是一周第一天:
 * {@link #getFirstDayOfWeekValue()}、{@link #getFirstDayOfWeek()}；
 * {@link #startOfWeek(DayOfWeek)}、{@link #endOfWeek(DayOfWeek)}；
 * <p>
 * 方法概览：
 * <pre>
 * 1. getXxx: 获取日期属性相关方法
 * 2. withXxx: 设置日期属性相关方法（区分可变和不可变对象，返回值可能不同）
 * 3. plusXxx: 日期加法，增加年月日等
 * 3. minusXxx: 减法，减去年月日等
 * 4. startOfXxx: 某个范围的起始时间，如 startOfMonth => 月初（当月的最初时刻）
 * 5. endOfXxx: 某个范围的最末时刻；
 * 6. isXxx: 日期比较
 * 7. toXxx: 各种日期类型转换
 * </pre>
 * <p>
 * 注意：{@link #setTime(long)}无论如何都是设置当前对象，与是否是可变对象无关，建议使用{@link #withTimeInMillis(long)}
 *
 * @author moonsky
 */
public final class DateTime extends Date implements TemporalAccessor, TemporalAdjuster, Temporal {

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

    private final Calendar calendar;
    /**
     * 是否是不可变对象(默认为 false，是可变对象)
     */
    private final boolean immutable;

    Calendar originCalendar() { return calendar; }

    private DateTime obtainReturning(Calendar calendar) { return immutable ? new DateTime(calendar, true) : this; }

    private Calendar obtainCalendar() { return immutable ? copy(calendar) : calendar; }

    /*
     * *********************************************************************************************
     * * 构造器 *************************************************************************************
     * *********************************************************************************************
     */

    public DateTime(Calendar originCalendar, boolean immutable) {
        super(originCalendar.getTimeInMillis());
        this.calendar = originCalendar;
        this.immutable = immutable;
    }

    public DateTime(long timeMillis) { this(DateUtil.toCalendar(timeMillis)); }

    public DateTime(Calendar calendar) { this(calendar, false); }

    public DateTime(String date) { this(DateUtil.toCalendar(date)); }

    public DateTime(CharSequence date) { this(DateUtil.toCalendar(date)); }

    public DateTime(Date date) { this(date.getTime()); }

    public DateTime(LocalDate localDate) { this(DateUtil.toCalendar(localDate)); }

    public DateTime(LocalDateTime datetime) { this(DateUtil.toCalendar(datetime)); }

    public DateTime(DateTime datetime) { this(copy(datetime.calendar), datetime.immutable); }

    public DateTime(Instant instant) { this(instant.toEpochMilli()); }

    public DateTime(org.joda.time.DateTime datetime) { this(datetime.toDate()); }

    public DateTime(org.joda.time.LocalDate date) { this(date.toDate()); }

    public DateTime(org.joda.time.LocalDateTime datetime) { this(datetime.toDate()); }

    public DateTime() { this(System.currentTimeMillis()); }

    /*
     * **************************************************************************************
     * * 静态方法 ****************************************************************************
     * **************************************************************************************
     */

    public static DateTime now() { return new DateTime(); }

    public static DateTime of() { return now(); }

    public static DateTime of(Date date) { return new DateTime(date); }

    public static DateTime of(Calendar calendar) { return new DateTime(calendar); }

    public static DateTime of(long timeOfMillis) { return new DateTime(timeOfMillis); }

    public static DateTime of(CharSequence dateStr) { return new DateTime(dateStr); }

    public static DateTime ofFields(int... fields) { return new DateTime(DateUtil.toCalendar(fields)); }

    public static DateTime ofToday(LocalTime time) { return new DateTime(time.atDate(LocalDate.now())); }

    public static DateTime of(LocalDate date) { return new DateTime(date); }

    public static DateTime of(LocalDateTime datetime) { return new DateTime(datetime); }

    /*
     * 构造不可变对象 ***************************************************************************************
     */

    public static DateTime ofImmutable() { return ofImmutable(DateUtil.getInstance()); }

    public static DateTime ofImmutable(Date date) { return ofImmutable(DateUtil.toCalendar(date)); }

    public static DateTime ofImmutable(Calendar calendar) { return new DateTime(calendar, true); }

    public static DateTime ofImmutable(long timeOfMillis) { return ofImmutable(DateUtil.toCalendar(timeOfMillis)); }

    public static DateTime ofImmutable(CharSequence dateStr) { return ofImmutable(DateUtil.toCalendar(dateStr)); }

    public static DateTime ofImmutableFields(int... fields) { return ofImmutable(DateUtil.toCalendar(fields)); }

    public static DateTime ofImmutable(LocalDate date) { return ofImmutable(DateUtil.toCalendar(date)); }

    public static DateTime ofImmutable(LocalDateTime datetime) { return ofImmutable(DateUtil.toCalendar(datetime)); }

    /*
     * ************************************************************************************************
     * getter & setter ********************************************************************************
     * ************************************************************************************************
     */

    /**
     * 是否是不可变对象
     *
     * @return 不可变对象每次返回的是新对象，否则操作和返回的是新对象
     */
    public boolean isImmutable() { return immutable; }

    public boolean isMutable() { return !immutable; }

    /**
     * 设置为不可变数据，不可变数据每次操作均返回新对象
     *
     * @see #obtainReturning(Calendar)
     */
    public DateTime asImmutable() { return immutable ? this : new DateTime(copy(calendar), true); }

    /**
     * 设置为可变数据，可变数据操作的是同一个对象
     *
     * @see #obtainReturning(Calendar)
     */
    public DateTime asMutable() { return immutable ? new DateTime(copy(calendar), false) : this; }

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
     * 返回当前月份，1 * 12
     *
     * @return 月份
     */
    public int getMonthValue() { return getField(MONTH); }

    /**
     * 获取月份索引，0 * 11
     *
     * @return 月份索引
     */
    public int getMonthIndex() { return getMonthValue() - 1; }

    /**
     * 获取当前月份
     *
     * @return 月份
     *
     * @see Month
     */
    public DateTimeMonth getMonthOfYear() { return DateTimeMonth.of(getMonthValue()); }

    /**
     * 获取星座
     *
     * @return Constellation
     */
    public Constellation getConstellation() {
        return Constellation.of(getMonthValue(), getDayOfMonth());
    }

    /**
     * 获取季节
     *
     * @return 季节
     */
    public Season getSeason() { return Season.ofMonth(getMonthValue()); }

    /**
     * 获取年龄（周岁），假设当前{@code Datetime}是某一对象的生日
     * 返回到当前时刻的年数
     *
     * @return 返回周岁年龄
     */
    public int getAge() { return CalendarUtil.getAge(originCalendar()); }

    /**
     * 获取年龄（虚岁）
     *
     * @return 返回虚岁年龄
     */
    public int getNominalAge() { return getAge() + 1; }

    /**
     * 返回当前是一年中的第 N 个星期
     *
     * @return N
     */
    public int getWeekOfYear() { return getField(DateTimeField.WEEK_OF_YEAR); }

    /**
     * 返回当前日期是当前月中的第 N 个星期
     *
     * @return N
     */
    public int getWeekOfMonth() { return getField(DateTimeField.WEEK_OF_MONTH); }

    /**
     * 返回当前是一年中的第 N 天
     *
     * @return N
     */
    public int getDayOfYear() { return getField(DateTimeField.DAY_OF_YEAR); }

    /**
     * 返回当前日期是当前月中的第 N 天
     *
     * @return N
     */
    public int getDayOfMonth() { return getField(DAY_OF_MONTH); }

    /**
     * 返回当前是星期几，从 0 开始
     *
     * @return 0 * 6
     */
    public int getDayOfWeekValue() { return getField(DateTimeField.DAY_OF_WEEK); }

    /**
     * 返回当前是星期几
     *
     * @return DayOfWeek
     */
    public DayOfWeek getDayOfWeek() {
        int value = getDayOfWeekValue();
        return value > 1 ? DayOfWeek.of(value - 1) : DayOfWeek.SUNDAY;
    }

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
     * Minutes per hour.
     */
    static final int MINUTES_PER_HOUR = 60;
    /**
     * Seconds per hour.
     */
    static final int SECONDS_PER_MINUTE = 60;
    /**
     * Nanos per second.
     */
    static final int MILLS_PER_SECOND = 1000;
    /**
     * Nanos per second.
     */
    static final long NANOS_PER_SECOND = 1000_000_000L;
    /**
     * Nanos per minute.
     */
    static final long NANOS_PER_MINUTE = NANOS_PER_SECOND * SECONDS_PER_MINUTE;
    /**
     * Nanos per hour.
     */
    static final long NANOS_PER_HOUR = NANOS_PER_MINUTE * MINUTES_PER_HOUR;

    /**
     * 纳秒数
     * <p>
     * copied from {@link LocalTime#toNanoOfDay()}
     *
     * @return 纳秒数
     */
    public long getNanoOfDay() {
        long total = getHourOfDay() * NANOS_PER_HOUR;
        total += getMinuteOfHour() * NANOS_PER_MINUTE;
        total += getSecondOfMinute() * NANOS_PER_SECOND;
        total += getNanoOfSecond();
        return total;
    }

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
    public long getTime() { return originCalendar().getTimeInMillis(); }

    /**
     * 获取指定字段的值
     * <p>
     * 注：获取月份返回的是 1 * 12
     *
     * @param field 字段
     *
     * @return 字段值
     */
    public int getField(DateTimeField field) { return get(field.value); }

    /**
     * 获取指定字段值
     * <p>
     * 注：获取月份返回的是 1 * 12
     *
     * @param field 字段
     *
     * @return 字段值
     */
    public int get(int field) { return getFieldVal(obtainCalendar(), field); }

    private static int getFieldVal(Calendar calendar, int field) {
        return field == Calendar.MONTH ? calendar.get(field) + 1 : calendar.get(field);
    }

    /**
     * 返回一周第一天是星期几
     *
     * @return 一周的第一天
     */
    public DayOfWeek getFirstDayOfWeek() { return DayOfWeek.of(getFirstDayOfWeekValue()); }

    /**
     * 返回一周第一天是星期几( 0 ~ 6 )
     *
     * @return 一周的第一天序号
     */
    public int getFirstDayOfWeekValue() { return calendar.getFirstDayOfWeek(); }

    /*
     * ****************************************************************************
     * * 设置日期数据 ***************************************************************
     * ****************************************************************************
     */

    /**
     * 设置毫秒数
     *
     * @param timeInMillis 时间戳
     *
     * @return 时间对象
     */
    public DateTime withTimeInMillis(long timeInMillis) {
        Calendar calendar = obtainCalendar();
        calendar.setTimeInMillis(timeInMillis);
        return obtainReturning(calendar);
    }

    /**
     * 设置秒数
     *
     * @param value 秒数
     *
     * @return 当前对象
     */
    public DateTime withSecond(int value) { return withField(SECOND, value); }

    /**
     * 设置分钟数
     *
     * @param value 分钟数
     *
     * @return 当前对象
     */
    public DateTime withMinute(int value) { return withField(MINUTE, value); }

    /**
     * 设置小时数（12 小时制）
     *
     * @param value 小时数
     *
     * @return 当前对象
     */
    public DateTime withHour(int value) { return withField(DateTimeField.HOUR, value); }

    /**
     * 设置小时数（24 小时制）
     *
     * @param value 小时数
     *
     * @return 当前对象
     */
    public DateTime withHourOfDay(int value) { return withField(HOUR_OF_DAY, value); }

    /**
     * 设置星期
     *
     * @param value 星期
     *
     * @return 当前对象
     */
    public DateTime withDayOfWeek(int value) { return withField(DateTimeField.DAY_OF_WEEK, value); }

    /**
     * 设置星期
     *
     * @param dayOfWeek 星期
     *
     * @return 当前对象
     */
    public DateTime withDayOfWeek(DayOfWeek dayOfWeek) {
        return withField(DateTimeField.DAY_OF_WEEK, dayOfWeek == DayOfWeek.SUNDAY ? 1 : dayOfWeek.getValue() + 1);
    }

    /**
     * 设置当月的第几天
     *
     * @param value 日期
     *
     * @return 当前对象
     */
    public DateTime withDayOfMonth(int value) { return withField(DAY_OF_MONTH, value); }

    /**
     * 设置当前年的第几天
     *
     * @param value 天数
     *
     * @return 当前对象
     */
    public DateTime withDayOfYear(int value) { return withField(DateTimeField.DAY_OF_YEAR, value); }

    /**
     * 设置当前月份，1 * 12
     *
     * @param value 月份
     *
     * @return 当前对象
     */
    public DateTime withMonth(int value) { return withField(MONTH, value); }

    /**
     * 设置当前月份，0 * 11
     *
     * @param index 月份索引：0 * 11
     *
     * @return 当前对象
     */
    public DateTime withMonthIndex(int index) { return withMonth(index + 1); }

    /**
     * 设置月份，1 * 12
     *
     * @param month 月份
     *
     * @return 当前对象
     */
    public DateTime withMonth(DateTimeMonth month) { return withMonth(month.getValue()); }

    /**
     * 设置月份，1 * 12
     *
     * @param month 月份
     *
     * @return 当前对象
     */
    public DateTime withMonth(Month month) { return withMonth(month.getValue()); }

    /**
     * 设置年份
     *
     * @param value 年份
     *
     * @return 当前对象
     */
    public DateTime withYear(int value) { return withField(YEAR, value); }

    /**
     * 设置指定字段值
     *
     * @param field 字段
     * @param value 字段值
     *
     * @return 当前对象
     */
    public DateTime withField(DateTimeField field, int value) { return with(field.value, value); }

    /**
     * 设置指定字段值
     *
     * @param field 字段
     * @param value 字段值
     *
     * @return 当前对象
     */
    public DateTime with(int field, int value) {
        return obtainReturning(setFieldVal(obtainCalendar(), field, value));
    }

    private static Calendar setFieldVal(Calendar calendar, int field, int value) {
        calendar.set(field, field == Calendar.MONTH ? value - 1 : value);
        return calendar;
    }

    /**
     * 设置一周的第一天是星期几
     *
     * @param firstDayOfWeek 给定一周的第一天
     *
     * @return 当前对象
     *
     * @see #startOfWeek(DayOfWeek)
     * @see #endOfWeek(DayOfWeek)
     */
    public DateTime setFirstDayOfWeek(DayOfWeek firstDayOfWeek) { return setFirstDayOfWeek(firstDayOfWeek.getValue()); }

    /**
     * 设置一周的第一天是星期几
     *
     * @param firstDayOfWeek 给定一周的第一天
     *
     * @return 当前对象
     */
    public DateTime setFirstDayOfWeek(int firstDayOfWeek) {
        Calendar calendar = obtainCalendar();
        calendar.setFirstDayOfWeek(firstDayOfWeek);
        return obtainReturning(calendar);
    }

    /**
     * 设置第一个星期最少多少天(默认: {@code value == 4})
     *
     * @param value 年份或月份的第一个星期很可能跨年或跨月，这种情况下设置在当前月份的部分最少多少天才视为第一个星期
     *
     * @return Datetime
     */
    public DateTime setMinimalDaysInFirstWeek(int value) {
        Calendar calendar = obtainCalendar();
        calendar.setMinimalDaysInFirstWeek(value);
        return obtainReturning(calendar);
    }

    /*
     * ****************************************************************************
     * * 日期加法操作 ***************************************************************
     * ****************************************************************************
     */

    public DateTime plusYears(int amount) { return plusField(YEAR, amount); }

    public DateTime plusMonths(int amount) { return plusField(MONTH, amount); }

    public DateTime plusWeeks(int amount) { return plusField(WEEK_OF_YEAR, amount); }

    public DateTime plusDays(int amount) { return plusField(DAY_OF_YEAR, amount); }

    public DateTime plusHours(int amount) { return plusField(HOUR_OF_DAY, amount); }

    public DateTime plusMinutes(int amount) { return plusField(MINUTE, amount); }

    public DateTime plusSeconds(int amount) { return plusField(SECOND, amount); }

    public DateTime plusMillis(int amount) { return plusField(MILLISECOND, amount); }

    public DateTime plusField(DateTimeField field, int amount) { return plus(field.value, amount); }

    public DateTime plus(int field, int amount) { return change(field, amount); }

    /*
     * ****************************************************************************
     * * 日期减法操作 ***************************************************************
     * ****************************************************************************
     */

    public DateTime minusYears(int amount) { return minusField(YEAR, amount); }

    public DateTime minusMonths(int amount) { return minusField(MONTH, amount); }

    public DateTime minusWeeks(int amount) { return minusField(WEEK_OF_YEAR, amount); }

    public DateTime minusDays(int amount) { return minusField(DAY_OF_MONTH, amount); }

    public DateTime minusHours(int amount) { return minusField(HOUR_OF_DAY, amount); }

    public DateTime minusMinutes(int amount) { return minusField(MINUTE, amount); }

    public DateTime minusSeconds(int amount) { return minusField(SECOND, amount); }

    public DateTime minusMillis(int amount) { return minusField(MILLISECOND, amount); }

    public DateTime minusField(DateTimeField field, int amount) { return minus(field.value, amount); }

    public DateTime minus(int field, int amount) { return change(field, -amount); }

    private DateTime change(int field, int amount) {
        return obtainReturning(changeCalendarOf(obtainCalendar(), field, amount));
    }

    private static Calendar changeCalendarOf(Calendar calendar, int field, int amount) {
        calendar.add(field, amount);
        return calendar;
    }

    @Override
    public boolean isSupported(TemporalUnit unit) { return toInstant().isSupported(unit); }

    @Override
    public DateTime with(TemporalField field, long newValue) {
        LocalDateTime datetime = toLocalDateTime().with(field, newValue);
        return obtainReturning(overrideCalendar(obtainCalendar(), datetime));
    }

    @Override
    public DateTime plus(long amountToAdd, TemporalUnit unit) {
        LocalDateTime datetime = toLocalDateTime().plus(amountToAdd, unit);
        return obtainReturning(overrideCalendar(obtainCalendar(), datetime));
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        return toLocalDateTime().until(endExclusive, unit);
    }

    private enum CalendarField {
        /**
         * 毫秒
         */
        MILLISECOND(Calendar.MILLISECOND, 0, 999),
        /**
         * 秒
         */
        SECOND(Calendar.SECOND, 0, 59),
        MINUTE(Calendar.MINUTE, 0, 59),
        HOUR_OF_DAY(Calendar.HOUR_OF_DAY, 0, 23),
        DAY_OF_MONTH(Calendar.DAY_OF_MONTH, 1, 31) {
            @Override
            public int getFieldMaxVal(Calendar value) {
                return Month.of(getFieldVal(value, Calendar.MONTH))
                    .length(Year.isLeap(getFieldVal(value, Calendar.YEAR)));
            }
        },
        MONTH(Calendar.MONTH, 1, 12);

        private final static CalendarField[] FIELDS = CalendarField.values();
        private final int field;
        private final int min;
        private final int max;

        CalendarField(int field, int min, int max) {
            this.field = field;
            this.min = min;
            this.max = max;
        }

        public int getFieldMaxVal(Calendar c) { return max; }
    }

    private DateTime boundaryOf(CalendarField ending, ToIntBiFunction<Calendar, CalendarField> getter) {
        CalendarField[] values = CalendarField.FIELDS;
        CalendarField field;
        Calendar calendar = obtainCalendar();
        int last = Math.min(ending.ordinal() + 1, values.length);
        for (int i = 0; i < last; i++) {
            field = values[i];
            setFieldVal(calendar, field.field, getter.applyAsInt(calendar, field));
        }
        return obtainReturning(calendar);
    }

    private DateTime startingOf(CalendarField ending) {
        return boundaryOf(ending, (c, field) -> field.min);
    }

    private DateTime endingOf(CalendarField ending) {
        return boundaryOf(ending, (c, f) -> f.getFieldMaxVal(c));
    }

    /**
     * 清除毫秒
     *
     * @return Datetime
     */
    public DateTime startOfSecond() { return startingOf(CalendarField.MILLISECOND); }

    /**
     * 清除秒
     *
     * @return Datetime
     */
    public DateTime startOfMinute() { return startingOf(CalendarField.SECOND); }

    /**
     * 清除分钟
     *
     * @return Datetime
     */
    public DateTime startOfHour() { return startingOf(CalendarField.MINUTE); }

    /**
     * 凌晨 00:00:00
     *
     * @return Datetime
     */
    public DateTime startOfDay() { return startingOf(CalendarField.HOUR_OF_DAY); }

    /**
     * 当前星期的最初时刻
     *
     * @return Datetime
     *
     * @see #getFirstDayOfWeek()
     * @see #getFirstDayOfWeekValue()
     */
    public DateTime startOfWeek() { return startOfWeek(getFirstDayOfWeek()); }

    /**
     * 当前星期的最初时刻
     *
     * @param firstDayOfWeek 指定星期几为一周第一天，这里不会修改当前对象的设置，
     *                       只是根据给定的每周第一天设置数据
     *
     * @return Datetime
     */
    public DateTime startOfWeek(DayOfWeek firstDayOfWeek) {
        DateTime datetime = startOfDay();
        int diffDays = datetime.getDayOfWeek().ordinal() - firstDayOfWeek.ordinal();
        Calendar calendar = datetime.originCalendar();
        changeCalendarOf(calendar, Calendar.DAY_OF_MONTH, -(diffDays < 0 ? diffDays + 7 : diffDays));
        return datetime;
    }

    /**
     * 月初
     *
     * @return Datetime
     */
    public DateTime startOfMonth() { return startingOf(CalendarField.DAY_OF_MONTH); }

    /**
     * 年初
     *
     * @return Datetime
     */
    public DateTime startOfYear() { return startingOf(CalendarField.MONTH); }

    /**
     * 秒末
     *
     * @return Datetime
     */
    public DateTime endOfSecond() { return endingOf(CalendarField.MILLISECOND); }

    /**
     * 分末
     *
     * @return Datetime
     */
    public DateTime endOfMinute() { return endingOf(CalendarField.SECOND); }

    /**
     * 时末
     *
     * @return Datetime
     */
    public DateTime endOfHour() { return endingOf(CalendarField.MINUTE); }

    /**
     * 深夜，23:59:59:999
     *
     * @return Datetime
     */
    public DateTime endOfDay() { return endingOf(CalendarField.HOUR_OF_DAY); }

    /**
     * 本周最末时刻
     *
     * @return Datetime
     */
    public DateTime endOfWeek() { return endOfWeek(getFirstDayOfWeek()); }

    /**
     * 本周最末时刻
     *
     * @param firstDayOfWeek 指定星期几为一周第一天
     *
     * @return Datetime
     *
     * @see #setFirstDayOfWeek(int)
     * @see #setFirstDayOfWeek(DayOfWeek)
     */
    public DateTime endOfWeek(DayOfWeek firstDayOfWeek) {
        DateTime datetime = endOfDay();
        int diffDays = firstDayOfWeek.ordinal() - datetime.getDayOfWeek().ordinal();
        changeCalendarOf(datetime.originCalendar(),
            Calendar.DAY_OF_MONTH,
            (diffDays <= 0 ? diffDays + 6 : diffDays - 1));
        return datetime;
    }

    /**
     * 月末
     *
     * @return Datetime
     */
    public DateTime endOfMonth() { return endingOf(CalendarField.DAY_OF_MONTH); }

    /**
     * 年末
     *
     * @return Datetime
     */
    public DateTime endOfYear() {
        DateTime datetime = endOfDay();
        Calendar calendar = datetime.originCalendar();
        setFieldVal(calendar, Calendar.DAY_OF_MONTH, 31);
        setFieldVal(calendar, Calendar.MONTH, 12);
        return datetime;
    }

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
    public boolean isAm() { return originCalendar().get(Calendar.AM_PM) == Calendar.AM; }

    /**
     * 是否是下午
     *
     * @return 24 小时制 12 点以后，返回 true，否则返回false
     */
    public boolean isPm() { return originCalendar().get(Calendar.AM_PM) == Calendar.PM; }

    /**
     * 是否是工作日(仅指非周末)
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
        final int dayOfWeek = getDayOfWeekValue();
        return dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY;
    }

    /**
     * 是否是闰年
     *
     * @return 如果是闰年返回 true，否则 false
     */
    public boolean isLeapYear() { return Year.isLeap(getYearValue()); }

    /**
     * 是否是指定年份
     *
     * @param year 指定年份
     *
     * @return this
     */
    public boolean isYearOf(int year) { return getYearValue() == year; }

    /**
     * 是否在指定年份之前
     *
     * @param year 指定年份
     *
     * @return 当日期所代表的年份在待测年份之前时返回 true，否则返回 false
     */
    public boolean isYearBefore(int year) { return getYearValue() < year; }

    public boolean isYearAfter(int year) { return getYearValue() > year; }

    public boolean isYearOf(DateTime date) { return date != null && isYearOf(date.getYearValue()); }

    public boolean isYearBefore(DateTime date) { return date != null && isYearBefore(date.getYearValue()); }

    public boolean isYearAfter(DateTime date) { return date != null && isYearAfter(date.getYearValue()); }

    public boolean isMonthOf(int year, int month) { return isYearOf(year) && isMonthAt(month); }

    public boolean isMonthAt(int month) { return getMonthValue() == month; }

    public boolean isMonthBefore(int year, int month) {
        return isYearBefore(year) || (isYearOf(year) && getMonthValue() < month);
    }

    public boolean isMonthAfter(int year, int month) {
        return isYearAfter(year) || (isYearOf(year) && getMonthValue() > month);
    }

    public boolean isMonthOf(DateTime date) {
        return date != null && isMonthOf(date.getYearValue(), date.getMonthValue());
    }

    public boolean isMonthBefore(DateTime date) {
        return date != null && isMonthBefore(date.getYearValue(), date.getMonthValue());
    }

    public boolean isMonthAfter(DateTime date) {
        return date != null && isMonthAfter(date.getYearValue(), date.getMonthValue());
    }

    public boolean isDateOf(int year, int month, int dayOfMonth) {
        return isMonthOf(year, month) && isDateAt(dayOfMonth);
    }

    public boolean isDateAt(int dayOfMonth) { return getDayOfMonth() == dayOfMonth; }

    public boolean isDateBefore(int year, int month, int dayOfMonth) {
        return isMonthBefore(year, month) || (isMonthOf(year, month) && getDayOfMonth() < dayOfMonth);
    }

    public boolean isDateAfter(int year, int month, int dayOfMonth) {
        return isMonthAfter(year, month) || (isMonthOf(year, month) && getDayOfMonth() > dayOfMonth);
    }

    public boolean isDateOf(DateTime date) {
        return date != null && isDateOf(date.getYearValue(), date.getMonthValue(), date.getDayOfMonth());
    }

    public boolean isDateBefore(DateTime date) {
        return date != null && isDateBefore(date.getYearValue(), date.getMonthValue(), date.getDayOfMonth());
    }

    public boolean isDateAfter(DateTime date) {
        return date != null && isDateAfter(date.getYearValue(), date.getMonthValue(), date.getDayOfMonth());
    }

    public boolean isHourOf(int hour) { return getHourOfDay() == hour; }

    public boolean isHourBefore(int hour) { return getHourOfDay() < hour; }

    public boolean isHourAfter(int hour) { return getHourOfDay() > hour; }

    public boolean isHourOf(DateTime hour) { return hour != null && isHourOf(hour.getHourOfDay()); }

    public boolean isHourBefore(DateTime hour) { return hour != null && isHourBefore(hour.getHourOfDay()); }

    public boolean isHourAfter(DateTime hour) { return hour != null && isHourAfter(hour.getHourOfDay()); }

    public boolean isMinuteOf(int hour, int minute) { return isHourOf(hour) && isMinuteAt(minute); }

    public boolean isMinuteAt(int minute) { return getMinuteOfHour() == minute; }

    public boolean isMinuteBefore(int hour, int minute) {
        return isHourBefore(hour) || (isHourOf(hour) && getMinuteOfHour() < minute);
    }

    public boolean isMinuteAfter(int hour, int minute) {
        return isHourAfter(hour) || (isHourOf(hour) && getMinuteOfHour() > minute);
    }

    public boolean isMinuteOf(DateTime datetime) {
        return datetime != null && isMinuteOf(datetime.getHourOfDay(), datetime.getMinuteOfHour());
    }

    public boolean isMinuteBefore(DateTime datetime) {
        return datetime != null && isMinuteBefore(datetime.getHourOfDay(), datetime.getMinuteOfHour());
    }

    public boolean isMinuteAfter(DateTime datetime) {
        return datetime != null && isMinuteAfter(datetime.getHourOfDay(), datetime.getMinuteOfHour());
    }

    public boolean isSecondOf(int hour, int minute, int second) {
        return isMinuteOf(hour, minute) && isSecondAt(second);
    }

    public boolean isSecondAt(int second) { return getSecondOfMinute() == second; }

    public boolean isSecondBefore(int hour, int minute, int second) {
        return isMinuteBefore(hour, minute) || (isMinuteOf(hour, minute) && getSecondOfMinute() < second);
    }

    public boolean isSecondAfter(int hour, int minute, int second) {
        return isMinuteAfter(hour, minute) || (isMinuteOf(hour, minute) && getSecondOfMinute() > second);
    }

    public boolean isSecondOf(DateTime time) {
        return time != null && isSecondOf(time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute());
    }

    public boolean isSecondBefore(DateTime time) {
        return time != null && isSecondBefore(time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute());
    }

    public boolean isSecondAfter(DateTime time) {
        return time != null && isSecondAfter(time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute());
    }

    private final static DateTimeField[] FIELDS = {
        YEAR, MONTH, DAY_OF_MONTH, HOUR_OF_DAY, MINUTE, SECOND, MILLISECOND
    };

    public boolean isDatetimeOf(DateTime datetime) { return equals(datetime); }

    public boolean isDatetimeBefore(DateTime datetime) { return before(datetime); }

    public boolean isDatetimeAfter(DateTime datetime) { return after(datetime); }

    public boolean isDatetimeOf(int... values) {
        if (values == null) {
            return false;
        }
        DateTimeField[] fields = FIELDS;
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
        DateTimeField[] fields = FIELDS;
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
        DateTimeField[] fields = FIELDS;
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
        DateTimeField[] fields = FIELDS;
        final int length = Math.min(values.length, fields.length);
        for (int i = 0; i < length; i++) {
            if (Objects.equals(getField(fields[i]), Integer.parseInt(values[i]))) {
                return false;
            }
        }
        return true;
    }

    public boolean isDatetimeBefore(String... values) {
        return values != null && isDatetimeBefore(IntUtil.toInts(values));
    }

    public boolean isDatetimeAfter(String... values) {
        return values != null && isDatetimeAfter(IntUtil.toInts(values));
    }

    public boolean isBefore(long timeInMillis) { return getTime() < timeInMillis; }

    public boolean isAfter(long timeInMillis) { return getTime() > timeInMillis; }

    /**
     * 当前日期所指的月份是否在指定日期（月份和日期）之前
     *
     * @param month      月份
     * @param dayOfMonth 日期
     *
     * @return
     */
    public boolean isBeforeMonthDay(int month, int dayOfMonth) {
        int thisMonth = getMonthValue(), thisDayOfMonth = getDayOfMonth();
        return thisMonth < month || (thisMonth == month && thisDayOfMonth < dayOfMonth);
    }

    /**
     * 当前日期所指的月份是否在指定日期（月份和日期）之后
     *
     * @param month      月份
     * @param dayOfMonth 日期
     *
     * @return
     */
    public boolean isAfterMonthDay(int month, int dayOfMonth) {
        int thisMonth = getMonthValue(), thisDayOfMonth = getDayOfMonth();
        return thisMonth > month || (thisMonth == month && thisDayOfMonth > dayOfMonth);
    }

    /**
     * 当前日期所指的时分是否在指定时分之前
     *
     * @param hour   小时
     * @param minute 分钟
     *
     * @return
     */
    public boolean isBeforeHourMinute(int hour, int minute) {
        int thisHour = getHourOfDay(), thisMinute = getMinuteOfHour();
        return thisHour < hour || (thisHour == hour && thisMinute < minute);
    }

    /**
     * 当前日期所指的时分是否在指定时分之后
     *
     * @param hour   小时
     * @param minute 分钟
     *
     * @return
     */
    public boolean isAfterHourMinute(int hour, int minute) {
        int thisHour = getHourOfDay(), thisMinute = getMinuteOfHour();
        return thisHour < hour || (thisHour == hour && thisMinute < minute);
    }

    /*
     * ****************************************************************************
     * * 转换器 ********************************************************************
     * ****************************************************************************
     */

    @Override
    public Instant toInstant() { return originCalendar().toInstant(); }

    public Calendar toCalendar() { return copy(originCalendar()); }

    public Date toDate() { return originCalendar().getTime(); }

    public java.sql.Date toSqlDate() { return new java.sql.Date(getTime()); }

    public Timestamp toTimestamp() { return new Timestamp(getTime()); }

    public LocalTime toLocalTime() {
        return LocalTime.of(getHourOfDay(), getMinuteOfHour(), getSecondOfMinute(), getNanoOfSecond());
    }

    public LocalDate toLocalDate() { return LocalDate.of(getYearValue(), getMonthValue(), getDayOfMonth()); }

    public LocalDateTime toLocalDateTime() { return LocalDateTime.of(toLocalDate(), toLocalTime()); }

    public YearMonth toYearMonth() {
        return YearMonth.of(getYearValue(), getMonthValue());
    }

    public MonthDay toMonthDay() {
        return MonthDay.of(getMonthValue(), getDayOfMonth());
    }

    public org.joda.time.LocalDateTime toJodaLocalDateTime() {
        return org.joda.time.LocalDateTime.fromCalendarFields(originCalendar());
    }

    public org.joda.time.DateTime toJodaDateTime() { return toJodaLocalDateTime().toDateTime(); }

    public org.joda.time.LocalDate toJodaLocalDate() {
        return org.joda.time.LocalDate.fromCalendarFields(originCalendar());
    }

    public org.joda.time.LocalTime toJodaLocalTime() {
        return org.joda.time.LocalTime.fromCalendarFields(originCalendar());
    }

    /*
     *************************************************************************
     * override methods for java.util.Date
     *************************************************************************
     */

    /**
     * 设置毫秒数时间戳(此方法的操作无论如何都是可变的)
     *
     * @param time
     *
     * @see #withTimeInMillis(long)
     */
    @Override
    public void setTime(long time) { originCalendar().setTimeInMillis(time); }

    @Override
    public boolean before(Date when) { return compareTo(when) < 0; }

    @Override
    public boolean after(Date when) { return compareTo(when) > 0; }

    @Override
    public int compareTo(Date anotherDate) {
        long thisTime = getTime();
        long thatTime = anotherDate.getTime();
        return Long.compare(thisTime, thatTime);
    }

    @Override
    public int hashCode() {
        long ht = this.getTime();
        return (int) ht ^ (int) (ht >> 32);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DateTime && ((DateTime) obj).getTime() == getTime();
    }

    @Override
    public DateTime clone() { return new DateTime(this); }

    @Override
    public String toString() { return toString(DateUtil.PATTERN); }

    public String toString(String pattern) { return DateUtil.format(originCalendar(), pattern); }

    public String toString(DateTimeFormatter formatter) { return formatter.format(this); }

    public String toString(DateFormat formatter) { return formatter.format(this); }

    /*
     *************************************************************************
    Supports for @Deprecated methods on java.util.Date
     *************************************************************************
     */

    public DateTime(int year, int month) { this(year, month, 1); }

    public DateTime(int year, int month, int date) { this(year, month, date, 0, 0); }

    public DateTime(int year, int month, int date, int hrs, int min) { this(year, month, date, hrs, min, 0); }

    public DateTime(int year, int month, int date, int hrs, int min, int sec) {
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
    public int getDay() { return getDayOfWeekValue(); }

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
        Calendar calendar = originCalendar();
        return -(calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / 60000;
    }

    @Override
    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            return f.isDateBased() || f.isTimeBased();
        }
        return field != null && field.isSupportedBy(this);
    }

    @Override
    public long getLong(TemporalField field) {
        if (field instanceof ChronoField) {
            ChronoField cf = (ChronoField) field;
            switch (cf) {
                case YEAR:
                case YEAR_OF_ERA:
                    return getYearValue();
                case DAY_OF_WEEK:
                    return getDayOfWeekValue();
                case DAY_OF_YEAR:
                    return getDayOfYear();
                case HOUR_OF_DAY:
                    return getHourOfDay();
                case DAY_OF_MONTH:
                    return getDayOfMonth();
                case MONTH_OF_YEAR:
                    return getMonthValue();
                case ERA:
                    return get(Calendar.ERA);
                case AMPM_OF_DAY:
                    return get(Calendar.AM_PM);
                case MINUTE_OF_HOUR:
                    return getMinuteOfHour();
                case MINUTE_OF_DAY:
                    return getMinuteOfDay();
                case SECOND_OF_DAY:
                    return getSecondOfDay();
                case SECOND_OF_MINUTE:
                    return getSecondOfMinute();
                case MILLI_OF_SECOND:
                    return getMillisOfSecond();
                case HOUR_OF_AMPM:
                    return get(Calendar.HOUR);
                case MILLI_OF_DAY:
                    return getMillisOfDay();
                case NANO_OF_SECOND:
                    return getNanoOfSecond();
                case EPOCH_DAY:
                    return toEpochDay();
                default:
            }
        }
        return toLocalDateTime().getLong(field);
    }

    /**
     * The number of days in a 400 year cycle.
     */
    private static final int DAYS_PER_CYCLE = 146097;
    /**
     * The number of days from year zero to year 1970.
     * There are five 400 year cycles from year zero to 2000.
     * There are 7 leap years from 1970 to 2000.
     */
    static final long DAYS_0000_TO_1970 = (DAYS_PER_CYCLE * 5L) - (30L * 365L + 7L);

    /**
     * copied from {@link LocalDate#toEpochDay()}
     *
     * @return {@link ChronoField#EPOCH_DAY}
     */
    public long toEpochDay() {
        long y = getYearValue();
        long m = getMonthValue();
        long total = 0;
        total += 365 * y;
        if (y >= 0) {
            total += (y + 3) / 4 - (y + 99) / 100 + (y + 399) / 400;
        } else {
            total -= y / -4 - y / -100 + y / -400;
        }
        total += ((367 * m - 362) / 12);
        total += getDayOfMonth() - 1;
        if (m > 2) {
            total--;
            if (isLeapYear() == false) {
                total--;
            }
        }
        return total - DAYS_0000_TO_1970;
    }

    @Override
    public Temporal adjustInto(Temporal temporal) {
        return temporal.with(EPOCH_DAY, toEpochDay()).with(NANO_OF_DAY, getNanoOfDay());
    }
}
