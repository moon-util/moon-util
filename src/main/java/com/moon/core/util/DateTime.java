package com.moon.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.MONTH;
import static java.util.Calendar.getInstance;

/**
 * @author moonsky
 */
public final class DateTime extends Date {

    private volatile Calendar calendar;

    public DateTime(long timeMillis) {
        super(timeMillis);
        Calendar calendar = getInstance();
        calendar.setTimeInMillis(timeMillis);
        this.calendar = calendar;
    }

    public DateTime(Calendar calendar) {
        super(calendar.getTimeInMillis());
        this.calendar = calendar;
    }

    public DateTime(Date date) {
        this((date == null ? new Date() : date).getTime());
    }

    public DateTime() { this(new Date()); }

    public DateTime(LocalDate localDate) {
        this(DateUtil.toCalendar(localDate));
    }

    public DateTime(LocalDateTime datetime) {
        this(DateUtil.toCalendar(datetime));
    }

    public int getYearValue() {
        return DateUtil.getYear(calendar);
    }

    public int getMonthValue() {
        return DateUtil.getMonth(calendar);
    }

    public int getDayOfYear() {
        return DateUtil.getDayOfYear(calendar);
    }

    public int getDayOfMonth() {
        return DateUtil.getDayOfMonth(calendar);
    }

    public int getDayOfWeek() {
        return DateUtil.getDayOfWeek(calendar);
    }

    public int getHourOfDay() {
        return DateUtil.getHour(calendar);
    }

    public int getMinuteOfHour() {
        return DateUtil.getMinute(calendar);
    }

    public int getSecondOfDay() {
        return getHourOfDay() * 3600 + getSecondOfHour();
    }

    public int getSecondOfHour() {
        return getMinuteOfHour() * 60 + getSecondOfMinute();
    }

    public int getSecondOfMinute() {
        return DateUtil.getSecond(calendar);
    }

    public int getMilliOfDay() {
        return getSecondOfDay() * 1000 + getMilliOfSecond();
    }

    public int getMilliOfHour() {
        return getSecondOfHour() * 1000 + getMilliOfSecond();
    }

    public int getMilliOfMinute() {
        return getSecondOfMinute() * 1000 + getMilliOfSecond();
    }

    public int getMilliOfSecond() {
        return DateUtil.getMillisecond(calendar);
    }

    /**
     * 是否是上午
     *
     * @return 24 小时制 12 点以前，返回 true，否则返回false
     */
    public boolean isAm() {
        return calendar.get(Calendar.AM_PM) == 0;
    }

    /**
     * 是否是下午
     *
     * @return 24 小时制 12 点以后，返回 true，否则返回false
     */
    public boolean isPm() {
        return calendar.get(Calendar.AM_PM) == 1;
    }

    /**
     * 设置秒数
     *
     * @param value 秒数
     *
     * @return 当前对象
     */
    public DateTime withSecond(int value) {
        return withValue(DateTimeField.SECOND, value);
    }

    /**
     * 设置分钟数
     *
     * @param value 分钟数
     *
     * @return 当前对象
     */
    public DateTime withMinute(int value) {
        return withValue(DateTimeField.MINUTE, value);
    }

    /**
     * 设置小时数（12 小时制）
     *
     * @param value 小时数
     *
     * @return 当前对象
     */
    public DateTime withHour(int value) {
        return withValue(DateTimeField.HOUR, value);
    }

    /**
     * 设置小时数（24 小时制）
     *
     * @param value 小时数
     *
     * @return 当前对象
     */
    public DateTime withHourOfDay(int value) {
        return withValue(DateTimeField.HOUR_OF_DAY, value);
    }

    /**
     * 设置当前周中的星期
     *
     * @param value 星期
     *
     * @return 当前对象
     */
    public DateTime withDayOfWeek(int value) {
        return withValue(DateTimeField.DAY_OF_WEEK, value);
    }

    /**
     * 设置当月的第几天
     *
     * @param value 日期
     *
     * @return 当前对象
     */
    public DateTime withDayOfMonth(int value) {
        return withValue(DateTimeField.DAY_OF_MONTH, value);
    }

    /**
     * 设置当前年的第几天
     *
     * @param value 天数
     *
     * @return 当前对象
     */
    public DateTime withDayOfYear(int value) {
        return withValue(DateTimeField.DAY_OF_YEAR, value);
    }

    /**
     * 设置当前月份
     *
     * @param value 月份
     *
     * @return 当前对象
     */
    public DateTime withMonth(int value) {
        return withValue(DateTimeField.MONTH, value);
    }

    /**
     * 设置年份
     *
     * @param value 年份
     *
     * @return 当前对象
     */
    public DateTime withYear(int value) {
        return withValue(DateTimeField.YEAR, value);
    }

    /**
     * 设置指定字段值
     *
     * @param field 字段
     * @param value 字段值
     *
     * @return 当前对象
     */
    public DateTime withValue(DateTimeField field, int value) {
        return withValue(field.value, value);
    }

    /**
     * 设置指定字段值
     *
     * @param field 字段
     * @param value 字段值
     *
     * @return 当前对象
     */
    public DateTime withValue(int field, int value) {
        calendar.set(field, field == MONTH ? value - 1 : value);
        return this;
    }
}
