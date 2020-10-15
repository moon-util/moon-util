package com.moon.core.util;

import com.moon.core.lang.CharUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author moonsky
 */
class RandomStringUtilTestTest {

    Object res, res0, res1, res2, res3;
    String s, s1, s2, s3, s4, s5, s6, s0;
    char ch, ch1;

    @Test
    void testInitCommonChinese() {
        int len = 5;
        s = RandomStringUtil.nextLower(len);
        assertEquals(s.length(), len);
        for (int i = 0; i < len; i++) {
            assertTrue(CharUtil.isLowerCase(s.charAt(i)));
        }

        int min = 5, max = 10;
        s = RandomStringUtil.nextLower(min, max);
        len = s.length();
        assertTrue(s.length() <= max);
        assertTrue(s.length() >= min);
        for (int i = 0; i < len; i++) {
            assertTrue(CharUtil.isLowerCase(s.charAt(i)));
        }
    }

    @Test
    void testNextChinese() {
        int len = 5;
        s = RandomStringUtil.nextChinese(len);
        assertEquals(s.length(), len);
        for (int i = 0; i < len; i++) {
            assertTrue(CharUtil.isChinese(s.charAt(i)));
        }

        int min = 5, max = 10;
        s = RandomStringUtil.nextChinese(min, max);
        len = s.length();
        assertTrue(s.length() <= max);
        assertTrue(s.length() >= min);
        for (int i = 0; i < len; i++) {
            assertTrue(CharUtil.isChinese(s.charAt(i)));
        }
    }

    @Test
    void testNextUpper() {
        int len = 5;
        s = RandomStringUtil.nextUpper(len);
        assertEquals(s.length(), len);
        for (int i = 0; i < len; i++) {
            assertTrue(CharUtil.isUpperCase(s.charAt(i)));
        }

        int min = 5, max = 10;
        s = RandomStringUtil.nextUpper(min, max);
        len = s.length();
        assertTrue(s.length() <= max);
        assertTrue(s.length() >= min);
        for (int i = 0; i < len; i++) {
            assertTrue(CharUtil.isUpperCase(s.charAt(i)));
        }
    }

    @Test
    void testNextLetter() {
        int len = 5;
        s = RandomStringUtil.nextLetter(len);
        assertEquals(s.length(), len);
        for (int i = 0; i < len; i++) {
            assertTrue(CharUtil.isLetter(s.charAt(i)));
        }

        int min = 5, max = 10;
        s = RandomStringUtil.nextLetter(min, max);
        len = s.length();
        assertTrue(s.length() <= max);
        assertTrue(s.length() >= min);
        for (int i = 0; i < len; i++) {
            assertTrue(CharUtil.isLetter(s.charAt(i)));
        }
    }

    @Test
    void testNextControl() {
    }

    @Test
    void testNextDigit() {
        int len = 5;
        s = RandomStringUtil.nextDigit(len);
        assertEquals(s.length(), len);
        for (int i = 0; i < len; i++) {
            assertTrue(CharUtil.isDigit(s.charAt(i)));
        }

        int min = 5, max = 10;
        s = RandomStringUtil.nextDigit(min, max);
        len = s.length();
        assertTrue(s.length() <= max);
        assertTrue(s.length() >= min);
        for (int i = 0; i < len; i++) {
            assertTrue(CharUtil.isDigit(s.charAt(i)));
        }
    }

    @Test
    void testNextSymbol() {
    }

    @Test
    void testRandomOrder() {
        String str = "abcedfghijklmnopqrstuvwxyz";
        s = RandomStringUtil.randomOrder(str);
        assertEquals(s.length(), str.length());
        assertNotEquals(s, str);
        char[] chars0 = str.toCharArray();
        char[] chars1 = s.toCharArray();
        Arrays.sort(chars0);
        Arrays.sort(chars1);
        assertTrue(Arrays.equals(chars0, chars1));
    }

    @Test
    void testName() {
        System.out.println(2 % 3);
    }
}