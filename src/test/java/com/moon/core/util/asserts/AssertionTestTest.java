package com.moon.core.util.asserts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class AssertionTestTest {

    final static String error = "错误消息";

    static final void notThrow(Executable executable) { assertDoesNotThrow(executable); }

    static final void hasThrow(Executable executable) { hasThrow(executable, Throwable.class); }

    static final void hasThrow(Executable executable, Class type) { assertThrows(type, executable); }

    @Test
    void testIsTrue() {
    }

    @Test
    void testIsFalse() {
    }

    @Test
    void testIsNull() {
    }

    @Test
    void testNotNull() {
    }

    @Test
    void testIsEmpty() {
    }

    @Test
    void testNotEmpty() {
    }

    @Test
    void testIsBlank() {
    }

    @Test
    void testNotBlank() {
    }

    @Test
    void testGt() {
    }

    @Test
    void testLt() {
    }

    @Test
    void testEq() {
    }

    @Test
    void testIsEquals() {
    }

    @Test
    void testNotEquals() {
    }

    @Test
    void testNoNullElement() {
    }

    @Test
    void testNoNullValue() {
    }
}