package com.moon.core.util;

import java.io.Serializable;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static java.util.Calendar.getInstance;

/**
 * @author moonsky
 */
public final class DateTime extends Date implements Cloneable, Serializable {

    private volatile Calendar calendar;
    private TimeZone timeZone;

    public DateTime(long timeMillis) {
        super(timeMillis);
        Calendar calendar = getInstance();
        calendar.setTimeInMillis(timeMillis);
        this.calendar = calendar;
        timeZone = calendar.getTimeZone();
    }

    public DateTime(Calendar calendar) {
        super(calendar.getTimeInMillis());
        this.calendar = calendar;
        this.timeZone = calendar.getTimeZone();
    }

    public DateTime(Date date) {
        this((date == null ? new Date() : date).getTime());
    }

    @SuppressWarnings("all")
    public DateTime() { this(new Date().getTime()); }
}
