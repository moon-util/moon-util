package com.moon.core.util;

import com.moon.core.time.Datetime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

/**
 * @author moonsky
 */
class DateTimeFieldTestTest {

    @Test
    void testName() throws Exception {
        Datetime datetime = Datetime.of();
        LocalTime time = datetime.toLocalTime();
        System.out.println(datetime.getNanoOfSecond());
        System.out.println(time.getNano());

        Assertions.assertEquals(time.getNano(),datetime.getNanoOfSecond());
    }
}