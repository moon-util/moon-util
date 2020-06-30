package com.moon.core.enums;

import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
class CharsetsTestTest {

    final Class<Charsets> CHARSETS = Charsets.class;

    @Test
    void testCharset() {
        Charsets[] charsets = CHARSETS.getEnumConstants();
        for (Charsets one : charsets) {
            System.out.println(one.getText());
        }
    }
}