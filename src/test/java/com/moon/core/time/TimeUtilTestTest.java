package com.moon.core.time;

import com.moon.core.lang.ref.IntAccessor;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author moonsky
 */
class TimeUtilTestTest {

    @Test
    void testForEachYears() {
        final int yearValue = 2018;
        final int monthValue = 2;
        final int dayValue = 20;
        LocalDate date = LocalDate.of(yearValue, monthValue, dayValue);
        LocalDate end = date.plusDays(10);
        // DateTimeUtil.forEach(date, end, d -> d.plusDays(1), thisDate -> {
        //     System.out.println(thisDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        // });
        DateTimeUtil.reduce(date, end, d -> d.plusDays(1), (total, thisDate, idx) -> {
            total.add(DateTimeUtil.format(thisDate, "yyyy-MM-dd"));
            return total;
        }, new ArrayList()).forEach(dateStr -> {
            System.out.println(dateStr);
        });
    }

    @Test
    void testToDate() {
    }

    @Test
    void testToTime() {
    }

    @Test
    void testToDateTime() {
    }

    @Test
    void testToDate1() {
    }

    @Test
    void testToTime1() {
    }

    @Test
    void testToDateTime1() {
    }

    @Test
    void testIsBefore() {
    }

    @Test
    void testIsAfter() {
    }

    @Test
    void testForEachMonths() {
    }

    @Test
    void testForEachDays() {
    }

    @Test
    void testForEachHours() {
        final int hourValue = 23;
        final int minuteValue = 2;
        final int secondValue = 20;
        LocalTime time = LocalTime.of(hourValue, minuteValue, secondValue);
        IntAccessor interTime = IntAccessor.of();
    }

    @Test
    void testForEachMinutes() {
    }

    @Test
    void testForEachSeconds() {
    }
}