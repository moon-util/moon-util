package com.moon.core.time;

import com.moon.core.enums.EnumDescriptor;

import java.util.Calendar;

/**
 * @author moonsky
 */
public enum DatetimeField implements EnumDescriptor {
    /**
     * 毫秒
     */
    MILLISECOND(Calendar.MILLISECOND),
    /**
     * 秒
     */
    SECOND(Calendar.SECOND),
    /**
     * 分钟
     */
    MINUTE(Calendar.MINUTE),
    /**
     * 24 小时制小时数
     */
    HOUR_OF_DAY(Calendar.HOUR_OF_DAY),
    /**
     * 12 小时制小时数
     */
    HOUR(Calendar.HOUR),
    /**
     * 星期
     */
    DAY_OF_WEEK(Calendar.DAY_OF_WEEK),
    /**
     * 日期
     */
    DAY_OF_MONTH(Calendar.DAY_OF_MONTH),
    /**
     * 天数
     */
    DAY_OF_YEAR(Calendar.DAY_OF_YEAR),
    /**
     * 第几周
     */
    WEEK_OF_MONTH(Calendar.WEEK_OF_MONTH),
    /**
     * 第几周
     */
    WEEK_OF_YEAR(Calendar.WEEK_OF_YEAR),
    /**
     * 月份
     */
    MONTH(Calendar.MONTH),
    /**
     * 年份
     */
    YEAR(Calendar.YEAR),
    ;
    public final int value;

    DatetimeField(int value) { this.value = value; }

    /**
     * 枚举信息
     *
     * @return 枚举信息
     */
    @Override
    public String getText() { return name(); }
}
