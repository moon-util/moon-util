package com.moon.core.util;

import org.junit.jupiter.api.Test;

import java.util.Calendar;

/**
 * @author moonsky
 */
class DateTimeFieldTestTest {

    @Test
    void testName() throws Exception {
        Calendar calendar = DateUtil.nowCalendar();
        System.out.println(calendar.get(Calendar.HOUR));
        System.out.println(calendar.get(Calendar.AM));
        System.out.println(calendar.get(Calendar.AM_PM));
        System.out.println(calendar.get(Calendar.PM));
        System.out.println("=============");
        calendar = DateUtil.setHourOfDay(calendar, 13);
        System.out.println(calendar.get(Calendar.HOUR));
        System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
        System.out.println(calendar.get(Calendar.MILLISECOND));
        System.out.println(calendar.get(Calendar.AM));
        System.out.println(calendar.get(Calendar.AM_PM));

    }
}