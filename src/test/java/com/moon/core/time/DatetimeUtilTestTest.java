package com.moon.core.time;

import com.moon.core.util.DateUtil;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

/**
 * @author benshaoye
 */
class DatetimeUtilTestTest {

    @Test
    void testToDateTime() {
        Date date = new Date();
        System.out.println(DateTimeUtil.format(DateTimeUtil.toDateTime(date), DateUtil.yyyy_MM_dd_HH_mm_ss_SSS));

        long milliseconds = date.getTime();
        Instant instant = Instant.ofEpochMilli(milliseconds);
        System.out.println(DateTimeUtil.format(DateTimeUtil.toDateTime(instant), DateUtil.yyyy_MM_dd_HH_mm_ss_SSS));
    }

}