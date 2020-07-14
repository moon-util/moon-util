package com.moon.core.util;

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
        Calendar calendar = CalendarUtil.nowCalendar();
        Datetime datetime = new Datetime(calendar);
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
        Datetime datenow = Datetime.ofToday(time);
        System.out.println(datenow.getNanoOfDay());
        assertEquals(time.toNanoOfDay(), datenow.getNanoOfDay());
        String fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(Datetime.now());
        System.out.println(fmt);

        System.out.println("======================================");
        for (DayOfWeek value : DayOfWeek.values()) {
            datetime.withDayOfWeek(value);
            System.out.println(datetime.getDayOfWeekValue());
            assertEquals(value, datetime.getDayOfWeek());
        }

        System.out.println(Datetime.now().getDayOfWeek());

        String endOfMonth = Datetime.now().endOfMonth().toString();
        System.out.println(endOfMonth);
        Datetime dt = Datetime.of(2000, 2, 12);
        System.out.println(dt.endOfMonth().toString());
    }
}