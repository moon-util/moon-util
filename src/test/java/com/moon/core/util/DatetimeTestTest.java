package com.moon.core.util;

import com.moon.core.time.CalendarUtil;
import com.moon.core.time.DateTime;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author benshaoye
 */
class DatetimeTestTest {

    @Test
    void testOf() {
        Calendar calendar = CalendarUtil.getCalendar();
        DateTime datetime = new DateTime(calendar);
        for (DayOfWeek value : DayOfWeek.values()) {
            datetime.setFirstDayOfWeek(value);
            assertEquals(datetime.getFirstDayOfWeek(), value);
        }
        System.out.println("今天：" + datetime.toString() + "\t" + datetime.getDayOfWeek());
        datetime.minusDays(1);
        System.out.println("昨天：" + datetime.toString() + "\t" + datetime.getDayOfWeek());

        LocalDate.now().getMonth();
        System.out.println(datetime.getMonthValue());
        System.out.println(datetime.getMonthOfYear().getLastDayOfMonth(datetime.getYearValue()));


        LocalTime time = LocalTime.now();
        System.out.println(time.toNanoOfDay());
        DateTime datenow = DateTime.ofToday(time);
        System.out.println(datenow.getNanoOfDay());
        assertEquals(time.toNanoOfDay(), datenow.getNanoOfDay());
        String fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(DateTime.now());
        System.out.println(fmt);

        System.out.println("======================================");
        for (DayOfWeek value : DayOfWeek.values()) {
            datetime.withDayOfWeek(value);
            System.out.println(datetime.getDayOfWeekValue());
            assertEquals(value, datetime.getDayOfWeek());
        }

        System.out.println(DateTime.now().getDayOfWeek());

        String endOfMonth = DateTime.now().endOfMonth().toString();
        System.out.println(endOfMonth);
        DateTime dt = DateTime.ofFields(2000, 2, 12);
        System.out.println(dt.endOfMonth().toString());
    }
}