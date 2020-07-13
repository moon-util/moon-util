package com.moon.core.util;

import java.util.Calendar;

/**
 * @author moonsky
 */
public enum DateTimeField {
    MILLISECOND(Calendar.MILLISECOND),
    SECOND(Calendar.SECOND),
    MINUTE(Calendar.MINUTE),
    HOUR_OF_DAY(Calendar.HOUR_OF_DAY),
    HOUR(Calendar.HOUR),
    DAY_OF_WEEK(Calendar.DAY_OF_WEEK),
    DAY_OF_MONTH(Calendar.DAY_OF_MONTH),
    DAY_OF_YEAR(Calendar.DAY_OF_YEAR),
    WEEK_OF_MONTH(Calendar.WEEK_OF_MONTH),
    WEEK_OF_YEAR(Calendar.WEEK_OF_YEAR),
    MONTH(Calendar.MONTH),
    YEAR(Calendar.YEAR),
    ;
    public final int value;

    DateTimeField(int value) {
        this.value = value;
    }
}
