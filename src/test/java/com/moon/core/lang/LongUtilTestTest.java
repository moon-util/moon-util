package com.moon.core.lang;

import com.moon.core.util.DateUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class LongUtilTestTest {

    @Test
    void testTestToString() throws Exception {
        long now = DateUtil.now();
        System.out.println(now);
        String value = LongUtil.toString(DateUtil.now(), 36);
        System.out.println(value);
        System.out.println(String.valueOf(now).length());
        System.out.println(value.length());
    }
}