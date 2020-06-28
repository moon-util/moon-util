package com.moon.core.util;

import com.moon.core.time.DateTimeUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class CalendarUtilTestTest {

    @Test
    void testGetDayOfYear() {
        Calendar calendar = CalendarUtil.nowCalendar();
        calendar = CalendarUtil.nextDay(calendar);
        System.out.println(CalendarUtil.getDayOfYear(calendar));
        System.out.println(DateTimeUtil.getDayOfYear(LocalDate.now()));
        System.out.println(DateTimeUtil.getDayOfWeek(LocalDate.now()));
        System.out.println(CalendarUtil.getDayOfWeek(calendar));
    }
}