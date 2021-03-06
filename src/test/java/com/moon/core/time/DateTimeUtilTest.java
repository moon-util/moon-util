package com.moon.core.time;

import com.moon.core.enums.Const;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author moonsky
 */
public class DateTimeUtilTest {

    public DateTimeUtilTest() {}

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtil.yyyy_MM_dd_HH_mm_ss_SSS);
    static DateTimeFormatter patternDate = DateTimeFormatter.ofPattern(Const.PATTERN_DATE);

    @Test
    void testStartingOfMonth() {
        LocalDateTime datetime = LocalDateTime.now();
        System.out.println(DateTimeUtil.format(datetime, Const.PATTERN));
        System.out.println(DateTimeUtil.format(DateTimeUtil.startOfMonth(datetime), Const.PATTERN));
        System.out.println(DateTimeUtil.endOfYear(datetime).format(formatter));
        System.out.println(DateTimeUtil.endOfMonth(datetime).format(formatter));
        System.out.println(DateTimeUtil.startOfDay(datetime).format(formatter));
        System.out.println(DateTimeUtil.endOfYear(datetime.toLocalDate()).format(patternDate));
        System.out.println(DateTimeUtil.endOfDay(datetime).format(formatter));
        System.out.println(DateTimeUtil.endOfYear(datetime).getNano());
        System.out.println(DateTimeUtil.startOfDay(datetime).getNano());
    }

    @Test
    void testNow() throws Exception {
    }

    @Test
    void testNowDate() throws Exception {
        List<String> formatted = DateTimeUtil.reduceMonths(LocalDate.now(),
            LocalDate.now().plusYears(1),
            (total, date, idx) -> {
                total.add(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date));
                return total;
            },
            new ArrayList());
        Assertions.assertEquals(formatted.size(), 12);
    }

    @Test
    void testNowTime() throws Exception {
        Map resultMap = Stream.of(1, 2, 3, 4, 5, 6).reduce(new HashMap<>(16), (map, kv) -> {
            return map;
        }, (hashMap, hashMap2) -> {
            return hashMap;
        });
        System.out.println(resultMap);
    }

    @Test
    void testNowDateTime() throws Exception {
        String body = Jsoup.connect("http://60.205.182.215/cyhr/main/wechat/registerBaseInfo/register-invite.jsp").get()
            // .body()
            .outerHtml();
        System.out.println(body);
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
        LocalDate now = DateTimeUtil.toDate(2020, 06, 29);
        LocalDate startOfWeek = DateTimeUtil.startOfWeek(now, DayOfWeek.SATURDAY);
        LocalDate endOfWeek = DateTimeUtil.endOfWeek(now, DayOfWeek.SATURDAY);
        System.out.println(DateTimeUtil.format(startOfWeek, "yyyy-MM-dd"));
        System.out.println(DateTimeUtil.format(endOfWeek, "yyyy-MM-dd"));
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

    @Test
    void testIsYearEquals() throws Exception {
    }

    @Test
    void testIsYearBefore() throws Exception {
    }

    @Test
    void testIsYearAfter() throws Exception {
    }

    @Test
    void testIsMonthEquals() throws Exception {
    }

    @Test
    void testIsMonthBefore() throws Exception {
    }

    @Test
    void testIsMonthAfter() throws Exception {
    }

    @Test
    void testIsDateEquals() throws Exception {
    }

    @Test
    void testIsDateBefore() throws Exception {
    }

    @Test
    void testIsDateAfter() throws Exception {
    }

    @Test
    void testIsHourEquals() throws Exception {
    }

    @Test
    void testTestIsHourEquals() throws Exception {
    }

    @Test
    void testIsHourBefore() throws Exception {
    }

    @Test
    void testTestIsHourBefore() throws Exception {
    }

    @Test
    void testIsHourAfter() throws Exception {
    }

    @Test
    void testTestIsHourAfter() throws Exception {
    }

    @Test
    void testIsMinuteEquals() throws Exception {
    }

    @Test
    void testTestIsMinuteEquals() throws Exception {
    }

    @Test
    void testIsMinuteBefore() throws Exception {
    }

    @Test
    void testTestIsMinuteBefore() throws Exception {
    }

    @Test
    void testIsMinuteAfter() throws Exception {
    }

    @Test
    void testTestIsMinuteAfter() throws Exception {
    }

    @Test
    void testIsSecondEquals() throws Exception {
    }

    @Test
    void testTestIsSecondEquals() throws Exception {
    }

    @Test
    void testIsSecondBefore() throws Exception {
    }

    @Test
    void testTestIsSecondBefore() throws Exception {
    }

    @Test
    void testIsSecondAfter() throws Exception {
    }

    @Test
    void testTestIsSecondAfter() throws Exception {
    }

    @Test
    void testGetHour() throws Exception {
    }

    @Test
    void testGetMinute() throws Exception {
    }

    @Test
    void testGetSecond() throws Exception {
    }

    @Test
    void testTestGetHour() throws Exception {
    }

    @Test
    void testTestGetMinute() throws Exception {
    }

    @Test
    void testTestGetSecond() throws Exception {
    }

    @Test
    void testReduceYears() throws Exception {
    }

    @Test
    void testReduceMonths() throws Exception {
    }

    @Test
    void testReduceWeeks() throws Exception {
    }

    @Test
    void testReduceDays() throws Exception {
    }

    @Test
    void testReduce() throws Exception {
    }

    @Test
    void testTestReduce() throws Exception {
    }

    @Test
    void testTestReduce1() throws Exception {
    }

    @Test
    void testReduceReverse() throws Exception {
    }

    @Test
    void testTestReduceReverse() throws Exception {
    }

    @Test
    void testReverseReduce() throws Exception {
    }
}
