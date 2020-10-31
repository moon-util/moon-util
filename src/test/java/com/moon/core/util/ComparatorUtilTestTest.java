package com.moon.core.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author moonsky
 */
class ComparatorUtilTestTest {

    @Test
    void testCompare() throws Exception {
        String[] values = {"123", null, "456"};
        System.out.println(Arrays.toString(values));
        assertArrayEquals(new String[]{"123", null, "456"}, values);
        Arrays.sort(values, ComparatorUtil::comparing);
        System.out.println(Arrays.toString(values));
        assertArrayEquals(new String[]{null, "123", "456"}, values);
        Arrays.sort(values, ComparatorUtil::comparingNullBehind);
        System.out.println(Arrays.toString(values));
        assertArrayEquals(new String[]{"123", "456", null}, values);
    }
}