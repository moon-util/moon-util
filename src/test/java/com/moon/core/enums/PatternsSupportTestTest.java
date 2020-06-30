package com.moon.core.enums;

import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
class PatternsSupportTestTest {

    @Test
    void testIsIpV4() {
        for (int code : Strings.NUMBERS.value.toCharArray()) {
            System.out.println((char) code + " : " + code);
        }
    }
}