package com.moon.core.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


/**
 * @author benshaoye
 */
public class FilterUtilTest {
    @Test
    void testRequiredFirst() {
        String[] strings = {
            "a",
            "aa",
            "aaa",
            "aaaa",
            "aaaaa",
            "aaaaaa",
        };

        assertEquals("aaaa", FilterUtil.requireFirst(strings, str -> str.length() > 3));
        assertEquals("aaaaaa", FilterUtil.requireLast(strings, str -> str.length() > 3));
        
        assertThrows(Exception.class, () ->
            FilterUtil.requireFirst(strings, String::isEmpty));
        assertThrows(Exception.class, () ->
            FilterUtil.requireLast(strings, String::isEmpty));
        
        assertNull(FilterUtil.nullableFirst(strings, String::isEmpty));
    }

    @Test
    void testRequiredLast() {
    }

    @Test
    void testNullableFirst() {
    }

    @Test
    void testNullableLast() {
    }

    @Test
    void testMatchAny() {
    }

    @Test
    void testMatchAll() {
    }

    @Test
    void testFilter() {
    }

    @Test
    void testMultiplyFilter() {
    }

    @Test
    void testForEachMatches() {
    }

}
