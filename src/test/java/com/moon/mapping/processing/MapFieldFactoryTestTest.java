package com.moon.mapping.processing;

import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class MapFieldFactoryTestTest {

    @Test
    void testDoConvertField() throws Exception {
        YearMonth ym = new YearMonth(new Date());
        DateTimeFormat.forPattern("yyyy-MM");
        System.out.println(ym.toString(DateTimeFormat.forPattern("yyyy-MM")));
    }
}