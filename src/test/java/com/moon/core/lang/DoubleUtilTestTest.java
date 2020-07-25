package com.moon.core.lang;

import com.moon.core.exception.NumberException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author moonsky
 */
class DoubleUtilTestTest {

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
    void testIsDouble() {
        assertTrue(DoubleUtil.isDouble(10.0));
        assertTrue(DoubleUtil.isDouble(10.0D));
        assertFalse(DoubleUtil.isDouble(10.0F));
        assertFalse(DoubleUtil.isDouble(10));
        assertFalse(DoubleUtil.isDouble(10L));
        assertFalse(DoubleUtil.isDouble(""));
        assertFalse(DoubleUtil.isDouble("a"));
        assertFalse(DoubleUtil.isDouble(" "));
        assertFalse(DoubleUtil.isDouble(" a "));
        assertFalse(DoubleUtil.isDouble(new Object()));
        assertFalse(DoubleUtil.isDouble(new ArrayList<>()));
        assertFalse(DoubleUtil.isDouble(new String[]{"", "A"}));
    }

    @Test
    void testMatchDouble() {
        assertTrue(DoubleUtil.matchDouble("10.0"));
        assertTrue(DoubleUtil.matchDouble("10"));
        assertTrue(DoubleUtil.matchDouble("10 "));
        assertTrue(DoubleUtil.matchDouble(" 10"));
        assertTrue(DoubleUtil.matchDouble(" 10 "));
        assertFalse(DoubleUtil.matchDouble(" 1 0 "));
        assertFalse(DoubleUtil.matchDouble("a"));
        assertFalse(DoubleUtil.matchDouble(" "));
        assertFalse(DoubleUtil.matchDouble(" a "));
    }

    @Test
    void testToDouble() {
    }

    @Test
    void testToDoubleValue() {
    }

    @Test
    void testMax() {
        DoubleUtil.max(1.1, 1.2);
    }

    @Test
    void testMin() {
    }

    @Test
    void testAvg() {
    }

    @Test
    void testSum() {
    }

    @Test
    void testMultiply() {
    }

    @Test
    void testAvgIgnoreNull() {
    }

    @Test
    void testSumIgnoreNull() {
    }

    @Test
    void testMultiplyIgnoreNull() {
    }

    @Test
    void testMaxIgnoreNull() {
    }

    @Test
    void testMinIgnoreNull() {
    }
}