package com.moon.core.util;

import com.moon.core.time.CalendarUtil;
import com.moon.core.time.DateUtil;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

/**
 * @author moonsky
 */
class CalendarUtilTestTest {

    @Test
    void testGetDayOfYear() {
        Calendar calendar = CalendarUtil.getCalendar();
        calendar = CalendarUtil.nextDay(calendar);
        System.out.println(CalendarUtil.getDayOfYear(calendar));
        System.out.println(CalendarUtil.getDayOfWeek(calendar));
    }

    @Test
    void testParseToCalendar() throws Exception {
        String date = "2020";
        Calendar calendar = CalendarUtil.parseToCalendar(date);

        String formatted = CalendarUtil.format(calendar, "yyyy-MM-dd HH:mm:ss SSS");

        DateUtil.toDate("");
        System.out.println(formatted);
    }
}