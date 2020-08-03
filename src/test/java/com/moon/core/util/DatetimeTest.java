package com.moon.core.util;

import com.moon.core.time.CalendarUtil;
import com.moon.core.time.Datetime;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
public class DatetimeTest {

    public DatetimeTest() {
        // Datetime.now().
    }

    @Test
    void testNow() throws Exception {
        System.out.println(Datetime.now());
        System.out.println(CalendarUtil.get(CalendarUtil.nowCalendar(), Calendar.ERA));
    }

    @Test
    void testOf() throws Exception {
        System.out.println(Datetime.of());
    }

    @Test
    void testTestOf() throws Exception {
        Datetime datetime = Datetime.of("2020-10-24 15:23:14");
        assertEquals(datetime.startOfMinute().toString(), "2020-10-24 15:23:00");
        assertEquals(datetime.plusSeconds(15).toString(), "2020-10-24 15:23:15");
        assertEquals(datetime.startOfHour().toString(), "2020-10-24 15:00:00");
    }

    @Test
    void testTestOf1() throws Exception {
    }

    @Test
    void testTestOf2() throws Exception {
    }

    @Test
    void testOfToday() throws Exception {
    }

    @Test
    void testIsImmutable() throws Exception {
    }

    @Test
    void testAsImmutable() throws Exception {
    }

    @Test
    void testAsMutable() throws Exception {
    }

    @Test
    void testGetYearLength() throws Exception {
        assertTrue(Datetime.of(2020, 12, 30).isLeapYear());
        assertTrue(Datetime.of(2020, 12, 30).getYearLength() == 366);
        assertTrue(Datetime.of(2016, 12, 30).isLeapYear());
        assertTrue(Datetime.of(2000, 12, 30).isLeapYear());
        assertFalse(Datetime.of(1900, 12, 30).isLeapYear());
    }

    @Test
    void testGetYearValue() throws Exception {
        assertTrue(Datetime.of(2020, 12, 30).isLeapYear());
        assertTrue(Datetime.of(2016, 12, 30).isLeapYear());
        assertTrue(Datetime.of(2000, 12, 30).isLeapYear());
        assertFalse(Datetime.of(1900, 12, 30).isLeapYear());
    }

    @Test
    void testGetQuarterValue() throws Exception {
    }

    @Test
    void testGetMonthValue() throws Exception {
    }

    @Test
    void testGetMonthOfYear() throws Exception {
    }

    @Test
    void testGetWeekOfYear() throws Exception {
    }

    @Test
    void testGetWeekOfMonth() throws Exception {
    }

    @Test
    void testGetDayOfYear() throws Exception {
    }

    @Test
    void testGetDayOfMonth() throws Exception {
    }

    @Test
    void testGetDayOfWeekValue() throws Exception {
    }

    @Test
    void testGetDayOfWeek() throws Exception {
    }

    @Test
    void testGetHourOfDay() throws Exception {
    }

    @Test
    void testGetMinuteOfDay() throws Exception {
    }

    @Test
    void testGetMinuteOfHour() throws Exception {
    }

    @Test
    void testGetSecondOfDay() throws Exception {
    }

    @Test
    void testGetSecondOfHour() throws Exception {
    }

    @Test
    void testGetSecondOfMinute() throws Exception {
    }

    @Test
    void testGetMillisOfDay() throws Exception {
    }

    @Test
    void testGetMillisOfHour() throws Exception {
    }

    @Test
    void testGetMillisOfMinute() throws Exception {
    }

    @Test
    void testGetMillisOfSecond() throws Exception {
    }

    @Test
    void testGetNanoOfDay() throws Exception {
    }

    @Test
    void testGetNanoOfSecond() throws Exception {
    }

    @Test
    void testGetTime() throws Exception {
    }

    @Test
    void testGetField() throws Exception {
    }

    @Test
    void testGet() throws Exception {
        Datetime datetime = Datetime.now();
        System.out.println(datetime);
        // datetime.with(ChronoField.DAY_OF_MONTH, 20);
        for (ChronoField value : ChronoField.values()) {
            System.out.println("case ChronoField." + value.name() + ":");
        }
    }

    @Test
    void testWithTimeInMillis() throws Exception {
        Datetime datetime = Datetime.now();
        LocalDateTime dateTime0 = datetime.toLocalDateTime();
        System.out.println(dateTime0);
        LocalDateTime dateTime1 = dateTime0.with(ChronoField.DAY_OF_MONTH, 12);
        System.out.println(dateTime1);
        System.out.println(dateTime1.getDayOfMonth());
        System.out.println(Datetime.of(dateTime1));
    }

    @Test
    void testWithSecond() throws Exception {
    }

    @Test
    void testWithMinute() throws Exception {
    }

    @Test
    void testWithHour() throws Exception {
    }

    @Test
    void testWithHourOfDay() throws Exception {
    }

    @Test
    void testWithDayOfWeek() throws Exception {
    }

    @Test
    void testTestWithDayOfWeek() throws Exception {
    }

    @Test
    void testWithDayOfMonth() throws Exception {
    }

    @Test
    void testWithDayOfYear() throws Exception {
    }

    @Test
    void testWithMonth() throws Exception {
    }

    @Test
    void testTestWithMonth() throws Exception {
    }

    @Test
    void testTestWithMonth1() throws Exception {
    }

    @Test
    void testWithYear() throws Exception {
    }

    @Test
    void testWithField() throws Exception {
    }

    @Test
    void testWith() throws Exception {
    }

    @Test
    void testGetFirstDayOfWeek() throws Exception {
    }

    @Test
    void testGetFirstDayOfWeekValue() throws Exception {
    }

    @Test
    void testSetFirstDayOfWeek() throws Exception {
    }

    @Test
    void testTestSetFirstDayOfWeek() throws Exception {
    }

    @Test
    void testPlusYears() throws Exception {
    }

    @Test
    void testPlusMonths() throws Exception {
    }

    @Test
    void testPlusWeeks() throws Exception {
    }

    @Test
    void testPlusDays() throws Exception {
    }

    @Test
    void testPlusHours() throws Exception {
    }

    @Test
    void testPlusMinutes() throws Exception {
    }

    @Test
    void testPlusSeconds() throws Exception {
    }

    @Test
    void testPlusMillis() throws Exception {
    }

    @Test
    void testPlusField() throws Exception {
    }

    @Test
    void testPlus() throws Exception {
    }

    @Test
    void testMinusYears() throws Exception {
    }

    @Test
    void testMinusMonths() throws Exception {
    }

    @Test
    void testMinusWeeks() throws Exception {
    }

    @Test
    void testMinusDays() throws Exception {
    }

    @Test
    void testMinusHours() throws Exception {
    }

    @Test
    void testMinusMinutes() throws Exception {
    }

    @Test
    void testMinusSeconds() throws Exception {
    }

    @Test
    void testMinusMillis() throws Exception {
    }

    @Test
    void testMinusField() throws Exception {
    }

    @Test
    void testMinus() throws Exception {
    }

    @Test
    void testStartOfSecond() throws Exception {
    }

    @Test
    void testStartOfMinute() throws Exception {
    }

    @Test
    void testStartOfHour() throws Exception {
    }

    @Test
    void testStartOfDay() throws Exception {
    }

    @Test
    void testStartOfWeek() throws Exception {
    }

    @Test
    void testTestStartOfWeek() throws Exception {
        Datetime datetime = Datetime.of(2020, 7, 25, 10, 29, 25);
        System.out.println(datetime);
        System.out.println(datetime.startOfWeek());
        System.out.println(datetime.endOfWeek());

        datetime = Datetime.of(2020, 7, 25, 10, 29, 25);
        System.out.println(datetime.startOfWeek(DayOfWeek.SUNDAY));
        System.out.println(datetime.endOfWeek(DayOfWeek.SUNDAY));

        datetime = Datetime.of(2020, 7, 25, 10, 29, 25);
        System.out.println(datetime.startOfWeek(DayOfWeek.TUESDAY));
        System.out.println(datetime.endOfWeek(DayOfWeek.TUESDAY));
    }

    @Test
    void testStartOfMonth() throws Exception {
    }

    @Test
    void testStartOfYear() throws Exception {
    }

    @Test
    void testEndOfSecond() throws Exception {
    }

    @Test
    void testEndOfMinute() throws Exception {
    }

    @Test
    void testEndOfHour() throws Exception {
    }

    @Test
    void testEndOfDay() throws Exception {
    }

    @Test
    void testEndOfWeek() throws Exception {
    }

    @Test
    void testTestEndOfWeek() throws Exception {
    }

    @Test
    void testEndOfMonth() throws Exception {
    }

    @Test
    void testEndOfYear() throws Exception {
    }

    @Test
    void testIsAm() throws Exception {
    }

    @Test
    void testIsPm() throws Exception {
    }

    @Test
    void testIsWeekday() throws Exception {
    }

    @Test
    void testIsWeekend() throws Exception {
    }

    @Test
    void testIsLeapYear() throws Exception {
    }

    @Test
    void testIsYearOf() throws Exception {
    }

    @Test
    void testIsYearBefore() throws Exception {
    }

    @Test
    void testIsYearAfter() throws Exception {
    }

    @Test
    void testTestIsYearOf() throws Exception {
    }

    @Test
    void testTestIsYearBefore() throws Exception {
    }

    @Test
    void testTestIsYearAfter() throws Exception {
    }

    @Test
    void testIsMonthOf() throws Exception {
    }

    @Test
    void testIsMonthBefore() throws Exception {
    }

    @Test
    void testIsMonthAfter() throws Exception {
    }

    @Test
    void testTestIsMonthOf() throws Exception {
    }

    @Test
    void testTestIsMonthBefore() throws Exception {
    }

    @Test
    void testTestIsMonthAfter() throws Exception {
    }

    @Test
    void testIsDateOf() throws Exception {
    }

    @Test
    void testIsDateBefore() throws Exception {
    }

    @Test
    void testIsDateAfter() throws Exception {
    }

    @Test
    void testTestIsDateOf() throws Exception {
    }

    @Test
    void testTestIsDateBefore() throws Exception {
    }

    @Test
    void testTestIsDateAfter() throws Exception {
    }

    @Test
    void testIsHourOf() throws Exception {
    }

    @Test
    void testIsHourBefore() throws Exception {
    }

    @Test
    void testIsHourAfter() throws Exception {
    }

    @Test
    void testTestIsHourOf() throws Exception {
    }

    @Test
    void testTestIsHourBefore() throws Exception {
    }

    @Test
    void testTestIsHourAfter() throws Exception {
    }

    @Test
    void testIsMinuteOf() throws Exception {
    }

    @Test
    void testIsMinuteBefore() throws Exception {
    }

    @Test
    void testIsMinuteAfter() throws Exception {
    }

    @Test
    void testTestIsMinuteOf() throws Exception {
    }

    @Test
    void testTestIsMinuteBefore() throws Exception {
    }

    @Test
    void testTestIsMinuteAfter() throws Exception {
    }

    @Test
    void testIsSecondOf() throws Exception {
    }

    @Test
    void testIsSecondBefore() throws Exception {
    }

    @Test
    void testIsSecondAfter() throws Exception {
    }

    @Test
    void testTestIsSecondOf() throws Exception {
    }

    @Test
    void testTestIsSecondBefore() throws Exception {
    }

    @Test
    void testTestIsSecondAfter() throws Exception {
    }

    @Test
    void testIsDatetimeOf() throws Exception {
    }

    @Test
    void testIsDatetimeBefore() throws Exception {
    }

    @Test
    void testIsDatetimeAfter() throws Exception {
    }

    @Test
    void testTestIsDatetimeOf() throws Exception {
    }

    @Test
    void testTestIsDatetimeBefore() throws Exception {
    }

    @Test
    void testTestIsDatetimeAfter() throws Exception {
    }

    @Test
    void testTestIsDatetimeOf1() throws Exception {
    }

    @Test
    void testTestIsDatetimeBefore1() throws Exception {
    }

    @Test
    void testTestIsDatetimeAfter1() throws Exception {
    }

    @Test
    void testToInstant() throws Exception {
    }

    @Test
    void testToCalendar() throws Exception {
    }

    @Test
    void testToDate() throws Exception {
    }

    @Test
    void testToSqlDate() throws Exception {
    }

    @Test
    void testToTimestamp() throws Exception {
    }

    @Test
    void testToLocalTime() throws Exception {
    }

    @Test
    void testToLocalDate() throws Exception {
    }

    @Test
    void testToLocalDateTime() throws Exception {
    }

    @Test
    void testToJodaLocalDateTime() throws Exception {
    }

    @Test
    void testToJodaDateTime() throws Exception {
    }

    @Test
    void testToJodaLocalDate() throws Exception {
    }

    @Test
    void testToJodaLocalTime() throws Exception {
    }

    @Test
    void testSetTime() throws Exception {
    }

    @Test
    void testBefore() throws Exception {
    }

    @Test
    void testAfter() throws Exception {
    }

    @Test
    void testCompareTo() throws Exception {
    }

    @Test
    void testTestHashCode() throws Exception {
    }

    @Test
    void testTestEquals() throws Exception {
    }

    @Test
    void testTestClone() throws Exception {
    }

    @Test
    void testTestToString() throws Exception {
    }

    @Test
    void testTestToString1() throws Exception {
    }

    @Test
    void testGetMonth() throws Exception {
    }

    @Test
    void testGetYear() throws Exception {
    }

    @Test
    void testSetYear() throws Exception {
    }

    @Test
    void testSetMonth() throws Exception {
    }

    @Test
    void testGetDate() throws Exception {
    }

    @Test
    void testSetDate() throws Exception {
    }

    @Test
    void testGetDay() throws Exception {
    }

    @Test
    void testGetHours() throws Exception {
    }

    @Test
    void testSetHours() throws Exception {
    }

    @Test
    void testGetMinutes() throws Exception {
    }

    @Test
    void testSetMinutes() throws Exception {
    }

    @Test
    void testGetSeconds() throws Exception {
    }

    @Test
    void testSetSeconds() throws Exception {
    }

    @Test
    void testToLocaleString() throws Exception {
    }

    @Test
    void testToGMTString() throws Exception {
    }

    @Test
    void testGetTimezoneOffset() throws Exception {
    }

    @Test
    void testIsSupported() throws Exception {
    }

    @Test
    void testGetLong() throws Exception {
    }

    @Test
    void testToEpochDay() throws Exception {
    }

    @Test
    void testAdjustInto() throws Exception {
    }
}
