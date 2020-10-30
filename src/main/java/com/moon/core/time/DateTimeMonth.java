package com.moon.core.time;

import com.moon.core.enums.EnumDescriptor;
import com.moon.core.lang.StringUtil;

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
public enum DateTimeMonth implements TemporalAccessor, TemporalAdjuster, EnumDescriptor {
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

    private final static DateTimeMonth[] MONTHS = values();

    private final int calendarValue;
    private final Month month;
    private final String ChineseText;

    DateTimeMonth(int month, String ChineseText) {
        this.ChineseText = ChineseText;
        this.calendarValue = month;
        this.month = Month.of(getValue());
    }

    /**
     * 获取季节
     *
     * @return 季节
     */
    public Season toSeason() { return Season.ofMonth(getValue()); }

    /**
     * 获取月份
     *
     * @return 月份
     */
    public Month getMonth() { return month; }

    /**
     * 获取月份值
     *
     * @return 月份值
     */
    public int getValue() { return ordinal() + 1; }

    /**
     * 中文名称
     *
     * @return
     */
    public String getChineseText() { return ChineseText; }

    /**
     * 短中文名称
     *
     * @return
     */
    public String getChineseShortText() { return month + "月"; }

    /**
     * 英文名称
     *
     * @return
     */
    public String getEnglishText() { return StringUtil.capitalize(name().toLowerCase()); }

    /**
     * 短名称，英文前三位
     *
     * @return
     */
    public String getEnglishShortText() { return name().substring(0, 3); }

    public static DateTimeMonth of(int month) {
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
