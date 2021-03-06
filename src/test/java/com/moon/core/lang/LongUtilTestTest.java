package com.moon.core.lang;

import com.moon.core.time.DateUtil;
import com.moon.core.time.DateTime;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class LongUtilTestTest {

    @Test
    void testTestToString() throws Exception {
        long now = DateUtil.nowTimeMillis();
        System.out.println(now);
        String value = LongUtil.toString(DateUtil.nowTimeMillis(), 36);
        System.out.println(value);
        System.out.println(String.valueOf(now).length());
        System.out.println(value.length());

        assertEquals(LongUtil.parseLong(value, 36), now);
    }

    @Test
    void testName() throws Exception {
        System.out.println(DateUtil.format());

        Date date = new Date();
        DateTime datetime = new DateTime(date);

        assertTrue(date.equals(datetime));
    }
}