package com.moon.core.util.asserts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static com.moon.core.util.asserts.Assertion.*;
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
        notThrow(() -> INSTANCE.isTrue(true));
        notThrow(() -> INSTANCE.isTrue(true, error));
        hasThrow(() -> INSTANCE.isTrue(false));
        hasThrow(() -> INSTANCE.isTrue(false,error));
    }

    @Test
    void testIsFalse() {
        hasThrow(() -> INSTANCE.isFalse(true));
        hasThrow(() -> INSTANCE.isFalse(true, error));
        notThrow(() -> INSTANCE.isFalse(false));
        notThrow(() -> INSTANCE.isFalse(false,error));
    }

    @Test
    void testIsNull() {
        hasThrow(() -> INSTANCE.isNull(true));
        hasThrow(() -> INSTANCE.isNull(true, error));
        hasThrow(() -> INSTANCE.isNull(false));
        hasThrow(() -> INSTANCE.isNull(false,error));
    }

    @Test
    void testNotNull() {
        notThrow(() -> INSTANCE.notNull(true));
        notThrow(() -> INSTANCE.notNull(true, error));
        notThrow(() -> INSTANCE.notNull(false));
        notThrow(() -> INSTANCE.notNull(false,error));
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