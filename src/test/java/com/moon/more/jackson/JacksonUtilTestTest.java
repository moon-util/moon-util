package com.moon.more.jackson;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class JacksonUtilTestTest {

    @Test
    void testStringify() throws Exception {
        int[] values = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        System.out.println(JacksonUtil.stringify(values));

        String[] strs = {"123", "456", null};
        System.out.println(JacksonUtil.stringify(strs));
    }
}