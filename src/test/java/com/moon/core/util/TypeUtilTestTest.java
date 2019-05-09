package com.moon.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author benshaoye
 */
class TypeUtilTestTest {


    @Test
    void testCast() {
        assertEquals(TypeUtil.cast().toBooleanValue(0), false);
    }
}