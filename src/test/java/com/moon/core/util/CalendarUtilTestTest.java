package com.moon.core.util;

import com.moon.core.time.CalendarUtil;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

/**
 * @author moonsky
 */
class CalendarUtilTestTest {

    @Test
    void testGetDayOfYear() {
        Calendar calendar = CalendarUtil.nowCalendar();
        calendar = CalendarUtil.nextDay(calendar);
        System.out.println(CalendarUtil.getDayOfYear(calendar));
        System.out.println(CalendarUtil.getDayOfWeek(calendar));
    }
}