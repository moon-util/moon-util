package com.moon.core.time;

import com.moon.core.lang.LongUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.SystemUtil;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class DatetimeTestTest {

    String fullStringify(DateTime datetime) {
        return datetime.toString(DateUtil.yyyy_MM_dd_HH_mm_ss_SSS);
    }

    @Test
    void testEndOfYear() throws Exception {
        DateTime datetime = DateTime.ofImmutableFields(2019, 8, 9, 12, 58, 59, 12, 26);
        assertEquals("2019-08-09 12:58:59 012", fullStringify(datetime));

        assertEquals("2019-01-01 00:00:00 000", fullStringify(datetime.startOfYear()));
        assertEquals("2019-12-31 23:59:59 999", fullStringify(datetime.endOfYear()));

        assertEquals("2019-08-01 00:00:00 000", fullStringify(datetime.startOfMonth()));
        assertEquals("2019-08-31 23:59:59 999", fullStringify(datetime.endOfMonth()));

        assertEquals("2019-08-09 00:00:00 000", fullStringify(datetime.startOfDay()));
        assertEquals("2019-08-09 23:59:59 999", fullStringify(datetime.endOfDay()));

        assertEquals("2019-08-09 12:00:00 000", fullStringify(datetime.startOfHour()));
        assertEquals("2019-08-09 12:59:59 999", fullStringify(datetime.endOfHour()));

        assertEquals("2019-08-09 12:58:00 000", fullStringify(datetime.startOfMinute()));
        assertEquals("2019-08-09 12:58:59 999", fullStringify(datetime.endOfMinute()));

        assertEquals("2019-08-09 12:58:59 000", fullStringify(datetime.startOfSecond()));
        assertEquals("2019-08-09 12:58:59 999", fullStringify(datetime.endOfSecond()));

        assertEquals("2019-08-05 00:00:00 000", fullStringify(datetime.startOfWeek()));
        assertEquals("2019-08-11 23:59:59 999", fullStringify(datetime.endOfWeek()));

        assertEquals("2019-08-04 00:00:00 000", fullStringify(datetime.startOfWeek(DayOfWeek.SUNDAY)));
        assertEquals("2019-08-10 23:59:59 999", fullStringify(datetime.endOfWeek(DayOfWeek.SUNDAY)));

        assertEquals("2019-08-09 00:00:00 000", fullStringify(datetime.startOfWeek(DayOfWeek.FRIDAY)));
        assertEquals("2019-08-15 23:59:59 999", fullStringify(datetime.endOfWeek(DayOfWeek.FRIDAY)));

        assertEquals("2019-08-03 00:00:00 000", fullStringify(datetime.startOfWeek(DayOfWeek.SATURDAY)));
        assertEquals("2019-08-09 23:59:59 999", fullStringify(datetime.endOfWeek(DayOfWeek.SATURDAY)));

        System.out.println(datetime.getFirstDayOfWeek());
        System.out.println(datetime.getDayOfWeek());
    }

    @Test
    void testCountOf() throws Exception {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            LongUtil.toString(time, 62);
        }
        long end = SystemUtil.now();
        System.out.println(LongUtil.toString(time, 62));
        System.out.println(end - time);

        time = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            LongUtil.toCompressionString(time, 20000);
        }
        end = SystemUtil.now();
        System.out.println(LongUtil.toCompressionString(time, 20000));
        System.out.println(end - time);
    }

    @Test
    void testLength() throws Exception {
        String ask = "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";
        List<String> asks = StringUtil.split(ask, '?');
        System.out.println(asks.size());
    }
}