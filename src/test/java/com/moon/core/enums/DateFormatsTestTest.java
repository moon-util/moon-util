package com.moon.core.enums;

import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * @author benshaoye
 */
class DateFormatsTestTest {

    @Test
    void testValues() {
        Date date = new Date();
        String pattern = "yyyy-MM-dd HH:mm:ss";
        for (DateFormats value : DateFormats.values()) {
            System.out.println(value.getText());
            System.out.println(value.of(pattern).format(date));
            System.out.println(value.getChineseText());
            System.out.println(value.getEnglishText());
        }
    }
}