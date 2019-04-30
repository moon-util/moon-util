package com.moon.core.time;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.util.require.Requires;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author benshaoye
 */
class TimeUtilTestTest {
    static final Requires REQUIRES = Requires.of();

    @Test
    void testForEachYears() {
        final int yearValue = 2018;
        final int monthValue = 2;
        final int dayValue = 20;
        LocalDate date = LocalDate.of(yearValue, monthValue, dayValue);
        IntAccessor interDate = IntAccessor.of();
        TimeUtil.forEachYears(date, date.plusYears(3), (year, localDate) -> {
            REQUIRES.requireEq(year, interDate.get() + yearValue);
            REQUIRES.requireEquals(localDate, LocalDate.of(yearValue + interDate.getAndIncrement(), monthValue, dayValue));
            return true;
        });
        LocalTime time = LocalTime.now();
        time.getHour();
        LocalDateTime dateTime = LocalDateTime.now();
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
        TimeUtil.forEachHours(time, time.plusHours(3), (hour, localTime) -> {
            System.out.println(localTime);
            return true;
        });
    }

    @Test
    void testForEachMinutes() {
    }

    @Test
    void testForEachSeconds() {
    }
}