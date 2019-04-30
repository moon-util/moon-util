package com.moon.core.lang;

import com.moon.core.exception.NumberException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author benshaoye
 */
class DoubleUtilTestTest {

    @Test
    void testRequiredEq() {
        DoubleUtil.requireEq(10.0, 10.0);
        assertThrows(NumberException.class, () -> DoubleUtil.requireEq(10.0, 10.1));
    }

    @Test
    void testRequiredGt() {
        DoubleUtil.requireGt(10.1, 10.0);
        DoubleUtil.requireGt(10.2, 10.0);
        assertThrows(NumberException.class, () -> DoubleUtil.requireGt(10.0, 10.1));
        assertThrows(NumberException.class, () -> DoubleUtil.requireGt(10.1, 10.1));
    }

    @Test
    void testRequiredLt() {
        DoubleUtil.requireLt(10.1, 10.2);
        DoubleUtil.requireLt(10.0, 10.2);
        assertThrows(NumberException.class, () -> DoubleUtil.requireLt(10.2, 10.2));
        assertThrows(NumberException.class, () -> DoubleUtil.requireLt(10.3, 10.2));
    }

    @Test
    void testRequiredGtOrEq() {
        DoubleUtil.requireGtOrEq(10.2, 10.2);
        DoubleUtil.requireGtOrEq(10.3, 10.2);
        assertThrows(NumberException.class, () -> DoubleUtil.requireGtOrEq(10.1, 10.2));
        assertThrows(NumberException.class, () -> DoubleUtil.requireGtOrEq(10.0, 10.2));
    }

    @Test
    void testRequiredLtOrEq() {
        DoubleUtil.requireLtOrEq(10.2, 10.2);
        DoubleUtil.requireLtOrEq(10.1, 10.2);
        DoubleUtil.requireLtOrEq(10.0, 10.2);
        assertThrows(NumberException.class, () -> DoubleUtil.requireLtOrEq(10.3, 10.2));
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