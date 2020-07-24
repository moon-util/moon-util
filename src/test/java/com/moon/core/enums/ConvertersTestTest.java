package com.moon.core.enums;

import com.moon.poi.excel.annotation.format.LocaleStrategy;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.temporal.WeekFields;

/**
 * @author moonsky
 */
class ConvertersTestTest {

    @Test
    void testName() throws Exception {
        WeekFields fields = WeekFields.of(DayOfWeek.MONDAY, 4);
        // WeekFields fields = WeekFields.of(DayOfWeek.MONDAY, 4);
        WeekFields.of(LocaleStrategy.DEFAULT.getLocale());
        // LocalDate.now().get();
    }

    @Test
    @Disabled
    void testCalculator() throws Exception {
        int value = Integer.parseInt("06235000") - Integer.parseInt("06234932");
        System.out.println(value);
        System.out.println(6235000 - 6234932);
        System.out.println(10 - 2);
    }
}