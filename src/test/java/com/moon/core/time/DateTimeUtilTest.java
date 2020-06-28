package com.moon.core.time;

import com.moon.core.enums.Const;
import com.moon.core.util.DateUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author benshaoye
 */
public class DateTimeUtilTest {

    public DateTimeUtilTest() {}

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtil.yyyy_MM_dd_HH_mm_ss_SSS);
    static DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(Const.PATTERN_DATE);

    @Test
    void testStartingOfMonth() {
        LocalDateTime datetime = LocalDateTime.now();
        System.out.println(DateTimeUtil.format(datetime, Const.PATTERN));
        System.out.println(DateTimeUtil.format(DateTimeUtil.startingOfMonth(datetime), Const.PATTERN));
        System.out.println(DateTimeUtil.endingOfYear(datetime).format(formatter));
        System.out.println(DateTimeUtil.endingOfMonth(datetime).format(formatter));
        System.out.println(DateTimeUtil.startingOfDay(datetime).format(formatter));
        System.out.println(DateTimeUtil.endingOfYear(datetime.toLocalDate()).format(patternDate));
        System.out.println(DateTimeUtil.endingOfDay(datetime).format(formatter));
        System.out.println(DateTimeUtil.endingOfYear(datetime).getNano());
        System.out.println(DateTimeUtil.startingOfDay(datetime).getNano());
    }

    @Test
    void testNow() throws Exception {
    }

    @Test
    void testNowDate() throws Exception {
    }

    @Test
    void testNowTime() throws Exception {
    }

    @Test
    void testNowDateTime() throws Exception {
    }

    @Test
    void testNowIfNull() throws Exception {
    }

    @Test
    void testTestNowIfNull() throws Exception {
    }

    @Test
    void testGetYear() throws Exception {
    }

    @Test
    void testGetMonthValue() throws Exception {
    }

    @Test
    void testGetDayOfMonth() throws Exception {
    }

    @Test
    void testGetDayOfYear() throws Exception {
        System.out.println(DateTimeUtil.getDayOfYear(LocalDate.now()));
        System.out.println(DateTimeUtil.getDayOfMonth(LocalDate.now()));
        System.out.println(DateTimeUtil.getMonthValue(LocalDate.now()));
        System.out.println(LocalDate.now().getMonth().getValue());
    }

    @Test
    void testGetDayOfWeek() throws Exception {
    }

    @Test
    void testTestGetYear() throws Exception {
    }

    @Test
    void testTestGetMonthValue() throws Exception {
    }

    @Test
    void testTestGetDayOfMonth() throws Exception {
    }

    @Test
    void testTestGetDayOfYear() throws Exception {
    }

    @Test
    void testTestGetDayOfWeek() throws Exception {
    }

    @Test
    void testStartingOfYear() throws Exception {
    }

    @Test
    void testTestStartingOfYear() throws Exception {
    }

    @Test
    void testStartingOfDay() throws Exception {
    }

    @Test
    void testStartingOfHour() throws Exception {
    }

    @Test
    void testTestStartingOfHour() throws Exception {
    }

    @Test
    void testStartingOfMinute() throws Exception {
    }

    @Test
    void testTestStartingOfMinute() throws Exception {
    }

    @Test
    void testStartingOfSecond() throws Exception {
    }

    @Test
    void testTestStartingOfSecond() throws Exception {
    }

    @Test
    void testEndingOfYear() throws Exception {
    }

    @Test
    void testTestEndingOfYear() throws Exception {
    }

    @Test
    void testEndingOfMonth() throws Exception {
    }

    @Test
    void testTestEndingOfMonth() throws Exception {
    }

    @Test
    void testEndingOfDay() throws Exception {
    }

    @Test
    void testEndingOfHour() throws Exception {
    }

    @Test
    void testTestEndingOfHour() throws Exception {
    }

    @Test
    void testEndingOfMinute() throws Exception {
    }

    @Test
    void testTestEndingOfMinute() throws Exception {
    }

    @Test
    void testEndingOfSecond() throws Exception {
    }

    @Test
    void testTestEndingOfSecond() throws Exception {
    }

    @Test
    void testFormat() throws Exception {
    }

    @Test
    void testTestFormat() throws Exception {
    }

    @Test
    void testSafeToDateTime() throws Exception {
    }

    @Test
    void testTestSafeToDateTime() throws Exception {
    }

    @Test
    void testTestSafeToDateTime1() throws Exception {
    }

    @Test
    void testSafeToInstant() throws Exception {
    }

    @Test
    void testTestSafeToInstant() throws Exception {
    }

    @Test
    void testToInstant() throws Exception {
    }

    @Test
    void testTestToInstant() throws Exception {
    }

    @Test
    void testTestToInstant1() throws Exception {
    }

    @Test
    void testToDate() throws Exception {
    }

    @Test
    void testToTime() throws Exception {
    }

    @Test
    void testToDateTime() throws Exception {
    }

    @Test
    void testTestToDate() throws Exception {
    }

    @Test
    void testTestToTime() throws Exception {
    }

    @Test
    void testTestToDateTime() throws Exception {
    }

    @Test
    void testTestToDateTime1() throws Exception {
    }

    @Test
    void testTestToDate1() throws Exception {
    }

    @Test
    void testTestToTime1() throws Exception {
    }

    @Test
    void testTestToDateTime2() throws Exception {
    }

    @Test
    void testTestToDate2() throws Exception {
    }

    @Test
    void testTestToTime2() throws Exception {
    }

    @Test
    void testTestToDateTime3() throws Exception {
    }

    @Test
    void testTestToDate3() throws Exception {
    }

    @Test
    void testTestToTime3() throws Exception {
    }

    @Test
    void testTestToDateTime4() throws Exception {
    }

    @Test
    void testTestToDate4() throws Exception {
    }

    @Test
    void testTestToTime4() throws Exception {
    }

    @Test
    void testTestToDateTime5() throws Exception {
    }

    @Test
    void testForEachYears() throws Exception {
    }

    @Test
    void testForEachMonths() throws Exception {
    }

    @Test
    void testForEachDays() throws Exception {
    }

    @Test
    void testForEachHours() throws Exception {
    }

    @Test
    void testForEachMinutes() throws Exception {
    }

    @Test
    void testForEachSeconds() throws Exception {
    }
}
