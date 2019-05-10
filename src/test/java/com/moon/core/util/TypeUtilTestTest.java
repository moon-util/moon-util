package com.moon.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class TypeUtilTestTest {

    static <T> T toType(Object value, Class<T> type) {
        return TypeUtil.cast().toType(value, type);
    }

    @Test
    void testCast() {
        assertEquals(TypeUtil.cast().toBooleanValue(0), false);
    }

    @Test
    void testToOptional() {
        String value = "1234567890";

        final Optional<String> optional = toType(value, Optional.class);
        assertDoesNotThrow(() -> assertTrue(optional.get() == value));
        assertTrue(optional.isPresent());
        assertFalse(optional.isAbsent());

        assertTrue(toType(null, Optional.class) == Optional.empty());
        assertTrue(toType(null, java.util.Optional.class) == java.util.Optional.empty());

        assertTrue(TypeUtil.cast().toOptional(null) == java.util.Optional.empty());
    }
}