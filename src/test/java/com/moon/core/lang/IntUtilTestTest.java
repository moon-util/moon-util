package com.moon.core.lang;

import com.moon.core.exception.NumberException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class IntUtilTestTest {

    @Test
    void testRequiredEq() {
        IntUtil.requireEq(1, 1);
        assertThrows(NumberException.class, () -> IntUtil.requireEq(1, 2));
    }

    @Test
    void testRequiredGt() {
        IntUtil.requireGt(2, 1);

        assertThrows(NumberException.class, () -> IntUtil.requireGt(1, 2));
        assertThrows(NumberException.class, () -> IntUtil.requireGt(2, 2));
    }

    @Test
    void testRequiredLt() {
        IntUtil.requireLt(2, 3);

        assertThrows(NumberException.class, () -> IntUtil.requireLt(2, 2));
        assertThrows(NumberException.class, () -> IntUtil.requireLt(3, 2));
    }

    @Test
    void testRequiredGtOrEq() {
        IntUtil.requireGtOrEq(2, 2);
        IntUtil.requireGtOrEq(3, 2);
        assertThrows(NumberException.class, () -> IntUtil.requireGtOrEq(1, 2));
    }

    @Test
    void testRequiredLtOrEq() {
        IntUtil.requireLtOrEq(1, 2);
        IntUtil.requireLtOrEq(2, 2);
        assertThrows(NumberException.class, () -> IntUtil.requireLtOrEq(3, 2));
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
        IntUtil.requireEq(IntUtil.defaultIfInvalid("20", 10), 20);
        IntUtil.requireEq(IntUtil.defaultIfInvalid("2 0", 10), 10);
    }

    @Test
    void testZeroIfInvalid() {
        IntUtil.requireEq(IntUtil.zeroIfInvalid("20"), 20);
        IntUtil.requireEq(IntUtil.zeroIfInvalid("2 0"), 0);
    }

    @Test
    void testOneIfInvalid() {
        IntUtil.requireEq(IntUtil.oneIfInvalid("20"), 20);
        IntUtil.requireEq(IntUtil.oneIfInvalid("2 0"), 1);
    }

    @Test
    void testMax() {
        IntUtil.requireEq(IntUtil.max(1, 2, 3, 4, 5, 6, 7, 8, 9), 9);
        IntUtil.requireEq(IntUtil.max(1, 2, 3, 7, 8, 9, 4, 5, 6), 9);

        int[] arr1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] arr2 = {1, 2, 3, 7, 8, 9, 4, 5, 6};

        IntUtil.requireEq(IntUtil.max(arr1), 9);
        IntUtil.requireEq(IntUtil.max(arr2), 9);
    }

    @Test
    void testMin() {
        IntUtil.requireEq(IntUtil.min(1, 2, 3, 4, 5, 6, 7, 8, 9), 1);
        IntUtil.requireEq(IntUtil.min(1, 2, 3, 7, 8, 9, 4, 5, 6), 1);

        int[] arr1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] arr2 = {1, 2, 3, 7, 8, 9, 4, 5, 6};

        IntUtil.requireEq(IntUtil.min(arr1), 1);
        IntUtil.requireEq(IntUtil.min(arr2), 1);
    }

    @Test
    void testAvg() {
        IntUtil.requireEq(IntUtil.avg(1, 2, 3, 4, 5, 6, 7, 8, 9), 5);
        IntUtil.requireEq(IntUtil.avg(1, 2, 3, 7, 8, 9, 4, 5, 6), 5);

        int[] arr1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] arr2 = {1, 2, 3, 7, 8, 9, 4, 5, 6};

        IntUtil.requireEq(IntUtil.avg(arr1), 5);
        IntUtil.requireEq(IntUtil.avg(arr2), 5);
    }

    @Test
    void testSum() {

        IntUtil.requireEq(IntUtil.sum(1, 2, 3, 4, 5, 6, 7, 8, 9), 45);
        IntUtil.requireEq(IntUtil.sum(1, 2, 3, 7, 8, 9, 4, 5, 6), 45);

        int[] arr1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[] arr2 = {1, 2, 3, 7, 8, 9, 4, 5, 6};

        IntUtil.requireEq(IntUtil.sum(arr1), 45);
        IntUtil.requireEq(IntUtil.sum(arr2), 45);
    }

    @Test
    void testMultiply() {

        IntUtil.requireEq(IntUtil.multiply(1, 2, 3, 4), 24);
        IntUtil.requireEq(IntUtil.multiply(1, 3, 2, 4), 24);

        int[] arr1 = {1, 2, 3, 4};
        int[] arr2 = {1, 3, 2, 4};

        IntUtil.requireEq(IntUtil.multiply(arr1), 24);
        IntUtil.requireEq(IntUtil.multiply(arr2), 24);
    }
}