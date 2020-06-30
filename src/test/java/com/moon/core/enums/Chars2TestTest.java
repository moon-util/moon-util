package com.moon.core.enums;

import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
class Chars2TestTest {

    @Test
    void testName() throws Exception {
        int val = '@';
        System.out.println(val);
        System.out.println("========================");
        for (Chars value : Chars.values()) {
            System.out.println(value + " <-> " + value.value + " <-> " + (int)value.value);
        }
    }
}