package com.moon.core.lang;

import com.moon.core.enums.Testers;
import com.moon.core.util.IteratorUtil;
import com.moon.core.util.RandomStringUtil;
import com.moon.core.util.validator.Validator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author benshaoye
 */
class StringUtilTestTest {

    @Test
    void testSubstrBefore() {
        assertEquals(StringUtil.substrBefore(null, "1234567890"), null);
        assertEquals(StringUtil.substrBefore("", "asdfghjkl"), "");
        assertEquals(StringUtil.substrBefore("abc", "a"), "");
        assertEquals(StringUtil.substrBefore("abcba", "b"), "a");
        assertEquals(StringUtil.substrBefore("abc", "b"), "a");
        assertEquals(StringUtil.substrBefore("abc", "c"), "ab");
        assertEquals(StringUtil.substrBefore("abc", "d"), "abc");
        assertEquals(StringUtil.substrBefore("abc", ""), "");
        assertEquals(StringUtil.substrBefore("abc", null), "abc");
    }

    @Test
    void testSubstrBeforeLast() {
        assertEquals(StringUtil.substrBeforeLast(null, "1234567890"), null);
        assertEquals(StringUtil.substrBeforeLast("", "asdfghjkl"), "");
        assertEquals(StringUtil.substrBeforeLast("abcba", "b"), "abc");
        assertEquals(StringUtil.substrBeforeLast("abc", "c"), "ab");
        assertEquals(StringUtil.substrBeforeLast("a", "a"), "");
        assertEquals(StringUtil.substrBeforeLast("a", "z"), "a");
        assertEquals(StringUtil.substrBeforeLast("a", null), "a");
        assertEquals(StringUtil.substrBeforeLast("a", ""), "a");
    }


    @Test
    void testSubstrAfter() {
        assertEquals(StringUtil.substrAfter(null, "1234567890"), null);
        assertEquals(StringUtil.substrAfter("", "asdfghjkl"), "");
        assertEquals(StringUtil.substrAfter("abc", null), "");
        assertEquals(StringUtil.substrAfter("abc", "a"), "bc");
        assertEquals(StringUtil.substrAfter("abcba", "b"), "cba");
        assertEquals(StringUtil.substrAfter("abc", "c"), "");
        assertEquals(StringUtil.substrAfter("abc", "d"), "");
        assertEquals(StringUtil.substrAfter("abc", ""), "abc");
    }


    @Test
    void testSubstrAfterLast() {
        assertEquals(StringUtil.substrAfterLast(null, "124567890"), null);
        assertEquals(StringUtil.substrAfterLast("", "poiuytrewq"), "");
        assertEquals(StringUtil.substrAfterLast("asdfghjkl", ""), "");
        assertEquals(StringUtil.substrAfterLast(",mnbvcxz", null), "");
        assertEquals(StringUtil.substrAfterLast("abc", "a"), "bc");
        assertEquals(StringUtil.substrAfterLast("abcba", "b"), "a");
        assertEquals(StringUtil.substrAfterLast("abc", "c"), "");
        assertEquals(StringUtil.substrAfterLast("a", "a"), "");
        assertEquals(StringUtil.substrAfterLast("a", "z"), "");
    }

    @Test
    void testSubstrDiscardAfter() {
        assertEquals(StringUtil.discardAfter(null, "*"), null);
        assertEquals(StringUtil.discardAfter("", "*"), "");
        assertEquals(StringUtil.discardAfter("*", ""), "");
        assertEquals(StringUtil.discardAfter("*", null), "");
        assertEquals(StringUtil.discardAfter("12345", "6"), "12345");
        assertEquals(StringUtil.discardAfter("12345", "23"), "123");
    }

    @Test
    void testSubstrDiscardAfterLast() {
        assertEquals(StringUtil.discardAfterLast(null, "*"), null);
        assertEquals(StringUtil.discardAfterLast(null, "1234567890"), null);
        assertEquals(StringUtil.discardAfterLast(null, "qwetyuiop"), null);
        assertEquals(StringUtil.discardAfterLast(null, "asdfghjkl"), null);
        assertEquals(StringUtil.discardAfterLast(null, "zxcvbnm"), null);
        assertEquals(StringUtil.discardAfterLast("", "*"), "");
        assertEquals(StringUtil.discardAfterLast("", "12346789"), "");
        assertEquals(StringUtil.discardAfterLast("", "qweio"), "");
        assertEquals(StringUtil.discardAfterLast("", "asdfghjkl;"), "");
        assertEquals(StringUtil.discardAfterLast("", "asdfbnm;"), "");
        assertEquals(StringUtil.discardAfterLast("*", ""), "*");
        assertEquals(StringUtil.discardAfterLast("123467890", ""), "123467890");
        assertEquals(StringUtil.discardAfterLast("qwertyuiop", ""), "qwertyuiop");
        assertEquals(StringUtil.discardAfterLast("123467890", null), "123467890");
        assertEquals(StringUtil.discardAfterLast("qwertyuiop", null), "qwertyuiop");
        assertEquals(StringUtil.discardAfterLast("*", null), "*");
        assertEquals(StringUtil.discardAfterLast("12345", "6"), "12345");
        assertEquals(StringUtil.discardAfterLast("12345", "23"), "123");
    }

    @Test
    void testDiscardBefore() {
        assertEquals(StringUtil.discardBefore(null, "*"), null);
        assertEquals(StringUtil.discardBefore(null, "234567890"), null);
        assertEquals(StringUtil.discardBefore(null, "qwertyuiop"), null);
        assertEquals(StringUtil.discardBefore(null, "wsdfbmki876"), null);
        assertEquals(StringUtil.discardBefore("", "*"), "");
        assertEquals(StringUtil.discardBefore("", "234567890"), "");
        assertEquals(StringUtil.discardBefore("", "qwertyuiop"), "");
        assertEquals(StringUtil.discardBefore("", "wsdfbmki876"), "");
        assertEquals(StringUtil.discardBefore("*", ""), "*");
        assertEquals(StringUtil.discardBefore("wsdfbmki876", ""), "wsdfbmki876");
        assertEquals(StringUtil.discardBefore("qwertyuiop", ""), "qwertyuiop");
        assertEquals(StringUtil.discardBefore("234567890", ""), "234567890");
        assertEquals(StringUtil.discardBefore("*", null), "*");
        assertEquals(StringUtil.discardBefore("wsdfbmki876", null), "wsdfbmki876");
        assertEquals(StringUtil.discardBefore("qwertyuiop", null), "qwertyuiop");
        assertEquals(StringUtil.discardBefore("234567890", null), "234567890");
        assertEquals(StringUtil.discardBefore("12345", "6"), "12345");
        assertEquals(StringUtil.discardBefore("12345", "23"), "2345");
    }

    @Test
    void testDiscardBeforeLast() {
        assertEquals(StringUtil.discardBefore(null, "*"), null);
        assertEquals(StringUtil.discardBefore(null, "234567890"), null);
        assertEquals(StringUtil.discardBefore(null, "qwertyuiop"), null);
        assertEquals(StringUtil.discardBefore(null, "wsdfbmki876"), null);
        assertEquals(StringUtil.discardBefore("", "*"), "");
        assertEquals(StringUtil.discardBefore("", "234567890"), "");
        assertEquals(StringUtil.discardBefore("", "qwertyuiop"), "");
        assertEquals(StringUtil.discardBefore("", "wsdfbmki876"), "");
        assertEquals(StringUtil.discardBefore("*", ""), "*");
        assertEquals(StringUtil.discardBefore("wsdfbmki876", ""), "wsdfbmki876");
        assertEquals(StringUtil.discardBefore("qwertyuiop", ""), "qwertyuiop");
        assertEquals(StringUtil.discardBefore("234567890", ""), "234567890");
        assertEquals(StringUtil.discardBefore("*", null), "*");
        assertEquals(StringUtil.discardBefore("wsdfbmki876", null), "wsdfbmki876");
        assertEquals(StringUtil.discardBefore("qwertyuiop", null), "qwertyuiop");
        assertEquals(StringUtil.discardBefore("234567890", null), "234567890");
        assertEquals(StringUtil.discardBefore("12345", "6"), "12345");
        assertEquals(StringUtil.discardBefore("12345", "23"), "2345");
    }

    @Test
    void testName() {
        String str = "123456789";
        assertEquals(StringUtil.charAt(str, -1), '9');
        assertEquals(StringUtil.charAt(str, -10), '9');
        assertEquals(StringUtil.charAt(str, 0), '1');
        assertEquals(StringUtil.charAt(str, 9), '1');
    }

    @Test
    void testFormat() {
        String template = "name: {}, age: {}, sex: {}";
        StringBuilder builder = new StringBuilder(template);

        System.out.println(StringUtil.format(template, "zhangsan", 20, '男', 45, '男', 45));
        System.out.println(StringUtil.format(template, "zhangsan", 20));
        String tpl = null;
        System.out.println(StringUtil.format(tpl, "zhangsan", 20));

        assertEquals("name: zhangsan, age: 20, sex: 男", StringUtil.format(template, "zhangsan", 20, '男', 45, '男', 45));
        assertEquals("name: zhangsan, age: 20, sex: 男45男45",
            StringUtil.format(true, template, "zhangsan", 20, '男', 45, '男', 45));
    }

    @Test
    void testCapitalize() {
        String name = "className";
        assertEquals(StringUtil.capitalize(name), "ClassName");
        assertEquals(StringUtil.capitalize("ClassName"), "ClassName");
        assertNull(StringUtil.capitalize(null));
        assertEquals(StringUtil.capitalize(""), "");
        assertEquals("  ", StringUtil.capitalize("  "));
    }

    @Test
    void testEquals() {
    }

    @Test
    void testContentEquals() {
    }

    @Test
    void testIndexOf() {
    }

    static final CharSequence[] testForConcats = {
        new StringBuffer(),
        new StringBuffer("123"),
        new StringBuffer("   "),
        new StringBuffer("abcd"),
        null,
        new StringBuilder(),
        new StringBuilder("123"),
        new StringBuilder("   "),
        new StringBuilder("abcd"),
        null,
        new String(),
        new String("123"),
        new String("   "),
        new String("abcd"),
        null
    };

    @Test
    void testConcat() {
        String concat = StringUtil.concat(testForConcats);
        assertEquals(concat, "123   abcdnull123   abcdnull123   abcdnull");
    }

    /*
     * ---------------------------------------------------------------------
     * REQUIRES
     * ---------------------------------------------------------------------
     */

    @Test
    void testIsNotEmpty() {
        assertFalse(StringUtil.isNotEmpty(null));
        assertFalse(StringUtil.isNotEmpty(""));
        assertTrue(StringUtil.isNotEmpty(" "));
        assertTrue(StringUtil.isNotEmpty("a"));
        assertTrue(StringUtil.isNotEmpty("abc"));
        assertTrue(StringUtil.isNotEmpty("null"));
        assertTrue(StringUtil.isNotEmpty("undefined"));
        assertTrue(StringUtil.isNotEmpty(" a b c "));
    }

    @Test
    void testIsEmpty() {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty(" "));
        assertFalse(StringUtil.isEmpty("a"));
        assertFalse(StringUtil.isEmpty("abc"));
        assertFalse(StringUtil.isEmpty("null"));
        assertFalse(StringUtil.isEmpty("undefined"));
        assertFalse(StringUtil.isEmpty(" a b c "));
    }

    @Test
    void testIsNotBlank() {
    }

    @Test
    void testIsBlank() {
    }

    @Test
    void testIsNullString() {
        assertTrue(StringUtil.isNullString(null));
        assertTrue(StringUtil.isNullString("null"));
        assertTrue(StringUtil.isNullString(""));

        assertFalse(StringUtil.isNullString("undefined"));
        assertFalse(StringUtil.isNullString(" "));
        assertFalse(StringUtil.isNullString("a"));
        assertFalse(StringUtil.isNullString("abc"));
        assertFalse(StringUtil.isNullString(" a b c "));
    }

    @Test
    void testIsUndefined() {
        assertTrue(StringUtil.isUndefined(null));
        assertTrue(StringUtil.isUndefined("null"));
        assertTrue(StringUtil.isUndefined("undefined"));

        assertTrue(StringUtil.isUndefined(""));
        assertFalse(StringUtil.isUndefined(" "));
        assertFalse(StringUtil.isUndefined("a"));
        assertFalse(StringUtil.isUndefined("abc"));
        assertFalse(StringUtil.isUndefined(" a b c "));
    }

    @Test
    void testEmptyIfNull() {
    }

    @Test
    void testNullIfEmpty() {
    }

    @Test
    void testDefaultIfNull() {
    }

    @Test
    void testDefaultIfEmpty() {
    }

    @Test
    void testDefaultIfBlank() {
    }

    @Test
    void testDefaultIfEquals() {
    }

    @Test
    void testDefaultIfNotEquals() {
    }

    @Test
    void testDefaultIfContains() {
    }

    @Test
    void testDefaultIfNotContains() {
    }

    @Test
    void testDefaultIfNullString() {
    }

    @Test
    void testDefaultIfUndefined() {
    }

    @Test
    void testNullIfNullString() {
    }

    @Test
    void testEmptyIfNullString() {
    }

    @Test
    void testNullIfUndefined() {
    }

    @Test
    void testEmptyIfUndefined() {
    }

    @Test
    void testContains() {
    }

    @Test
    void testLength() {
    }

    @Test
    void testPadStart() {
        String str = "1";
        assertEquals(StringUtil.padStart(str, 5, '0'), "00001");
        assertEquals(StringUtil.padEnd(str, 5, '0'), "10000");
    }

    @Test
    void testPadEnd() {
        assertEquals(StringUtil.padEnd("1", 5, '0'), "10000");
    }

    @Test
    void testMap() {
    }

    @Test
    void testToString() {
    }

    @Test
    void testToBuilder() {
    }

    @Test
    void testToBuffer() {
    }

    @Test
    void testRepeat() {
    }

    @Test
    void testTrim() {
    }

    @Test
    void testTrimToNull() {
    }

    @Test
    void testTrimToEmpty() {
    }

    @Test
    void testTrimToDefault() {
    }

    @Test
    void testDeleteWhiteSpace() {
    }

    @Test
    void testDistinctChars() {
        assertEquals(StringUtil.distinctChars("aaaaaaa"), "a");
        assertTrue(StringUtil.distinctChars("").equals(""));
        assertEquals(StringUtil.distinctChars(""), "");
        assertEquals(StringUtil.distinctChars("aaaabbbbbcccccdddddfffffaaa"), "abcdf");
        assertEquals(StringUtil.distinctChars("aabbbccdddfbbcccaaffaddffaa"), "abcdf");
    }

    @Test
    void testSortChars() {
        final String src = "abcdefghijklmn";
        String now = RandomStringUtil.randomOrder(src);
        assertNotEquals(src, now);
        assertEquals(src, StringUtil.sortChars(now));
    }

    @Test
    void testFormatToChars() {
    }

    @Test
    void testFormatToBuilder() {
    }

    @Test
    void testFormatToBuffer() {
    }

    @Test
    void testToCharArray() {
        String source = "abcdefghijklmn";
        char[] chars1 = StringUtil.toCharArray(new StringBuilder(source));
        char[] chars2 = StringUtil.toCharArray(new StringBuilder(source));
        char[] chars3 = StringUtil.toCharArray(source);
        assertTrue(Arrays.equals(chars1, chars2));
        assertTrue(Arrays.equals(chars3, chars2));
        assertTrue(Arrays.equals(chars3, chars1));
    }

    @Test
    void testDeleteWhitespaces() {
        final String str = " 1 2 3 4 5 6 ";
        String deleted = StringUtil.deleteWhitespaces(str);
        assertEquals(deleted, "123456");


        final CharSequence cs1 = new StringBuilder(str);
        CharSequence deleted1 = StringUtil.deleteWhitespaces(cs1);
        assertTrue(deleted1 instanceof StringBuilder);
        assertEquals(deleted1.toString(), "123456");

        final CharSequence cs2 = new StringBuffer(str);
        CharSequence deleted2 = StringUtil.deleteWhitespaces(cs2);
        assertTrue(deleted2 instanceof StringBuffer);
        assertEquals(deleted2.toString(), "123456");
    }


    @Test
    void testRequireEmpty() {
        StringUtil.requireEmpty("");
        StringUtil.requireEmpty(null);
        assertThrows(Exception.class, () -> StringUtil.requireEmpty(" "));
        assertThrows(Exception.class, () -> StringUtil.requireEmpty("   "));
        assertThrows(Exception.class, () -> StringUtil.requireEmpty("\n\t"));
        assertThrows(Exception.class, () -> StringUtil.requireEmpty("\t"));
        assertThrows(Exception.class, () -> StringUtil.requireEmpty("\n"));
    }

    @Test
    void testRequireNotEmpty() {
        assertThrows(Exception.class, () -> StringUtil.requireNotEmpty(""));
        assertThrows(Exception.class, () -> StringUtil.requireNotEmpty(null));

        StringUtil.requireNotEmpty(" ");
        StringUtil.requireNotEmpty("   ");
        StringUtil.requireNotEmpty("   ");
        StringUtil.requireNotEmpty("\t");
        StringUtil.requireNotEmpty("\n");
        StringUtil.requireNotEmpty("\n\t");
    }

    @Test
    void testRequireBlank() {
        StringUtil.requireBlank("");
        StringUtil.requireBlank(" ");
        StringUtil.requireBlank("   ");
        StringUtil.requireBlank("   ");
        StringUtil.requireBlank("\t");
        StringUtil.requireBlank("\n");
        StringUtil.requireBlank("\n\t");


        assertThrows(Exception.class, () -> StringUtil.requireBlank("a"));
        assertThrows(Exception.class, () -> StringUtil.requireBlank("  a"));
        assertThrows(Exception.class, () -> StringUtil.requireBlank("  a  "));
        assertThrows(Exception.class, () -> StringUtil.requireBlank("\t a"));
        assertThrows(Exception.class, () -> StringUtil.requireBlank("\n a"));
        assertThrows(Exception.class, () -> StringUtil.requireBlank("\n a \t"));
    }

    @Test
    void testRequireNotBlank() {
        assertThrows(Exception.class, () -> StringUtil.requireNotBlank(""));
        assertThrows(Exception.class, () -> StringUtil.requireNotBlank(" "));
        assertThrows(Exception.class, () -> StringUtil.requireNotBlank("   "));
        assertThrows(Exception.class, () -> StringUtil.requireNotBlank("\t"));
        assertThrows(Exception.class, () -> StringUtil.requireNotBlank("\n"));
        assertThrows(Exception.class, () -> StringUtil.requireNotBlank("\n\t"));


        StringUtil.requireNotBlank("a");
        StringUtil.requireNotBlank("  a");
        StringUtil.requireNotBlank("  a  ");
        StringUtil.requireNotBlank("\t a");
        StringUtil.requireNotBlank("\n a");
        StringUtil.requireNotBlank("\n a \t");
    }

    @Test
    void testOnlyWhitespace() {
        String str = "   test  Only    White space   ";
        String now = StringUtil.onlyWhitespace(str);
        assertEquals(now, " test Only White space ");

        str = "   tes t  Only    White space   ";
        now = StringUtil.onlyWhitespace(str);
        assertEquals(now, " tes t Only White space ");
    }

    @Test
    void testUnderscore() {
        String str = "testOnlyWhitespace";
        String now = StringUtil.underscore(str);
        assertEquals(now, "test_only_whitespace");
        str = "StringUtilTestTest";
        now = StringUtil.underscore(str);
        assertEquals(now, "string_util_test_test");
        str = "CYStringUtilTestTest";
        now = StringUtil.underscore(str);
        assertEquals(now, "c_y_string_util_test_test");
    }

    @Test
    void testCamelcaseToHyphen() {
        String str = "testOnlyWhitespace";
        String now = StringUtil.camelcaseToHyphen(str);
        assertEquals(now, "test-only-whitespace");
        str = "StringUtilTestTest";
        now = StringUtil.camelcaseToHyphen(str);
        assertEquals(now, "string-util-test-test");
        str = "CYStringUtilTestTest";
        now = StringUtil.camelcaseToHyphen(str);
        assertEquals(now, "c-y-string-util-test-test");
        str = "CYStringUtilTestTest";
        now = StringUtil.camelcaseToHyphen(str, '|');
        assertEquals(now, "c|y|string|util|test|test");
        str = "CYStringUtilTestTest";
        now = StringUtil.camelcaseToHyphen(str, '-', false);
        assertEquals(now, "cy-string-util-test-test");
    }

    /**
     * 4E00-9FA5
     * 4E00-9FA5
     * 4E00-9FA5
     * 20000-2A6D6
     * 2A700-2B734
     * 2B740-2B81D
     * 2B820-2CEA1
     * 2CEB0-2EBE0
     * 2F00-2FD5
     * 2E80-2EF3
     * F900-FAD9
     * 2F800-2FA1D
     * E815-E86F
     * E400-E5E8
     * E600-E6CF
     * 31C0-31E3
     * 2FF0-2FFB
     * 3105-312F
     * 31A0-31BA
     * 3007
     */
    @Test
    void testCharCodeAt() {
    }

    @Test
    void testUnicode() {
        String str = " 4E00-9FA5\n" + "      4E00-9FA5\n" + "      4E00-9FA5\n" + "      20000-2A6D6\n" + "      2A700-2B734\n" + "      2B740-2B81D\n" + "      2B820-2CEA1\n" + "      2CEB0-2EBE0\n" + "      2F00-2FD5\n" + "      2E80-2EF3\n" + "      F900-FAD9\n" + "      2F800-2FA1D\n" + "      E815-E86F\n" + "      E400-E5E8\n" + "      E600-E6CF\n" + "      31C0-31E3\n" + "      2FF0-2FFB\n" + "      3105-312F\n" + "      31A0-31BA\n" + "      3007";

        String[] strings = str.split("\\n");
        IteratorUtil.forEach(strings, s -> {
            System.out.println("=========================================");
            s = s.trim();
            String[] unicodes = s.split("-");
            IteratorUtil.forEach(unicodes, u -> {
                u = u.trim();
                Integer.parseInt(u, 16);
                System.out.println(u + "\t" + Integer.parseInt(u, 16));
            });
        });
    }

    @Test
    void testReplace() {
        double val = .1 * .1;
        System.out.println(val);
        boolean now = true & true;
        System.out.println(now);
        now = false | false;
        System.out.println(now);
        now = true ^ false;// 表示互斥
        System.out.println(now);

        now = true;
        int num = 0;
        boolean flag = now | num++ == 0;
        System.out.println("=======================");
        System.out.println(flag);
        System.out.println(num);

        num = 0;
        flag = now || num++ > 0;
        System.out.println("=======================");
        System.out.println(flag);
        System.out.println(num);
    }

    @Test
    void testEqualsIgnoreCase() {
    }

    @Test
    void testConcatWithTransformer() {
        // assertEquals(StringUtil.concatWithTransformer(str -> str.toString()), "");
        // assertEquals(StringUtil.concatWithTransformer(str -> str.toString(), 1, 2, 3), "123");
    }

    @Test
    void testStringifyOrNull() {
    }

    @Test
    void testStringifyOrEmpty() {
    }

    @Test
    void testStringifyOrDefault() {
    }

    @Test
    void testCamelcase() {
        String string = "cy-string-util-test-test";
        assertEquals("cyStringUtilTestTest", StringUtil.camelcase(string, false));
        assertEquals("cyStringUtilTestTest", StringUtil.camelcase(string));
        assertEquals("CyStringUtilTestTest", StringUtil.camelcase(string, true));
        string = "-cy-string-util-test-test";
        assertEquals("cyStringUtilTestTest", StringUtil.camelcase(string, false));
        assertEquals("cyStringUtilTestTest", StringUtil.camelcase(string));
        assertEquals("CyStringUtilTestTest", StringUtil.camelcase(string, true));
    }

    @Test
    void testReplaceFirst() {
    }

    @Test
    void testConcatAllMatched() throws Exception {
    }

    @Test
    void testIsAllMatched() throws Exception {
    }

    @Test
    void testIsAnyMatched() throws Exception {
    }

    @Test
    void testElseIfNull() throws Exception {
    }

    @Test
    void testElseIfEmpty() throws Exception {
    }

    @Test
    void testElseIfBlank() throws Exception {
    }

    @Test
    void testToStringOrEmpty() throws Exception {
    }

    @Test
    void testToStringOrDefault() throws Exception {
    }

    @Test
    void testStringify() throws Exception {
    }

    @Test
    void testUncapitalize() throws Exception {
    }

    @Test
    void testCodePointAt() throws Exception {
    }

    @Test
    void testCharAt() throws Exception {
    }

    @Test
    void testSubstrBetween() throws Exception {
    }

    @Test
    void testTestSubstrBetween() throws Exception {
    }

    @Test
    void testDiscardAfter() throws Exception {
    }

    @Test
    void testDiscardAfterLast() throws Exception {
        assertEquals(StringUtil.discardBeforeLast(null, "123456789"), "");
        assertEquals(StringUtil.discardBeforeLast("", "123456789"), "");
        assertEquals(StringUtil.discardBeforeLast("sdfgvsdfbsdfb", ""), "");
        assertEquals(StringUtil.discardBeforeLast("12354235", null), "");
        assertEquals(StringUtil.discardBeforeLast("12345", "6"), "");
        assertEquals(StringUtil.discardBeforeLast("12345", "23"), "2345");
        assertEquals(StringUtil.discardBeforeLast("1234235", "23"), "235");
    }

    @Test
    void testDeleteChars() throws Exception {
    }

    @Test
    void testTestDeleteChars() throws Exception {
    }

    @Test
    void testSubstr() throws Exception {
        String str = "0123456789";
        assertEquals("123456789", StringUtil.substr(str, 1));
        assertEquals("12345678", StringUtil.substr(str, 1, 8));
        assertThrows(StringIndexOutOfBoundsException.class, () -> {
            StringUtil.substr(str, 1, 10);
        });
        assertEquals("123456789", StringUtil.substr(str, 1, 10, true));
        assertEquals("", StringUtil.substr(str, 10, 10, true));
    }

    @Test
    void testTestSubstr() throws Exception {
    }

    @Test
    void testTestSubstr1() throws Exception {
        Validator<String> validator = Validator.of("123456");
        // validator.require(Testers.isNotNull,"").when();
    }

    @Test
    void testSlice() throws Exception {
        String str = "0123456789";
        assertEquals("56789", StringUtil.slice(str, 5));
        assertEquals("56789", StringUtil.slice(str, 5, 15));
        assertEquals("2", StringUtil.slice("123", 1, -1));
        assertEquals("2", StringUtil.slice("123", -2, -1));
    }
}