package com.moon.core.lang;

import com.moon.core.enums.Arrays2;
import com.moon.core.enums.Const;
import com.moon.core.enums.Predicates;
import com.moon.core.lang.support.StringSupport;
import com.moon.core.util.function.IntBiFunction;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntPredicate;

import static com.moon.core.enums.Const.NULL_STR;
import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.lang.support.StringSupport.concatHandler;
import static com.moon.core.lang.support.StringSupport.formatBuilder;
import static java.lang.Character.*;

/**
 * @author benshaoye
 * @date 2018-09-11
 */
public final class StringUtil {

    private StringUtil() { noInstanceError(); }

    public final static String EMPTY = "";
    private final static char[] EMPTY_CHARS = Arrays2.CHARS.empty();
    private static final String NULL = null;
    private final static int NO = -1;

    /**
     * 比较两个字符序列是否相同
     *
     * @param str1 字符串
     * @param str2 字符串
     *
     * @return 内容是否相等
     */
    public static <T> boolean equals(T str1, T str2) { return Objects.equals(str1, str2); }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 != null && str1.equalsIgnoreCase(str2);
    }

    /**
     * 比较两个字符序列内容是否一致
     * <p>
     * "aaa".equals("aaa") ----------------------------------------> true
     * new String("1").equals(new String("1")) --------------------> true
     * StringUtil.equals(new String("1"), new String("1")) --------> true
     * StringUtil.contentEquals(new String("1"), new String("1")) -> true
     * <p>
     * new StringBuffer("1").equals(new StringBuffer("1")) --------------------> false
     * StringUtil.equals(new StringBuffer("1"), new StringBuffer("1")) --------> false
     * StringUtil.contentEquals(new StringBuffer("1"), new StringBuffer("1")) -> true
     * <p>
     * new StringBuilder("1").equals(new StringBuilder("1")) --------------------> false
     * StringUtil.equals(new StringBuilder("1"), new StringBuilder("1")) --------> false
     * StringUtil.contentEquals(new StringBuilder("1"), new StringBuilder("1")) -> true
     * <p>
     * StringUtil.contentEquals(new StringBuffer("1"), new StringBuilder("1")) --> false
     *
     * @param str1 字符串
     * @param str2 字符串
     *
     * @return 内容是否相同
     */
    public static boolean contentEquals(CharSequence str1, CharSequence str2) {
        return str1 == null ? str2 == null : (str1.equals(str2) || StringSupport.matches(str1, str2));
    }

    /*
     * -------------------------------------------------------------------
     * indexOf
     * -------------------------------------------------------------------
     */

    public static int indexOf(String str, boolean bool) { return str == null ? NO : str.indexOf(String.valueOf(bool)); }

    public static int indexOf(String str, double num) { return str == null ? NO : str.indexOf(String.valueOf(num)); }

    public static int indexOf(String str, float num) { return str == null ? NO : str.indexOf(String.valueOf(num)); }

    public static int indexOf(String str, int num) { return str == null ? NO : str.indexOf(String.valueOf(num)); }

    public static int indexOf(String str, long num) { return str == null ? NO : str.indexOf(String.valueOf(num)); }

    public static int indexOf(String str, char c) { return str == null ? NO : str.indexOf(c); }

    public static int indexOf(String str, String test) { return str == null ? NO : str.indexOf(test); }

    /*
     * -------------------------------------------------------------------
     * concat
     * -------------------------------------------------------------------
     */

    /**
     * 经一定量测试后，程序中运行的每个字符串长度平均值约在 6 - 14 之间，取 11 作为默认值，一定程度提高效率
     *
     * @param css 字符串列表
     *
     * @return 连接后的字符串
     */
    public static String concat(CharSequence... css) { return concatHandler(Predicates.TRUE, css); }

    public static String concatSkipNulls(CharSequence... css) { return concatHandler(Predicates.isNotNull, css); }

    public static String concatSkipBlanks(CharSequence... css) { return concatHandler(cs -> isNotBlank(cs), css); }

    public static String concatSkipEmpties(CharSequence... css) { return concatHandler(cs -> isNotEmpty(cs), css); }

    public static String concatUseForNulls(CharSequence nullVal, CharSequence... css) {
        return concatWithTransformer(str -> (str == null ? nullVal : str), css);
    }

    /*
     * -------------------------------------------------------------------
     * assertions
     * -------------------------------------------------------------------
     */

    /**
     * @param string 字符串
     *
     * @return 是否不是空字符串
     *
     * @see #isEmpty(CharSequence)
     */
    public static boolean isNotEmpty(CharSequence string) { return !isEmpty(string); }

    /**
     * string is null、""(EMPTY string)
     * <p>
     * StringUtil.isEmpty(null)         === true
     * StringUtil.isEmpty("")           === true
     * <p>
     * StringUtil.isEmpty("null")       === false
     * StringUtil.isEmpty("undefined")  === false
     * StringUtil.isEmpty(" ")          === false
     * StringUtil.isEmpty("a")          === false
     * StringUtil.isEmpty("abc")        === false
     * StringUtil.isEmpty(" a b c ")    === false
     *
     * @param string 待测字符串
     *
     * @return 是否为空
     */
    public static boolean isEmpty(CharSequence string) { return string == null || string.length() == 0; }

    /**
     * @param string 待测字符串
     *
     * @return 是否不为空
     *
     * @see #isBlank(CharSequence)
     */
    public static boolean isNotBlank(CharSequence string) { return !isBlank(string); }

    /**
     * string is null、""(EMPTY string) or " "(all char is whitespace)
     * <p>
     * StringUtil.isBlank(null)         === true
     * StringUtil.isBlank("")           === true
     * StringUtil.isBlank(" ")          === true
     * <p>
     * StringUtil.isBlank("null")       === false
     * StringUtil.isBlank("undefined")  === false
     * StringUtil.isBlank("a")          === false
     * StringUtil.isBlank("abc")        === false
     * StringUtil.isBlank(" a b c ")    === false
     *
     * @param string 待测字符串
     *
     * @return 是否为空
     */
    public static boolean isBlank(CharSequence string) {
        if (string != null) {
            for (int i = 0, len = string.length(); i < len; i++) {
                if (!Character.isWhitespace(string.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * cs is null or "null"
     * <p>
     * StringUtil.isNullString(null)         === true
     * StringUtil.isNullString("null")       === true
     * <p>
     * StringUtil.isNullString("undefined")  === false
     * StringUtil.isNullString("")           === false
     * StringUtil.isNullString(" ")          === false
     * StringUtil.isNullString("a")          === false
     * StringUtil.isNullString("abc")        === false
     * StringUtil.isNullString(" a b c ")    === false
     *
     * @param cs 待测字符串
     *
     * @return 是否是空或 null 字符串
     */
    public static boolean isNullString(CharSequence cs) {
        if (isEmpty(cs)) {
            return true;
        }
        if (cs instanceof StringBuffer) {
            return "null".equals(cs.toString());
        }
        if (cs.length() == 4) {
            return cs.charAt(0) == 'n' && cs.charAt(1) == 'u' && cs.charAt(2) == 'l' && cs.charAt(3) == 'l';
        }
        return false;
    }

    /**
     * cs is null、"null" or "undefined"
     * <p>
     * StringUtil.isUndefined(null)         === true
     * StringUtil.isUndefined("null")       === true
     * StringUtil.isUndefined("undefined")  === true
     * StringUtil.isUndefined("")           === true
     * <p>
     * StringUtil.isUndefined(" ")          === false
     * StringUtil.isUndefined("a")          === false
     * StringUtil.isUndefined("abc")        === false
     * StringUtil.isUndefined(" a b c ")    === false
     *
     * @param cs 待测字符串
     *
     * @return 是否是空或 null、undefined
     */
    public static boolean isUndefined(CharSequence cs) {
        if (isEmpty(cs)) {
            return true;
        }
        if (cs instanceof StringBuffer) {
            String str = cs.toString();
            return "null".equals(str) || "undefined".equals(str);
        }
        switch (cs.length()) {
            case 4:
                return cs.charAt(0) == 'n' && cs.charAt(1) == 'u' && cs.charAt(2) == 'l' && cs.charAt(3) == 'l';
            case 9:
                return cs.charAt(0) == 'u' && cs.charAt(1) == 'n' && cs.charAt(2) == 'd' && cs.charAt(3) == 'e' && cs.charAt(
                    4) == 'f' && cs.charAt(5) == 'i' && cs.charAt(6) == 'n' && cs.charAt(7) == 'e' && cs.charAt(8) == 'd';
            default:
                return false;
        }
    }

    public final static boolean isAllLetter(CharSequence cs) { return isAllMatched(cs, curr -> isLetter(curr)); }

    public final static boolean isAllUpperCase(CharSequence cs) {
        return isAllMatched(cs, curr -> Character.isUpperCase(curr));
    }

    public final static boolean isAllLowerCase(CharSequence cs) {
        return isAllMatched(cs, curr -> Character.isLowerCase(curr));
    }

    public final static boolean isAllMatched(CharSequence cs, IntPredicate tester) {
        final int length = length(cs);
        if (length == 0) { return false; }
        String source = cs.toString();
        for (int i = 0; i < length; i++) {
            if (!tester.test(source.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public final static boolean isAnyLetter(CharSequence cs) { return isAnyMatched(cs, curr -> isLetter(curr)); }

    public final static boolean isAnyUpperCase(CharSequence cs) {
        return isAnyMatched(cs, curr -> Character.isUpperCase(curr));
    }

    public final static boolean isAnyLowerCase(CharSequence cs) {
        return isAnyMatched(cs, curr -> Character.isLowerCase(curr));
    }

    public final static boolean isAnyMatched(CharSequence cs, IntPredicate tester) {
        final int length = length(cs);
        if (length == 0) { return false; }
        String source = cs.toString();
        for (int i = 0; i < length; i++) {
            if (tester.test(source.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /*
     * -------------------------------------------------------------------
     * requires
     * -------------------------------------------------------------------
     */

    public static <C extends CharSequence> C requireEmpty(C c) {
        if (isEmpty(c)) { return c; }
        String error = "Require an empty String, but got: ";
        throw new IllegalArgumentException(error + c);
    }

    public static <C extends CharSequence> C requireNotEmpty(C c) {
        if (isEmpty(c)) {
            String error = "Require a not empty String, but got: ";
            throw new IllegalArgumentException(error + c);
        }
        return c;
    }

    public static <C extends CharSequence> C requireBlank(C c) {
        if (isBlank(c)) { return c; }
        String error = "Require a blank String, but got: ";
        throw new IllegalArgumentException(error + c);
    }

    public static <C extends CharSequence> C requireNotBlank(C c) {
        if (isBlank(c)) {
            String error = "Require a not blank String, but got: ";
            throw new IllegalArgumentException(error + c);
        }
        return c;
    }

    /*
     * -------------------------------------------------------------------
     * default
     * -------------------------------------------------------------------
     */

    public static String emptyIfNull(String str) { return str == null ? EMPTY : str; }

    public static StringBuffer emptyIfNull(StringBuffer str) { return str == null ? new StringBuffer() : str; }

    public static StringBuilder emptyIfNull(StringBuilder str) { return str == null ? new StringBuilder() : str; }

    public static <T extends CharSequence> T nullIfEmpty(T cs) { return isEmpty(cs) ? null : cs; }

    public static <T> T defaultIfNull(T cs, T defaultValue) { return cs == null ? defaultValue : cs; }

    public static <T extends CharSequence> T defaultIfEmpty(T cs, T defaultValue) {
        return isEmpty(cs) ? defaultValue : cs;
    }

    public static <T extends CharSequence> T defaultIfBlank(T cs, T defaultValue) {
        return isBlank(cs) ? defaultValue : cs;
    }

    public static <T extends CharSequence> T defaultIfEquals(T cs, T testString, T defaultValue) {
        return equals(cs, testString) ? defaultValue : cs;
    }

    public static <T extends CharSequence> T defaultIfNotEquals(T cs, T testString, T defaultValue) {
        return equals(cs, testString) ? cs : defaultValue;
    }

    public static <T extends CharSequence> T defaultIfContains(T cs, T searchString, T defaultValue) {
        return contains(cs, searchString) ? defaultValue : cs;
    }

    public static <T extends CharSequence> T defaultIfNotContains(T cs, T searchString, T defaultValue) {
        return contains(cs, searchString) ? cs : defaultValue;
    }


    /**
     * 以下几个方法主要是针对 JavaScript
     *
     * @param cs           待测字符串
     * @param defaultValue 默认值
     *
     * @return 测试后的值
     *
     * @see #isNullString(CharSequence)
     */
    public static <T extends CharSequence> T defaultIfNullString(T cs, T defaultValue) {
        return isNullString(cs) ? defaultValue : cs;
    }

    public static <T extends CharSequence> T defaultIfUndefined(T cs, T defaultValue) {
        return isUndefined(cs) ? defaultValue : cs;
    }

    public static <T extends CharSequence> T nullIfNullString(T cs) { return defaultIfNullString(cs, null); }

    public static String emptyIfNullString(String cs) { return defaultIfNullString(cs, EMPTY); }

    public static <T extends CharSequence> T nullIfUndefined(T cs) { return defaultIfUndefined(cs, null); }

    public static String emptyIfUndefined(String cs) { return defaultIfUndefined(cs, EMPTY); }

    public static <T extends CharSequence> boolean contains(T cs, T search) {
        if (equals(cs, search)) {
            return true;
        } else if (cs != null && search == null) {
            return false;
        } else if (cs.toString().indexOf(search.toString()) < 0) {
            return false;
        }
        return true;
    }

    /*
     * -------------------------------------------------------------------
     * length
     * -------------------------------------------------------------------
     */

    public static int length(CharSequence cs) { return cs == null ? 0 : cs.length(); }

    /*
     * -------------------------------------------------------------------
     * pad to length
     * -------------------------------------------------------------------
     */

    public static String padStart(Object source, int length, char ch) {
        return doPad(source, length, ch, (src, chars) -> new StringBuilder().append(chars).append(src));
    }

    public static String padEnd(Object source, int length, char ch) {
        return doPad(source, length, ch, (src, chars) -> new StringBuilder().append(src).append(chars));
    }

    private static String doPad(Object source, int length, char ch, BiFunction<String, char[], CharSequence> toString) {
        String src = String.valueOf(source);
        int diff = length - length(src);
        return diff > 0 ? toString.apply(src, ArrayUtil.fill(new char[diff], ch)).toString() : src;
    }

    /*
     * -------------------------------------------------------------------
     * map
     * -------------------------------------------------------------------
     */

    public static <T, S extends CharSequence> String concatWithTransformer(Function<T, S> transformer, T... objects) {
        final int length = objects == null ? 0 : objects.length;
        if (length > 0) {
            StringBuilder builder = new StringBuilder(length * Const.DEFAULT_LENGTH);
            for (int i = 0; i < length; i++) { builder.append(transformer.apply(objects[i])); }
            return builder.toString();
        }
        return EMPTY;
    }

    /*
     * -------------------------------------------------------------------
     * toString
     * -------------------------------------------------------------------
     */

    public static String toString(Object value) { return toStringOrDefault(value, NULL_STR); }

    public static final String toString(char[] chars) {
        return chars == null ? NULL_STR : new String(chars, 0, chars.length);
    }

    public static final String toString(char[] chars, int from, int end) {
        return chars == null ? NULL_STR : new String(chars, from, end);
    }

    public static final StringBuilder toBuilder(char[] chars, int from, int to) {
        StringSupport.checkIndexesBetween(from, to, chars.length);
        StringBuilder builder = new StringBuilder(to - from);
        builder.append(chars, from, to);
        return builder;
    }

    public static final StringBuffer toBuffer(char[] chars, int from, int to) {
        StringSupport.checkIndexesBetween(from, to, chars.length);
        StringBuffer builder = new StringBuffer(to - from);
        builder.append(chars, from, to);
        return builder;
    }

    public static String stringify(Object value) { return toStringOrDefault(value, null); }

    public static String toStringOrEmpty(Object value) { return toStringOrDefault(value, Const.EMPTY); }

    public static String toStringOrDefault(Object value, String defaultValue) {
        return value == null ? defaultValue : value.toString();
    }

    /*
     * -------------------------------------------------------------------
     * repeat
     * -------------------------------------------------------------------
     */

    public static String repeat(CharSequence cs, int count) {
        if (cs != null) {
            if (count < 0) {
                throw new StringIndexOutOfBoundsException(count);
            }
            if (count == 0) {
                return EMPTY;
            }

            String str = cs.toString();
            int len = str.length();
            if (len == 0) {
                return EMPTY;
            }

            long calcLen = (long) len * (long) count;
            int newLen = (int) calcLen;
            if (newLen != calcLen) {
                throw new StringIndexOutOfBoundsException(String.valueOf(calcLen));
            }

            char[] data = new char[newLen];
            str.getChars(0, len, data, 0);

            int begin;
            for (begin = len; begin < newLen - begin; begin <<= 1) {
                System.arraycopy(data, 0, data, begin, begin);
            }
            System.arraycopy(data, 0, data, begin, newLen - begin);
            return String.valueOf(data);
        }
        return null;
    }

    /*
     * -------------------------------------------------------------------
     * trim
     * -------------------------------------------------------------------
     */

    public static String trim(CharSequence string) { return trimToEmpty(string); }

    public static String trimToNull(CharSequence string) { return trimToDefault(string, NULL); }

    public static String trimToEmpty(CharSequence string) { return trimToDefault(string, EMPTY); }

    public static String trimToDefault(CharSequence cs, String defaultValue) {
        String ret = isEmpty(cs) ? defaultValue : cs.toString().trim();
        return ret == null ? null : (length(ret) > 0 ? (ret.charAt(0) == 65279 ? ret.substring(1) : ret) : EMPTY);
    }

    /*
     * -------------------------------------------------------------------
     * operations
     * -------------------------------------------------------------------
     */

    /**
     * 删除{@link String}、{@link StringBuilder}、{@link StringBuffer}中的空白字符
     * 不支持其他类型{@link CharSequence}
     *
     * @param cs  待测字符串
     * @param <T> 字符串类型
     *
     * @return 删除所有空白字符后的字符串
     *
     * @see IllegalArgumentException can not support except types in String、StringBuilder、StringBuffer
     */
    public static <T extends CharSequence> T deleteWhitespaces(T cs) {
        if (cs != null) {
            int length = cs.length(), curr = 0;
            char[] chars;
            if (length > 0) {
                char ch;
                int i = 0;
                chars = new char[length];
                for (; i < length; i++) {
                    if (!Character.isWhitespace(ch = cs.charAt(i)) && ch != 65279) {
                        chars[curr++] = ch;
                    }
                }
            } else {
                chars = Arrays2.CHARS.empty();
            }
            if (cs instanceof String) {
                return (T) (curr == 0 ? EMPTY : String.valueOf(chars, 0, curr));
            }
            if (cs instanceof StringBuilder) {
                return (T) new StringBuilder(curr).append(chars, 0, curr);
            }
            if (cs instanceof StringBuffer) {
                return (T) new StringBuffer(curr).append(chars, 0, curr);
            }
            throw new IllegalArgumentException(cs.toString());
        }
        return null;
    }

    /**
     * 连续空白只保留一个
     *
     * @param str 待操作字符串
     *
     * @return 操作成功后的字符串
     */
    public final static String onlyWhitespace(String str) {
        char[] chars = toCharArray(str), value = Arrays2.CHARS.empty();
        boolean isNotWhitespace = true;
        int i = 0, idx = 0, len = chars.length;
        char curr;
        for (; i < len; i++) {
            curr = chars[i];
            if (Character.isWhitespace(curr)) {
                if (isNotWhitespace) {
                    isNotWhitespace = false;
                    value = SupportUtil.setChar(value, idx++, curr);
                }
            } else {
                isNotWhitespace = true;
                value = SupportUtil.setChar(value, idx++, curr);
            }
        }
        return SupportUtil.toStr(value, idx);
    }

    public final static String sortChars(String str) {
        return str == null ? null : new String(ArrayUtil.sort(str.toCharArray()));
    }

    /**
     * 去掉字符串中重复字符
     * <p>
     * 返回字符串中字符顺序为源字符串中首次出现顺序
     *
     * @param sourceString
     */
    public static final String distinctChars(String sourceString) {
        if (sourceString != null) {
            int len = sourceString.length();
            if (len == 0) {
                return EMPTY;
            }

            char[] chars = null;
            int i = 0, index = 0, j;
            outer:
            for (; i < len; i++) {
                char ch = sourceString.charAt(i);
                if (chars == null) {
                    chars = SupportUtil.setChar(null, index++, ch);
                } else {
                    for (j = 0; j < index; j++) {
                        if (chars[j] == ch) {
                            continue outer;
                        }
                    }
                    chars = SupportUtil.setChar(chars, index++, ch);
                }
            }
            return String.valueOf(chars, 0, index);
        }
        return null;
    }

    /*
     * -------------------------------------------------------------------
     * formatter
     * -------------------------------------------------------------------
     */

    public static final String format(String template, Object... values) { return format(false, template, values); }

    @SuppressWarnings("all")
    public static final String format(boolean appendIfValuesOverflow, String template, Object... values) {
        return formatBuilder((end, chars) -> new String(chars, 0, end),//
            appendIfValuesOverflow, toCharArray(template), values);
    }

    public static final <C extends CharSequence, R> R format(
        IntBiFunction<char[], R> returningBuilder, C template, Object... values
    ) { return formatBuilder(returningBuilder, toCharArray(template), values); }

    public static final <C extends CharSequence, R> R format(
        IntBiFunction<char[], R> returningBuilder,
        String placeholder,
        boolean appendIfValuesOverflow,
        C template,
        Object... values
    ) {
        return formatBuilder(returningBuilder,
            toCharArray(placeholder),
            appendIfValuesOverflow,
            toCharArray(template),
            values);
    }

    /*
     * -------------------------------------------------------------------
     * to char array
     * -------------------------------------------------------------------
     */

    public static final char[] toCharArray(CharSequence cs) { return toCharArray(cs, false); }

    public static final char[] toCharArray(CharSequence cs, boolean emptyIfNull) {
        return cs == null ? (emptyIfNull ? EMPTY_CHARS : null) : cs.toString().toCharArray();
    }

    /*
     * -------------------------------------------------------------------
     * capitalize
     * -------------------------------------------------------------------
     */

    /**
     * 首字母大写
     * <p>
     * "className"    ===      "ClassName"
     * "ClassName"    ===      "ClassName"
     * null           ===      null
     * ""             ===      ""
     * " "            ===      " "
     *
     * @param str 字符串
     *
     * @return 首字母大写后的字符串
     */
    public static final String capitalize(String str) {
        int len = str == null ? 0 : str.length();
        if (len == 0) { return str; }
        char ch = str.charAt(0);
        if (CharUtil.isLowerCase(ch)) {
            char[] chars = new char[len];
            chars[0] = (char) (ch - 32);
            str.getChars(1, len, chars, 1);
            return new String(chars);
        }
        return str;
    }

    /**
     * 首字母小写
     *
     * @param str 字符串
     *
     * @return 首字母小写后的字符串
     */
    public static final String uncapitalize(String str) {
        int len = length(str);
        if (len == 0) { return str; }
        char ch = str.charAt(0);
        if (CharUtil.isUpperCase(ch)) {
            char[] chars = new char[len];
            chars[0] = (char) (ch + 32);
            str.getChars(1, len, chars, 1);
            return new String(chars);
        }
        return str;
    }

    /**
     * 连字符号转驼峰
     *
     * @param str 字符串
     *
     * @return 转换后的字符串
     */
    public final static String camelcase(String str) { return camelcase(str, false); }

    /**
     * 连字符号转驼峰
     *
     * @param str          字符串
     * @param firstToUpper 第一个字母是否大写
     *
     * @return 转换后的字符串
     */
    public final static String camelcase(String str, boolean firstToUpper) {
        final int len = str == null ? 0 : str.length();
        if (len == 0) { return str; }
        char curr;
        int index = 0, count = 0;
        char[] chars = new char[len];
        boolean isCamel = false, isLetter;
        for (; index < len; index++) {
            if (isLetter = isLetterOrDigit(curr = str.charAt(index))) {
                curr = (firstToUpper && count == 0) || isCamel ? toUpperCase(curr) : toLowerCase(curr);
                chars[count++] = curr;
            } else if (index == 0) {
                isCamel = count != 0;
                continue;
            }
            isCamel = !isLetter;
        }
        return new String(chars, 0, count);
    }

    /**
     * "oneOnlyWhitespace"      ====    "one_only_whitespace"
     * "StringUtilTestTest"     ====    "string_util_test_test"
     * null                     ====    null
     * ""                       ====    ""
     * " "                      ====    " "
     *
     * @param str 字符串
     *
     * @return 转换后的字符串
     */
    public final static String underscore(String str) { return camelcaseToHyphen(str, '_'); }

    /**
     * 驼峰转连字符号，可自定义连字符号和是否拆分连接连续大写字母
     * <p>
     * "oneOnlyWhitespace"      ====    "one-only-whitespace"
     * "StringUtilTestTest"     ====    "string-util-test-test"
     * null                     ====    null
     * ""                       ====    ""
     * " "                      ====    " "
     *
     * @param str 字符串
     *
     * @return 转换后的字符串
     */
    public final static String camelcaseToHyphen(String str) { return camelcaseToHyphen(str, '-'); }

    /**
     * 驼峰转连字符
     *
     * @param hyphen 自定义连字符号
     * @param str    字符串
     *
     * @return 转换后的字符串
     */
    public final static String camelcaseToHyphen(String str, char hyphen) {
        return camelcaseToHyphen(str, hyphen, true);
    }

    /**
     * 驼峰转连字符
     * <p>
     * camelcaseToHyphen(null, ',')                             ====   null
     * camelcaseToHyphen("", ',')                               ====   ""
     * camelcaseToHyphen("   ", ',')                            ====   ""
     * camelcaseToHyphen("oneOnlyWhitespace", ',')              ====   "one,only,whitespace"
     * camelcaseToHyphen("StringUtilTestTest", ',')             ====   "string,util,test,test"
     * camelcaseToHyphen("SSStringUtilTestTest", ',', false)    ====   "ss,string,util,test,test"
     * camelcaseToHyphen("SSStringUtilTestTest", ',', true)     ====   "s,s,string,util,test,test"
     *
     * @param str             字符串
     * @param hyphen          自定义连字符号
     * @param continuousSplit 自定义连续大写字母是否拆分连接
     *
     * @return 转换后的字符串
     */
    public final static String camelcaseToHyphen(String str, char hyphen, boolean continuousSplit) {
        final int len = length(str);
        if (len == 0) { return str; }
        boolean prevIsUpper = false, currIsUpper;
        char ch;
        StringBuilder res = new StringBuilder(len + 5);
        for (int i = 0; i < len; i++) {
            if (currIsUpper = Character.isUpperCase(ch = str.charAt(i))) {
                ch = Character.toLowerCase(ch);
                if (i == 0) {
                    res.append(ch);
                } else if (prevIsUpper) {
                    if (continuousSplit) {
                        res.append(hyphen).append(ch);
                    } else if (Character.isLowerCase(str.charAt(i + 1))) {
                        res.append(hyphen).append(ch);
                    } else {
                        res.append(ch);
                    }
                } else {
                    res.append(hyphen).append(ch);
                }
            } else if (!Character.isWhitespace(ch)) {
                res.append(ch);
            }
            prevIsUpper = currIsUpper;
        }
        return res.toString();
    }

    public final static int codePointAt(String str, int index) { return str.charAt(index); }

    /*
     * -------------------------------------------------------------------
     * replace
     * -------------------------------------------------------------------
     */

    public final static String replace(String str, char old, char now) {
        return str == null ? null : str.replace(old, now);
    }

    public final static String replaceFirst(String src, String old, String now) { return src.replaceFirst(old, now); }

    public static char charAt(CharSequence str, int index) {
        int length = str.length();
        if (index < 0) {
            return str.charAt(length + (index > -length ? index : (index % length)));
        } else {
            return str.charAt(index < length ? index : index % length);
        }
    }

    /**
     * StringUtil.substrBefore(null, *)      = null
     * StringUtil.substrBefore("", *)        = ""
     * StringUtil.substrBefore("abc", "a")   = ""
     * StringUtil.substrBefore("abcba", "b") = "a"
     * StringUtil.substrBefore("abc", "c")   = "ab"
     * StringUtil.substrBefore("abc", "d")   = "abc"
     * StringUtil.substrBefore("abc", "")    = ""
     * StringUtil.substrBefore("abc", null)  = "abc"
     *
     * @param str
     * @param search
     *
     * @return
     */
    public static String substrBefore(String str, String search) {
        if (isEmpty(str) || search == null) {
            return str;
        }
        if (search.isEmpty()) {
            return EMPTY;
        }
        int index = str.indexOf(search);
        return index < 0 ? str : str.substring(0, index);
    }

    /**
     * StringUtil.substrBeforeLast(null, *)      = null
     * StringUtil.substrBeforeLast("", *)        = ""
     * StringUtil.substrBeforeLast("abcba", "b") = "abc"
     * StringUtil.substrBeforeLast("abc", "c")   = "ab"
     * StringUtil.substrBeforeLast("a", "a")     = ""
     * StringUtil.substrBeforeLast("a", "z")     = "a"
     * StringUtil.substrBeforeLast("a", null)    = "a"
     * StringUtil.substrBeforeLast("a", "")      = "a"
     *
     * @param str
     * @param search
     *
     * @return
     */
    public static String substrBeforeLast(String str, String search) {
        if (isEmpty(str) || isEmpty(search)) {
            return str;
        }
        int index = str.lastIndexOf(search);
        return index < 0 ? str : str.substring(0, index);
    }

    /**
     * StringUtil.substrAfter(null, *)      = null
     * StringUtil.substrAfter("", *)        = ""
     * StringUtil.substrAfter(*, null)      = ""
     * StringUtil.substrAfter("abc", "a")   = "bc"
     * StringUtil.substrAfter("abcba", "b") = "cba"
     * StringUtil.substrAfter("abc", "c")   = ""
     * StringUtil.substrAfter("abc", "d")   = ""
     * StringUtil.substrAfter("abc", "")    = "abc"
     *
     * @param str
     * @param search
     *
     * @return
     */
    public static String substrAfter(String str, String search) {
        if (isEmpty(str)) {
            return str;
        }
        if (search == null) {
            return EMPTY;
        }
        int index = str.indexOf(search);
        return index < 0 ? EMPTY : str.substring(index + search.length());
    }

    /**
     * StringUtil.substrAfterLast(null, *)      = null
     * StringUtil.substrAfterLast("", *)        = ""
     * StringUtil.substrAfterLast(*, "")        = ""
     * StringUtil.substrAfterLast(*, null)      = ""
     * StringUtil.substrAfterLast("abc", "a")   = "bc"
     * StringUtil.substrAfterLast("abcba", "b") = "a"
     * StringUtil.substrAfterLast("abc", "c")   = ""
     * StringUtil.substrAfterLast("a", "a")     = ""
     * StringUtil.substrAfterLast("a", "z")     = ""
     *
     * @param str
     * @param search
     *
     * @return
     */
    public static String substrAfterLast(String str, String search) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(search)) {
            return EMPTY;
        }
        int index = str.lastIndexOf(search);
        if (index < 0 || index == str.length() - search.length()) {
            return EMPTY;
        }
        return str.substring(index + search.length());
    }

    public static String substrBetween(String str, String search) { return substrBetween(str, search, search); }

    public static String substrBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start < 0) {
            return null;
        }
        int end = str.indexOf(close, start + open.length());
        if (end < 0) {
            return null;
        }
        return str.substring(start + open.length(), end);
    }

    /*
    discard indexOf 和 lastIndexOf 小于 0 的情况有待商榷
     */

    /**
     * StringUtil.discardAfter(null, *)      = null
     * StringUtil.discardAfter("", *)        = ""
     * StringUtil.discardAfter(*, "")        = ""
     * StringUtil.discardAfter(*, null)      = ""
     * StringUtil.discardAfter("12345", "6") = "12345"
     * StringUtil.discardAfter("12345", "23") = "123"
     *
     * @param str
     * @param search
     *
     * @return
     */
    public static String discardAfter(String str, String search) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(search)) {
            return EMPTY;
        }
        int index = str.indexOf(search);
        if (index < 0) {
            return str;
        }
        return str.substring(0, index + search.length());
    }

    /**
     * StringUtil.discardAfterLast(null, *)      = null
     * StringUtil.discardAfterLast("", *)        = ""
     * StringUtil.discardAfterLast(*, "")        = *
     * StringUtil.discardAfterLast(*, null)      = *
     * StringUtil.discardAfterLast("12345", "6") = "12345"
     * StringUtil.discardAfterLast("12345", "23") = "123"
     *
     * @param str
     * @param search
     *
     * @return
     */
    public static String discardAfterLast(String str, String search) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(search)) {
            return str;
        }
        int index = str.lastIndexOf(search);
        if (index < 0) {
            return str;
        }
        return str.substring(0, index + search.length());
    }

    /**
     * StringUtil.discardBefore(null, *)      = null
     * StringUtil.discardBefore("", *)        = ""
     * StringUtil.discardBefore(*, "")        = *
     * StringUtil.discardBefore(*, null)      = *
     * StringUtil.discardBefore("12345", "6") = "12345"
     * StringUtil.discardBefore("12345", "23") = "2345"
     *
     * @param str
     * @param search
     *
     * @return
     */
    public static String discardBefore(String str, String search) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(search)) {
            return str;
        }
        int index = str.indexOf(search);
        return index < 0 ? str : str.substring(index);
    }

    /**
     * StringUtil.discardBeforeLast(null, *)      = *
     * StringUtil.discardBeforeLast("", *)        = *
     * StringUtil.discardBeforeLast(*, "")        = ""
     * StringUtil.discardBeforeLast(*, null)      = ""
     * StringUtil.discardBeforeLast("12345", "6") = "12345"
     * StringUtil.discardBeforeLast("12345", "23") = "2345"
     *
     * @param str
     * @param search
     *
     * @return
     */
    public static String discardBeforeLast(String str, String search) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(search)) {
            return EMPTY;
        }
        int index = str.lastIndexOf(search);
        return index < 0 ? str : str.substring(index);
    }

    /*
    split

    todo splitter
     */
}
