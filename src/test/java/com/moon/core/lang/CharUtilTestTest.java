package com.moon.core.lang;

import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
class CharUtilTestTest {

    @Test
    void testIndexOf() {
        String str1 = "moonsky";
        String str2 = "shao";

        int index = CharUtil.indexOf(str1.toCharArray(), str2.toCharArray(), 0);
        System.out.println(index);
        index = CharUtil.indexOf(str2.toCharArray(), str1.toCharArray(), 0);
        System.out.println(index);
    }

    @Test
    void testIsRegionMatches() throws Exception {
    }

    @Test
    void testIsSafeRegionMatches() throws Exception {
    }

    @Test
    void testIsVarStarting() throws Exception {
    }

    @Test
    void testIsVar() throws Exception {
    }

    @Test
    void testIs_() throws Exception {
    }

    @Test
    void testIs$() throws Exception {
    }

    @Test
    void testIsUnderscore() throws Exception {
    }

    @Test
    void testIsChineseYuan() throws Exception {
    }

    @Test
    void testIsDollar() throws Exception {
    }

    @Test
    void testIsChinese() throws Exception {
    }

    @Test
    void testIsLetterOrDigit() throws Exception {
    }

    @Test
    void testIsLetter() throws Exception {
    }

    @Test
    void testIsUpperCase() throws Exception {
    }

    @Test
    void testIsLowerCase() throws Exception {
    }

    @Test
    void testIsDigit() throws Exception {
    }

    @Test
    void testEqualsIgnoreCase() throws Exception {
    }

    @Test
    void testIsASCIICode() throws Exception {
    }

    @Test
    void testIsChar() throws Exception {
    }

    @Test
    void testToDigitMaxAs62() throws Exception {
    }

    @Test
    void testToCharValue() throws Exception {
    }

    @Test
    void testTestToCharValue() throws Exception {
    }

    @Test
    void testTestToCharValue1() throws Exception {
    }

    @Test
    void testTestToCharValue2() throws Exception {
    }

    @Test
    void testTestToCharValue3() throws Exception {
    }

    @Test
    void testTestToCharValue4() throws Exception {
    }
}