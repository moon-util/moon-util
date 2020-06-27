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

        assertEquals("aaaa", FilterUtil.requireFind(strings, str -> str.length() > 3));
        assertEquals("aaaaaa", FilterUtil.requireFind(strings, str -> str.length() > 3));
        
        assertThrows(Exception.class, () ->
            FilterUtil.requireFind(strings, String::isEmpty));
        assertThrows(Exception.class, () ->
            FilterUtil.requireFind(strings, String::isEmpty));
        
        assertNull(FilterUtil.nullableFind(strings, String::isEmpty));
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
