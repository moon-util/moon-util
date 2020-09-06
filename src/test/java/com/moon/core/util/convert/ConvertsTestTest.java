package com.moon.core.util.convert;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class ConvertsTestTest {

    @Test
    void testConvert() throws Exception {
        Object value = ToInt.byString.convertTo("1");
        assertEquals(value, 1);
    }
}