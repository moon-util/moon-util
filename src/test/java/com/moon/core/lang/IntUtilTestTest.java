package com.moon.core.lang;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class IntUtilTestTest {

    @Test
    void testRequiredEq() {
    }

    @Test
    void testRequiredGt() {
    }

    @Test
    void testRequiredLt() {
    }

    @Test
    void testRequiredGtOrEq() {
    }

    @Test
    void testRequiredLtOrEq() {
    }

    @Test
    void testIsInt() {
        Object val = 10;
        assertTrue(IntUtil.isInt(val));
        assertTrue(IntUtil.isInt(10));
        assertFalse(IntUtil.isInt(10.0));
        assertFalse(IntUtil.isInt(""));
        assertFalse(IntUtil.isInt("a"));
        assertFalse(IntUtil.isInt(" "));
        assertFalse(IntUtil.isInt(" a b "));
        assertFalse(IntUtil.isInt(new Object()));
        assertFalse(IntUtil.isInt(new ArrayList<>()));
    }

    @Test
    void testMatchInt() {
        assertTrue(IntUtil.matchInt("10"));
        assertFalse(IntUtil.matchInt("10.0"));
        assertTrue(IntUtil.matchInt(" 10 "));
        assertTrue(IntUtil.matchInt("10 "));
    }

    @Test
    void testToIntValue() {
        Object value = 10;
        assertTrue(IntUtil.isInt(IntUtil.toIntValue(value)));
        assertTrue(IntUtil.isInt(IntUtil.toIntValue(true)));
        assertTrue(IntUtil.isInt(IntUtil.toIntValue(10L)));
        assertTrue(IntUtil.isInt(IntUtil.toIntValue(10.0D)));
        assertTrue(IntUtil.isInt(IntUtil.toIntValue(10.0F)));
        assertTrue(IntUtil.isInt(IntUtil.toIntValue(false)));
        assertTrue(IntUtil.isInt(IntUtil.toIntValue("10")));
        assertTrue(IntUtil.isInt(IntUtil.toIntValue(" 10 ")));
        assertThrows(NumberFormatException.class, () -> IntUtil.toIntValue("10.0"));
        assertThrows(NumberFormatException.class, () -> IntUtil.toIntValue(" 1 0 "));


    }

    @Test
    void testDefaultIfInvalid() {
    }

    @Test
    void testZeroIfInvalid() {
    }

    @Test
    void testOneIfInvalid() {
    }

    @Test
    void testMax() {

        int[] arr1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] arr2 = {1, 2, 3, 7, 8, 9, 4, 5, 6};

    }

    @Test
    void testMin() {
        int[] arr1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] arr2 = {1, 2, 3, 7, 8, 9, 4, 5, 6};

    }

    @Test
    void testAvg() {
        int[] arr1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] arr2 = {1, 2, 3, 7, 8, 9, 4, 5, 6};
    }

    @Test
    void testSum() {
        int[] arr1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] arr2 = {1, 2, 3, 7, 8, 9, 4, 5, 6};
    }

    @Test
    void testMultiply() {
        int[] arr1 = {1, 2, 3, 4};
        int[] arr2 = {1, 3, 2, 4};
    }
}