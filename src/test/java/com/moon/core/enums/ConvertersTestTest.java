package com.moon.core.enums;

import com.moon.core.util.ListUtil;
import com.moon.core.util.SetUtil;
import com.moon.more.excel.annotation.format.LocaleStrategy;
import lombok.Lombok;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;

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