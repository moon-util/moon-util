package com.moon.core.util.convert;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.moon.core.util.convert.Arrs.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author benshaoye
 */
class ToUtilTestTest {

    @Test
    void testConcat() {
        Class[] classes = ToUtil.concat(WRAPPER_NUMBERS, Arrs.EXPAND_NUMBERS);
        System.out.println(Arrays.toString(WRAPPER_NUMBERS));
        System.out.println(Arrays.toString(Arrs.EXPAND_NUMBERS));
        System.out.println(Arrays.toString(classes));
        assertEquals(classes.length, WRAPPER_NUMBERS.length + EXPAND_NUMBERS.length);
    }
}