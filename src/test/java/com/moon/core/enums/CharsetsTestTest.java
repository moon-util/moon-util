package com.moon.core.enums;

import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
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