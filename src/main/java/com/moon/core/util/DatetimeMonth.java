package com.moon.core.util;

import com.moon.core.enums.EnumDescriptor;

import java.time.DateTimeException;
import java.time.Month;
import java.time.Year;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.util.Calendar;

/**
 * @author benshaoye
 * @see Month
 * @see java.util.Calendar
 * @see java.util.Calendar#JANUARY
 */
public enum DatetimeMonth implements TemporalAccessor, TemporalAdjuster, EnumDescriptor {
    /**
     * 一月
     */
    JANUARY(Calendar.JANUARY, "一月"),


    FEBRUARY(Calendar.FEBRUARY, "二月"),


    MARCH(Calendar.MARCH, "三月"),


    APRIL(Calendar.APRIL, "四月"),


    MAY(Calendar.MAY, "五月"),


    JUNE(Calendar.JUNE, "六月"),


    JULY(Calendar.JULY, "七月"),


    AUGUST(Calendar.AUGUST, "八月"),


    SEPTEMBER(Calendar.SEPTEMBER, "九月"),


    OCTOBER(Calendar.OCTOBER, "十月"),


    NOVEMBER(Calendar.NOVEMBER, "十一月"),


    DECEMBER(Calendar.DECEMBER, "十二月");

    private final static DatetimeMonth[] MONTHS = values();

    private final int calendarValue;
    private final Month month;
    private final String ChineseText;

    DatetimeMonth(int month, String ChineseText) {
        this.ChineseText = ChineseText;
        this.calendarValue = month;
        this.month = Month.of(getValue());
    }

    public Month getMonth() { return month; }

    public int getValue() { return ordinal() + 1; }

    public String getShortName() { return name().substring(0, 3); }

    public String getChineseText() { return ChineseText; }

    public String getShortChineseText() { return month + "月"; }

    public static DatetimeMonth of(int month) {
        if (month < 1 || month > 12) {
            throw new DateTimeException("Invalid value for MonthOfYear: " + month);
        }
        return MONTHS[month - 1];
    }

    /**
     * 返回当月最后一天
     *
     * @param year 年份，用以区别是否闰年
     *
     * @return 当月最后一天
     *
     * @see Month#length(boolean)
     */
    public int getLastDayOfMonth(int year) {
        return getMonth().length(Year.isLeap(year));
    }

    /**
     * 返回指定年份的当月第一天是指定年份的第 N 天
     *
     * @param year 年份
     *
     * @return 当月第一天是给定年份的第 N 天
     */
    public int getFirstDayOfYear(int year) {
        return getMonth().firstDayOfYear(Year.isLeap(year));
    }

    /**
     * 返回指定年份的当月最后一天是指定年份的第 N 天
     *
     * @param year 年份
     *
     * @return 当月最后一天是给定年份的第 N 天
     */
    public int getLastDayOfYear(int year) {
        Month month = getMonth();
        boolean isLeap = Year.isLeap(year);
        return month.firstDayOfYear(isLeap) + month.length(isLeap);
    }

    /**
     * copied from {@link Month}
     *
     * @param field TemporalField
     *
     * @return true|false
     */
    @Override
    public boolean isSupported(TemporalField field) {
        return getMonth().isSupported(field);
    }

    /**
     * copied from {@link Month}
     *
     * @param field TemporalField
     *
     * @return long value
     */
    @Override
    public long getLong(TemporalField field) {
        return getMonth().getLong(field);
    }

    /**
     * copied from {@link Month}
     *
     * @param temporal Temporal
     *
     * @return Temporal
     */
    @Override
    public Temporal adjustInto(Temporal temporal) {
        return getMonth().adjustInto(temporal);
    }

    @Override
    public String getText() { return name(); }
}
