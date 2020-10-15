package com.moon.core.util;

import com.moon.core.time.CalendarUtil;
import com.moon.core.time.DateUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

    /**
     * 提取日期字段
     * <p>
     * 要求符合格式：yyyy-MM-dd HH:mm:ss SSS 的一个或多个字段，并且每个字段的位数一致
     * 1. 超出部分将忽略
     * 2. 不足部分用该字段初始值填充，月日填充 1，时间各字段填充 0；
     * 3. 如果各字段之间有有效的非数字符号（汉字、- 号、/ 号、: 号、. 号等）间隔，间隔符号之间的数字就是字段值，格式无特殊要求
     * 4. 如果只有连续的数字则严格要求年份是 4 位，月日时分秒字段都是两位，最后紧接的最多三位连续数字是毫秒数
     * <p>
     * 示例：
     * |-----------------------------------------------------------------------------------|
     * | Date String                 | Year | Month | Day | Hour | Minute | Second | Micro |
     * |-----------------------------|------|-------|-----|------|--------|--------|-------|
     * | 1980年02月03日08时09分59秒23  | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 1980年2月3日8时9分59秒23      | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 1980年02月03日08095923       | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 1980-02-03 08:09:59.23      | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 1980-02/03 08.09*59 23      | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 1980 02 03 08 09 59 23      | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 1980 02 03 08 09 59         | 1980 | 02    | 03  | 08   | 09     | 59     | 00    |
     * | 1980020308095923            | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 1980 020308 095923          | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * | 19800203                    | 1980 | 02    | 03  | 00   | 00     | 00     | 00    |
     * | 日期19800203时间080959毫秒23  | 1980 | 02    | 03  | 08   | 09     | 59     | 23    |
     * |-----------------------------|------|-------|-----|------|--------|--------|-------|
     * | 999                         | 999  | 01    | 01  | 00   | 00     | 00     | 00    |
     * | 1                           | 1    | 01    | 01  | 00   | 00     | 00     | 00    |
     * |-----------------------------------------------------------------------------------|
     *
     * @return 日期年月日时分秒毫秒各字段的值或如果字符串中不包含任何数字时返回 null
     */
    @Test
    void testExtractDateFields() {
        int[] excepted0 = {1980, 2, 3, 8, 9, 59, 23}, parsed;
        parsed = CalendarUtil.extractDateTimeFields("1980-02/03 08.09*59 23");
        assertArrayEquals(excepted0, parsed);
        parsed = CalendarUtil.extractDateTimeFields("1980-2/3 8.9*59 023");
        assertArrayEquals(excepted0, parsed);
        parsed = CalendarUtil.extractDateTimeFields("1980 02 03 08 09 59 23");
        assertArrayEquals(excepted0, parsed);
        parsed = CalendarUtil.extractDateTimeFields("1980 2 3 8 09 59 023");
        assertArrayEquals(excepted0, parsed);
        parsed = CalendarUtil.extractDateTimeFields("1980年02月03日08095923");
        assertArrayEquals(excepted0, parsed);
        parsed = CalendarUtil.extractDateTimeFields("1980年02月3日080959023");
        assertArrayEquals(excepted0, parsed);
        parsed = CalendarUtil.extractDateTimeFields("1980-02-03 08:09:59.23");
        assertArrayEquals(excepted0, parsed);
        parsed = CalendarUtil.extractDateTimeFields("1980 023 08 09 59 23");
        assertArrayEquals(excepted0, parsed);
        parsed = CalendarUtil.extractDateTimeFields("19802 3 08 09 59 23");
        assertArrayEquals(excepted0, parsed);
        parsed = CalendarUtil.extractDateTimeFields("1980-2-03 08:9:59.23");
        assertArrayEquals(excepted0, parsed);
        parsed = CalendarUtil.extractDateTimeFields("1980年02月03日08时09分59秒23");
        assertArrayEquals(excepted0, parsed);
        parsed = CalendarUtil.extractDateTimeFields("1980年2月03日8时09分59秒023");
        assertArrayEquals(excepted0, parsed);
        parsed = CalendarUtil.extractDateTimeFields("1980 020308 095923");
        assertArrayEquals(excepted0, parsed);
        parsed = CalendarUtil.extractDateTimeFields("1980年2月3日8时9分59秒23");
        assertArrayEquals(excepted0, parsed);
        parsed = CalendarUtil.extractDateTimeFields("日期19800203时间080959毫秒23");
        assertArrayEquals(excepted0, parsed);
        parsed = CalendarUtil.extractDateTimeFields("日期1980023时间080959毫秒023");
        assertArrayEquals(excepted0, parsed);
        parsed = CalendarUtil.extractDateTimeFields("1980020308095923");
        assertArrayEquals(excepted0, parsed);
        System.out.println(Arrays.toString(parsed));
    }

    @Test
    void testParseDateTime() {
        assertFalse(CalendarUtil.isValidDateTimeFields(CalendarUtil.extractDateTimeFields("1602411650738")));
        System.out.println("1602411650738");
        String newStr = Arrays.toString(CalendarUtil.extractDateTimeFields("1602411650738"));
        System.out.println(newStr);
        System.out.println("===================================================");
        System.out.println("253402271999999" + "\t" + Long.MAX_VALUE);
        newStr = Arrays.toString(CalendarUtil.extractDateTimeFields("253402271999999"));
        System.out.println(newStr);
        System.out.println("===================================================");
        Calendar start = CalendarUtil.toCalendar(1970, 1, 1, 8, 0, 0, 0);
        Calendar end = CalendarUtil.toCalendar(9999, 12, 31, 23, 59, 59, 999);
        System.out
            .println(CalendarUtil.format(start, CalendarUtil.yyyy_MM_dd_HH_mm_ss_SSS) + "\t" + start.getTimeInMillis());
        System.out
            .println(CalendarUtil.format(end, CalendarUtil.yyyy_MM_dd_HH_mm_ss_SSS) + "\t" + end.getTimeInMillis());
    }
}