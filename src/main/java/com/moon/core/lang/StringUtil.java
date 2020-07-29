package com.moon.core.lang;

import com.moon.core.enums.Arrays2;
import com.moon.core.enums.Const;
import com.moon.core.enums.IntTesters;
import com.moon.core.enums.Testers;
import com.moon.core.lang.support.StringSupport;
import com.moon.core.util.ValidationUtil;
import com.moon.core.util.function.BiIntFunction;
import com.moon.core.util.function.IntBiFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.moon.core.enums.Const.STR_NULL;
import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static com.moon.core.lang.support.StringSupport.concatHandler;
import static com.moon.core.lang.support.StringSupport.formatBuilder;
import static java.lang.Character.*;

/**
 * @author moonsky
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

    /**
     * 返回字符串中指定字符出现的次数
     *
     * @param str 待测字符串
     * @param ch  目标字符
     *
     * @return 字符出现的次数
     */
    public static int countOf(final CharSequence str, final char ch) {
        if (isEmpty(str)) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (ch == str.charAt(i)) {
                count++;
            }
        }
        return count;
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
    public static String concat(CharSequence... css) { return concatAllMatched(Testers.TRUE, css); }

    /**
     * 拼接字符串
     *
     * @param predicate 拼接条件，只有通过检测的字符串才拼接
     * @param css       字符串列表
     *
     * @return 连接后的字符串
     */
    public static String concatAllMatched(Predicate<CharSequence> predicate, CharSequence... css) {
        return concatHandler(predicate, css);
    }

    /**
     * 拼接字符串
     *
     * @param converter 转换器，主要用于提供默认值等
     * @param css       字符串列表
     *
     * @return 连接后的字符串
     */
    public static String concat(BiIntFunction<CharSequence, CharSequence> converter, CharSequence... css) {
        return concatHandler(converter, css);
    }

    /*
     * -------------------------------------------------------------------
     * tests
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
     * <pre>
     * StringUtil.isEmpty(null)         === true
     * StringUtil.isEmpty("")           === true
     *
     * StringUtil.isEmpty("null")       === false
     * StringUtil.isEmpty("undefined")  === false
     * StringUtil.isEmpty(" ")          === false
     * StringUtil.isEmpty("a")          === false
     * StringUtil.isEmpty("abc")        === false
     * StringUtil.isEmpty(" a b c ")    === false
     * </pre>
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
    public static boolean isNullString(CharSequence cs) {
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
                return safeIsUndefined(cs);
            default:
                return false;
        }
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
    public static boolean isUndefined(CharSequence cs) {
        return isEmpty(cs) || (cs instanceof StringBuffer ? "undefined".equals(cs.toString()) : (cs.length() == 9 && safeIsUndefined(
            cs)));
    }

    private static boolean safeIsUndefined(CharSequence cs) {
        return cs.charAt(0) == 'u' && cs.charAt(1) == 'n' && cs.charAt(2) == 'd' && cs.charAt(3) == 'e' && cs.charAt(4) == 'f' && cs
            .charAt(5) == 'i' && cs.charAt(6) == 'n' && cs.charAt(7) == 'e' && cs.charAt(8) == 'd';
    }

    /**
     * 字符串是否是 0，主要用于某些网络请求的返回状态码判断
     *
     * @param cs 待测字符串
     *
     * @return 当字符串内容仅包含 0 时返回 true，否则返回 false
     */
    public final static boolean is0(CharSequence cs) { return cs != null && "0".equals(cs.toString()); }

    /**
     * 字符串是否是 1，主要用于某些网络请求的返回状态码判断
     *
     * @param cs 待测字符串
     *
     * @return 当字符串内容仅包含 1 时返回 true，否则返回 false
     */
    public final static boolean is1(CharSequence cs) { return cs != null && "1".equals(cs.toString()); }

    /**
     * 是否是真值
     *
     * @param sequence 待测字符串
     *
     * @return
     */
    public static boolean isTrue(CharSequence sequence) { return BooleanUtil.isTrue(sequence); }

    /**
     * 字符串内所有字符是否都符合条件
     *
     * @param cs     待测字符串
     * @param tester 依次接受 cs 中的字符为参数，返回是否符合条件
     *
     * @return 全部都符合条件返回 true
     *
     * @see IntTesters
     */
    public final static boolean isAllMatches(CharSequence cs, IntPredicate tester) {
        final int length = length(cs);
        if (length == 0) { return false; }
        for (int i = 0; i < length; i++) {
            if (!tester.test(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    /**
     * 字符串内所有字符是否都符合条件
     *
     * @param cs     待测字符串
     * @param tester 依次接受 cs 中的字符为参数，返回是否符合条件
     *
     * @return 至少有一个符号条件返回 true
     *
     * @see IntTesters
     */
    public final static boolean isAnyMatches(CharSequence cs, IntPredicate tester) {
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

    public static <C extends CharSequence> C requireEmpty(C c) { return ValidationUtil.requireEmpty(c); }

    public static <C extends CharSequence> C requireNotEmpty(C c) { return ValidationUtil.requireNotEmpty(c); }

    public static <C extends CharSequence> C requireBlank(C c) { return ValidationUtil.requireBlank(c); }

    public static <C extends CharSequence> C requireNotBlank(C c) { return ValidationUtil.requireNotBlank(c); }

    /*
     * -------------------------------------------------------------------
     * default
     * -------------------------------------------------------------------
     */

    public static String emptyIfNull(String str) { return str == null ? EMPTY : str; }

    public static <T extends CharSequence> T nullIfEmpty(T cs) { return isEmpty(cs) ? null : cs; }

    public static <T> T defaultIfNull(T cs, T defaultValue) { return cs == null ? defaultValue : cs; }

    public static <T extends CharSequence> T defaultIfEmpty(T cs, T defaultValue) {
        return isEmpty(cs) ? defaultValue : cs;
    }

    public static <T extends CharSequence> T defaultIfBlank(T cs, T defaultValue) {
        return isBlank(cs) ? defaultValue : cs;
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

    public static <T> T elseIfNull(T cs, Supplier<T> elseBuilder) { return cs == null ? elseBuilder.get() : cs; }

    public static <T extends CharSequence> T elseIfEmpty(T cs, Supplier<T> elseBuilder) {
        return isEmpty(cs) ? elseBuilder.get() : cs;
    }

    public static <T extends CharSequence> T elseIfBlank(T cs, Supplier<T> elseBuilder) {
        return isBlank(cs) ? elseBuilder.get() : cs;
    }

    public static <T extends CharSequence> boolean contains(T cs, T search) {
        if (equals(cs, search)) {
            return true;
        } else if (cs != null && search == null) {
            return false;
        } else {
            return cs.toString().contains(search.toString());
        }
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
     * toString
     * -------------------------------------------------------------------
     */

    public static String toString(Object value) { return toStringOrDefault(value, STR_NULL); }

    public static String toString(char[] chars) {
        return chars == null ? STR_NULL : new String(chars, 0, chars.length);
    }

    public static String toString(char[] chars, int from, int end) {
        return chars == null ? STR_NULL : new String(chars, from, end);
    }

    public static StringBuilder toBuilder(char[] chars, int from, int to) {
        StringBuilder builder = new StringBuilder(to - from);
        builder.append(chars, from, to);
        return builder;
    }

    public static StringBuffer toBuffer(char[] chars, int from, int to) {
        StringBuffer builder = new StringBuffer(to - from);
        builder.append(chars, from, to);
        return builder;
    }

    public static String toStringOrEmpty(Object value) { return toStringOrDefault(value, Const.EMPTY); }

    public static String toStringOrDefault(Object value, String defaultValue) {
        return value == null ? defaultValue : value.toString();
    }

    public static String stringify(Object value) { return toStringOrDefault(value, null); }

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

    /**
     * 裁剪首尾空白字符
     *
     * @param string 待裁剪字符串
     *
     * @return 裁剪首尾空白字符完成后的字符串
     */
    public static String trim(CharSequence string) { return trimToEmpty(string); }

    /**
     * 裁剪首尾空白字符
     *
     * @param string 待裁剪字符串
     *
     * @return 若裁剪完后的字符串不包含任何元素，则返回 null，否则返回裁剪完成的字符串
     */
    public static String trimToNull(CharSequence string) { return trimToDefault(string, NULL); }

    /**
     * 裁剪首尾空白字符
     *
     * @param string 待裁剪字符串
     *
     * @return 若 cs == null，返回空字符串，否则返回裁剪后的字符
     */
    public static String trimToEmpty(CharSequence string) { return trimToDefault(string, EMPTY); }

    /**
     * 裁剪首尾空白字符
     *
     * @param cs           待裁剪字符串
     * @param defaultValue 默认值
     *
     * @return 若 cs == null，返回{@code defaultValue}，否则返回裁剪后的字符
     */
    public static String trimToDefault(CharSequence cs, String defaultValue) {
        return cs == null ? defaultValue : defaultIfNull((cs.charAt(0) == 65279 ? cs.subSequence(1,
            cs.length()) : cs).toString().trim(), defaultValue);
    }

    /**
     * 取消开始部分的空白字符
     *
     * @param str 待处理字符串
     *
     * @return 如果字符串头部存在空白字符，就删除头部的空白字符，否则返回原字符串
     */
    public static String trimStart(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return str.substring(i);
            }
        }
        return str;
    }

    /**
     * 取消结尾部分的空白字符
     *
     * @param str 待处理字符串
     *
     * @return 如果字符串尾部存在空白字符，就删除尾部的空白字符，否则返回原字符串
     */
    public static String trimEnd(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        for (int i = length - 1; i >= 0; i--) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return str.substring(0, i + 1);
            }
        }
        return str;
    }

    /**
     * 删除头部从第一个字符开始所有连续匹配的字符串
     *
     * @param str      待处理字符串
     * @param starting 目标前缀
     *
     * @return 删除完成的字符串
     *
     * @see #trimPrefix(String, String) 只删除头部一个匹配的字符串
     */
    public static String trimStart(String str, String starting) {
        if (isEmpty(starting) || isEmpty(str)) {
            return str;
        }
        char[] origin = str.toCharArray();
        char[] search = starting.toCharArray();
        int start = 0, index = CharUtil.indexOf(origin, search, start);
        while (index >= 0) {
            start = index + search.length;
            index = CharUtil.indexOf(origin, search, start);
        }
        return start > 0 ? str.substring(start) : str;
    }

    /**
     * 删除尾部从最后一个字符开始所有连续匹配字符串
     *
     * @param str    待处理字符串
     * @param ending 目标后缀
     *
     * @return 删除完成的字符串
     *
     * @see #triggerSuffix(CharSequence, String) 只删除尾部一个匹配的字符串
     */
    public static String trimEnd(String str, String ending) {
        if (isEmpty(ending) || isEmpty(str)) {
            return str;
        }
        char[] origin = str.toCharArray();
        char[] search = ending.toCharArray();
        int searchLen = search.length;
        int originLen = origin.length;
        int lastIdx = originLen, originLastIdx = originLen - searchLen;
        while (CharUtil.isSafeRegionMatches(origin, originLastIdx, search, 0)) {
            lastIdx = originLastIdx;
            originLastIdx -= searchLen;
            if (originLastIdx < 0) {
                break;
            }
        }
        return lastIdx == originLen ? str : str.substring(0, lastIdx);
    }

    /**
     * 裁剪首尾标记字符
     *
     * @param str 待处理字符串
     * @param tag 标记字符串
     *
     * @return 裁剪后的字符串
     */
    public static String trim(String str, String tag) { return trim(str, tag, tag); }

    /**
     * 裁剪首尾开闭标记字符串
     *
     * @param str   待处理字符串
     * @param open  头部标记字符串
     * @param close 尾部标记字符串
     *
     * @return 裁剪后的字符串
     */
    public static String trim(String str, String open, String close) { return trim(str, open, close, null); }

    /**
     * 裁剪首尾标记字符
     *
     * @param str   待处理字符串
     * @param open  头部标记字符串
     * @param close 尾部标记字符串
     * @param type  处理方式
     *
     * @return 裁剪后的字符串
     */
    public static String trim(String str, String open, String close, TrimType type) {
        if (isEmpty(open)) {
            return trimEnd(str, close);
        }
        if (isEmpty(close)) {
            return trimStart(str, open);
        }
        if (isEmpty(str)) {
            return str;
        }
        if (type != null) {
            switch (type) {
                case PRIORITY_START:
                    return trimEnd(trimStart(str, open), close);
                case PRIORITY_END:
                    return trimStart(trimEnd(str, close), open);
                default:
            }
        }
        char[] origin = str.toCharArray();
        char[] start = open.toCharArray();
        char[] end = close.toCharArray();
        int originLen = origin.length;
        int startLen = start.length;
        int endLen = end.length;
        int minTagLen = Math.min(startLen, endLen);
        if (originLen < minTagLen) {
            return str;
        }
        int firstIdx = 0, lastIdx = originLen;
        int originLastIdx = originLen - endLen, count;
        while (true) {
            if (CharUtil.isSafeRegionMatches(origin, firstIdx, start, 0)) {
                firstIdx = firstIdx + startLen;
            }
            if (CharUtil.isSafeRegionMatches(origin, originLastIdx, end, 0)) {
                lastIdx = originLastIdx;
                originLastIdx -= endLen;
            }
            count = lastIdx - firstIdx;
            if (count < 0) {
                if (type == TrimType.BALANCE_START) {
                    return str.substring(firstIdx, lastIdx + endLen);
                } else if (type == TrimType.BALANCE_END) {
                    return str.substring(firstIdx - startLen, lastIdx);
                } else {
                    // type == null or type == DEFAULT
                    return Const.EMPTY;
                }
            } else if (count < minTagLen) {
                return str.substring(firstIdx, lastIdx);
            }
        }
    }

    public enum TrimType {
        /**
         * 优先裁剪头部，头部处理完后余下的部分裁剪尾部
         */
        PRIORITY_START,
        /**
         * 优先裁剪尾部
         */
        PRIORITY_END,
        /**
         * 平衡裁剪，最后如存在交叉部分，优先裁剪头部
         */
        BALANCE_START,
        /**
         * 平衡裁剪，最后如存在交叉部分，优先裁剪尾部
         */
        BALANCE_END,
        /**
         * 平衡裁剪，最后如存在交叉部分，返回空字符串(这是默认情况)
         */
        DEFAULT
    }

    /**
     * 取消前缀
     *
     * @param str    待处理字符串
     * @param prefix 目标前缀
     *
     * @return 如果待处理字符串以目标为前缀，就删除前缀，否则返回原字符串
     *
     * @see #trimStart(String, String) 删除头部所有连续匹配的字符串
     */
    public static String trimPrefix(String str, String prefix) {
        return str == null ? null : str.startsWith(prefix) ? substrAfter(str, prefix) : str;
    }

    /**
     * 取消后缀
     *
     * @param str    待处理字符串
     * @param suffix 目标后缀
     *
     * @return 如果待处理字符串以目标为后缀，就删除后缀，否则返回原字符串
     *
     * @see #trimEnd(String, String) 删除尾部所有连续匹配的字符串
     */
    public static String trimSuffix(String str, String suffix) {
        return str == null ? null : str.endsWith(suffix) ? substrBefore(str, suffix) : str;
    }

    /**
     * 在头部保持前缀
     *
     * @param str
     * @param prefix
     *
     * @return
     */
    public static String padPrefixIfAbsent(String str, String prefix) {
        return isEmpty(str) ? prefix : (isEmpty(prefix) || str.startsWith(prefix) ? str : (prefix + str));
    }

    /**
     * 在尾部保持后缀
     *
     * @param str
     * @param prefix
     *
     * @return
     */
    public static String padSuffixIfAbsent(String str, String prefix) {
        return isEmpty(str) ? prefix : (isEmpty(prefix) || str.endsWith(prefix) ? str : (str + prefix));
    }

    /**
     * 切换前缀，如果原字符串已指定后缀结尾，就删除，否则就追加
     *
     * @param str    字符串
     * @param prefix 前缀
     *
     * @return 转换后的字符串
     */
    public static String triggerPrefix(CharSequence str, String prefix) {
        if (isEmpty(str)) {
            return prefix;
        }
        String origin = str.toString();
        if (origin.startsWith(prefix)) {
            return origin.substring(prefix.length());
        }
        return origin + prefix;
    }

    /**
     * 切换后缀，如果原字符串已指定后缀结尾，就删除，否则就追加
     *
     * @param str    字符串
     * @param suffix 后缀
     *
     * @return 转换后的字符串
     */
    public static String triggerSuffix(CharSequence str, String suffix) {
        if (isEmpty(str)) {
            return suffix;
        }
        String origin = str.toString();
        if (origin.endsWith(suffix)) {
            return origin.substring(0, origin.length() - suffix.length());
        }
        return origin + suffix;
    }

    /*
     * -------------------------------------------------------------------
     * operations
     * -------------------------------------------------------------------
     */

    /**
     * 删除字符串中空白字符
     *
     * @param str 待处理字符串
     *
     * @return 删除所有空白字符后的字符串；如果 str == null，返回 null
     */
    public static String deleteWhitespaces(CharSequence str) { return deleteChars(str, Character::isWhitespace); }

    /**
     * 删除符合条件的字符
     *
     * @param str    待处理字符串
     * @param tester 检测规则
     *
     * @return 返回删除符合条件后的字符串；如果 str == null，返回 null
     */
    public static String deleteChars(CharSequence str, IntPredicate tester) {
        return deleteChars(str, tester, (end, chars) -> String.valueOf(chars, 0, end));
    }

    /**
     * 删除符合条件的字符，自定义返回值类型
     *
     * @param str     待处理字符串
     * @param tester  检测规则
     * @param builder 自定义返回类型，接收参数：(字符数组最后一个有效位置 endIndex，字符数组 chars)
     * @param <C>     字符串类型
     * @param <R>     返回值字符串类型
     *
     * @return builder 的返回值；如果 str == null，返回 null
     */
    public static <C extends CharSequence, R extends CharSequence> R deleteChars(
        C str, IntPredicate tester, IntBiFunction<char[], ? extends R> builder
    ) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return builder.apply(0, Arrays2.CHARS.empty());
        }
        char ch;
        int endIdx = 0, i = 0;
        char[] chars = new char[len];
        for (; i < len; i++) {
            if (!tester.test(ch = str.charAt(i))) {
                chars[endIdx++] = ch;
            }
        }
        return builder.apply(endIdx, chars);
    }

    /**
     * 连续空白只保留一个
     *
     * @param str 待操作字符串
     *
     * @return 操作成功后的字符串
     */
    public static String onlyWhitespace(String str) {
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

    public static String sortChars(String str) {
        return str == null ? null : new String(ArrayUtil.sort(str.toCharArray()));
    }

    /**
     * 去掉字符串中重复字符
     * <p>
     * 返回字符串中字符顺序为源字符串中首次出现顺序
     *
     * @param sourceString 可能包含重复字符的字符串
     *
     * @return 去掉字符串中重复字符后的新字符串
     */
    public static String distinctChars(String sourceString) {
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

    /**
     * 格式化内容，占位符为：{}
     *
     * @param template 含有占位符的模板，默认占位符为：{}
     * @param values   每个占位符对应的值，
     *                 如果值的数量超过占位符数量，超出部分将被舍弃，{@link #format(boolean, String, Object...)}
     *                 每个值用{@link String#valueOf(Object)}取字符串
     *
     * @return 替换完成后的字符串
     */
    public static String format(String template, Object... values) { return format(false, template, values); }

    /**
     * 格式化字符串
     *
     * @param appendIfValuesOverflow 如果值的数量超过占位符数量，超出部分是否统一追加到字符串末尾，{@link #format(String, Object...)}
     * @param template               含有占位符的模板，默认占位符为：{}
     * @param values                 每个占位符对应的值，
     *                               每个值用{@link String#valueOf(Object)}取字符串
     *
     * @return 替换完成后的字符串
     */
    @SuppressWarnings("all")
    public static String format(boolean appendIfValuesOverflow, String template, Object... values) {
        return formatBuilder((end, chars) -> new String(chars, 0, end),//
            appendIfValuesOverflow, toCharArray(template), values);
    }

    /**
     * 格式化字符串
     *
     * @param returningBuilder 自定义返回类型，接受两个参数：（包含所有内容的字符数组最后一个有值索引，包含所有内容的字符数组）
     * @param template         模板字符串
     * @param values           模板字符串中所有值的数组
     * @param <C>              模板类型
     * @param <R>              返回类型
     *
     * @return 自定义返回类型
     */
    public static <C extends CharSequence, R> R format(
        IntBiFunction<char[], R> returningBuilder, C template, Object... values
    ) { return formatBuilder(returningBuilder, toCharArray(template), values); }

    /**
     * 格式化字符串
     *
     * @param returningBuilder       自定义返回类型，接受两个参数：（包含所有内容的字符数组最后一个有值索引，包含所有内容的字符数组）
     * @param placeholder            自定义占位符
     * @param appendIfValuesOverflow 如果值的数量超过占位符数量，超出部分是否统一追加到字符串末尾
     * @param template               模板字符串
     * @param values                 模板字符串中所有值的数组
     * @param <C>                    模板类型
     * @param <R>                    返回类型
     *
     * @return 自定义返回类型
     */
    public static <C extends CharSequence, R> R format(
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

    /**
     * 将{@link CharSequence}转为字符数组
     *
     * @param cs CharSequence 序列
     *
     * @return 字符数组
     */
    public static char[] toCharArray(CharSequence cs) { return toCharArray(cs, false); }

    /**
     * 将{@link CharSequence}转为字符数组
     *
     * @param cs          CharSequence 序列
     * @param emptyIfNull 如果 cs == null，返回 null 或者 new char[0]
     *
     * @return 字符数组
     */
    public static char[] toCharArray(CharSequence cs, boolean emptyIfNull) {
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
    public static String capitalize(String str) {
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
    public static String uncapitalize(String str) {
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
    public static String camelcase(String str) { return camelcase(str, false); }

    /**
     * 连字符号转驼峰
     *
     * @param str             字符串
     * @param capitalizeFirst 第一个字母是否大写
     *
     * @return 转换后的字符串
     */
    public static String camelcase(String str, boolean capitalizeFirst) {
        final int len = str == null ? 0 : str.length();
        if (len == 0) { return str; }
        char curr;
        int index = 0, count = 0;
        char[] chars = new char[len];
        boolean isCamel = false, isLetter;
        for (; index < len; index++) {
            if (isLetter = isLetterOrDigit(curr = str.charAt(index))) {
                curr = (capitalizeFirst && count == 0) || isCamel ? toUpperCase(curr) : toLowerCase(curr);
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
    public static String underscore(String str) { return camelcaseToHyphen(str, '_'); }

    /**
     * 驼峰转连字符号
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
    public static String camelcaseToHyphen(String str) { return camelcaseToHyphen(str, '-'); }

    /**
     * 驼峰转连字符，可自定义连字符号
     *
     * @param hyphen 自定义连字符号
     * @param str    字符串
     *
     * @return 转换后的字符串
     */
    public static String camelcaseToHyphen(String str, char hyphen) { return camelcaseToHyphen(str, hyphen, true); }

    /**
     * 驼峰转连字符，可自定义连字符号和是否拆分连接连续大写字母
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
     * @param continuousSplit 连续大写字母是否拆分
     *
     * @return 转换后的字符串
     */
    public static String camelcaseToHyphen(String str, char hyphen, boolean continuousSplit) {
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

    /**
     * 指定位置的字符编码
     *
     * @param str   字符串
     * @param index 位置
     *
     * @return 指定位置字符编码
     *
     * @throws NullPointerException            str == null
     * @throws StringIndexOutOfBoundsException index 超出字符串范围
     */
    public static int codePointAt(String str, int index) { return str.charAt(index); }

    /**
     * 指定位置的字符编码
     *
     * @param str      字符串
     * @param index    位置
     * @param tolerant 宽容模式
     *
     * @return 指定位置字符编码
     *
     * @throws NullPointerException            str is empty
     * @throws StringIndexOutOfBoundsException 当采取非宽容模式，并且 index 超出字符串范围时
     */
    public static int codePointAt(String str, int index, boolean tolerant) {
        if (!tolerant) {
            return codePointAt(str, index);
        }
        int length = str == null ? 0 : str.length();
        if (length == 0) {
            throw new NullPointerException("Can not got char value at index of: " + index);
        }
        if (index < 0 || index >= length) {
            index = index % length + length;
        }
        return str.charAt(index);
    }

    /*
     * -------------------------------------------------------------------
     * replace
     * -------------------------------------------------------------------
     */

    /**
     * 替换字符
     *
     * @param str 目标字符串
     * @param old 搜索的字符
     * @param now 替换的字符
     *
     * @return 替换后的子串；如果 str == null，则返回 null
     */
    public static String replace(String str, char old, char now) { return str == null ? null : str.replace(old, now); }

    /**
     * 替换第一个匹配子串
     *
     * @param src 目标字符串
     * @param old 搜索子串
     * @param now 替换子串
     *
     * @return 替换后的子串；如果 str == null，则返回 null
     */
    public static String replaceFirst(String src, String old, String now) {
        return src == null ? null : src.replaceFirst(old, now);
    }

    /**
     * 指定位置字符，主要是支持负值索引
     *
     * @param str   目标字符串
     * @param index 字符索引位置
     *
     * @return 索引位置的字符
     */
    public static char charAt(CharSequence str, int index) {
        int length = str.length();
        if (index < 0) {
            return str.charAt(length + (index > -length ? index : (index % length)));
        } else {
            return str.charAt(index < length ? index : index % length);
        }
    }

    /**
     * 指定位置子字符串，仅包含单个字符
     *
     * @param str   原字符串
     * @param index 位置
     *
     * @return 单个字符的字符串
     */
    public static String substrAt(CharSequence str, int index) { return String.valueOf(charAt(str, index)); }

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

    /**
     * 截取以{@code tag}包裹的第一个子字符串
     *
     * @param str 原字符串
     * @param tag 包裹标签
     *
     * @return 子字符串
     */
    public static String substrBetween(String str, String tag) { return substrBetween(str, tag, tag); }

    /**
     * 截取字符串之间的第一个子串
     *
     * @param str   原字符串
     * @param open  起始字符串
     * @param close 结束字符串
     *
     * @return 起始字符串和结束字符串之间的子串；任意不符合截取条件的情况返回 null
     */
    public static String substrBetween(String str, String open, String close) {
        return substrBetween(str, open, close, false, false);
    }

    /**
     * 截取字符串之间的第一个子串
     *
     * @param str          原字符串
     * @param open         起始字符串
     * @param close        结束字符串
     * @param includeOpen  是否包含起始字符串
     * @param includeClose 是否包含结束字符串
     *
     * @return 起始字符串和结束字符串之间的子串；任意不符合截取条件的情况返回 null
     */
    public static String substrBetween(
        String str, String open, String close, boolean includeOpen, boolean includeClose
    ) {
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
        return str.substring(includeOpen ? start : start + open.length(), includeClose ? end + close.length() : end);
    }

    /**
     * 从字符串中截取子串
     *
     * @param str  源字符串
     * @param from 起始位置
     *
     * @return 返回从起始位置之后的子字符串
     *
     * @throws StringIndexOutOfBoundsException 起始位置超过字符串长度范围时抛出索引异常
     * @see String#substring(int) 实现方式
     * @see #slice(String, int) 强调"片段"
     */
    public static String substr(String str, int from) { return isEmpty(str) ? EMPTY : str.substring(from); }

    /**
     * 从字符串中截取子串
     *
     * @param str        源字符串
     * @param from       起始位置
     * @param charsCount 截取字符个数
     *
     * @return 返回从起始位置之后 {@code charsCount} 个字符组成的子字符串
     *
     * @throws StringIndexOutOfBoundsException 子串范围超长时抛出异常
     */
    public static String substr(String str, int from, int charsCount) { return substr(str, from, charsCount, false); }

    /**
     * 从字符串中截取子串
     *
     * @param str        源字符串
     * @param from       起始位置
     * @param charsCount 截取字符个数
     * @param tolerant   宽容模式
     *
     * @return 返回从起始位置之后 {@code charsCount} 个字符组成的子字符串
     *
     * @throws StringIndexOutOfBoundsException 当采取非宽容模式（tolerant == false），子串范围超长时抛出异常
     */
    public static String substr(String str, int from, int charsCount, boolean tolerant) {
        if (isEmpty(str)) {
            return EMPTY;
        }
        int end = from + charsCount;
        if (tolerant) {
            int length = str.length();
            if (from >= length) {
                return EMPTY;
            }
            if (end >= length) {
                end = length;
            }
        }
        return str.substring(from, end);
    }

    /**
     * 截取字符串片段，
     * <p>
     * 字符串片段参考 js 实现，总是以“宽容模式”处理，并支持负值索引
     *
     * @param str  源字符串
     * @param from 起始位置
     *
     * @return 返回从起始位置之后的字符串片段
     *
     * @see String#substring(int) 实现方式
     * @see #substr(String, int) 强调"子串"
     */
    public static String slice(String str, int from) {
        return (isEmpty(str) || from >= str.length()) ? EMPTY : str.substring(from);
    }

    /**
     * 截取字符串片段，
     * <p>
     * 字符串片段参考 js 规则，总是以“宽容模式”处理，并支持负值索引
     *
     * @param str  源字符串
     * @param from 起始位置
     * @param to   结束位置
     *
     * @return 返回从起始位置和结束位置之间的子串
     */
    public static String slice(String str, int from, int to) {
        if (isEmpty(str)) {
            return EMPTY;
        }
        int length = str.length();
        if (from >= length) {
            return EMPTY;
        }
        if (from < 0) {
            from = from % length + length;
        }
        if (to >= length) {
            to = length;
        }
        if (to < 0) {
            to = to % length + length;
        }
        if (to > from) {
            return str.substring(from, to);
        } else {
            return EMPTY;
        }
    }

    /*
    discard indexOf 和 lastIndexOf 小于 0 的情况有待商榷
     */

    /**
     * 丢弃最后一个匹字符串之后的内容
     * <p>
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
            return EMPTY;
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
     * 丢弃最后一个匹配字符之后的内容
     * <p>
     * StringUtil.discardAfterLast(null, *)         = ""
     * StringUtil.discardAfterLast("", *)           = ""
     * StringUtil.discardAfterLast(*, "")           = *
     * StringUtil.discardAfterLast(*, null)         = *
     * StringUtil.discardAfterLast("12345", "6")    = "12345"
     * StringUtil.discardAfterLast("12345", "23")   = "123"
     * StringUtil.discardAfterLast("1234235", "23") = "123423"
     *
     * @param str
     * @param search
     *
     * @return
     */
    public static String discardAfterLast(String str, String search) {
        if (isEmpty(str)) {
            return EMPTY;
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
     * 丢弃第一个匹配字符串之前的内容
     * <p>
     * StringUtil.discardBefore(null, *)         = ""
     * StringUtil.discardBefore("", *)           = ""
     * StringUtil.discardBefore(*, "")           = *
     * StringUtil.discardBefore(*, null)         = *
     * StringUtil.discardBefore("12345", "6")    = "12345"
     * StringUtil.discardBefore("12345", "23")   = "2345"
     * StringUtil.discardBefore("1234235", "23") = "234235"
     *
     * @param str    目标字符串
     * @param search 待匹配字符串
     *
     * @return 处理完成的字符串
     */
    public static String discardBefore(String str, String search) {
        if (isEmpty(str)) {
            return EMPTY;
        }
        if (isEmpty(search)) {
            return str;
        }
        int index = str.indexOf(search);
        return index < 0 ? str : str.substring(index);
    }

    /**
     * 丢弃最后一个匹配字符串之前的内容
     * <p>
     * StringUtil.discardBeforeLast(null, *)         = ""
     * StringUtil.discardBeforeLast("", *)           = ""
     * StringUtil.discardBeforeLast(*, "")           = ""
     * StringUtil.discardBeforeLast(*, null)         = ""
     * StringUtil.discardBeforeLast("12345", "6")    = ""
     * StringUtil.discardBeforeLast("12345", "23")   = "2345"
     * StringUtil.discardBeforeLast("1234235", "23") = "235"
     *
     * @param str    目标字符串
     * @param search 待匹配字符串
     *
     * @return 处理完成的字符串
     */
    public static String discardBeforeLast(String str, String search) {
        if (isEmpty(str) || isEmpty(search)) {
            return EMPTY;
        }
        int index = str.lastIndexOf(search);
        return index < 0 ? EMPTY : str.substring(index);
    }

    /*
    split
     */

    /**
     * 字符串拆分，将字符串按指定分隔符拆分
     *
     * @param str       待拆分字符串
     * @param separator 分隔符
     *
     * @return 拆分后的字符串
     */
    public static List<String> split(CharSequence str, char separator) {
        List<String> result = new ArrayList<>();
        int length = str == null ? 0 : str.length();
        if (length == 0) {
            return result;
        }
        char ch;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if ((ch = str.charAt(i)) == separator) {
                result.add(builder.toString());
                builder = new StringBuilder();
            } else {
                builder.append(ch);
            }
        }
        result.add(builder.toString());
        return result;
    }

    /**
     * 字符串拆分，将字符串按指定分隔符拆分
     *
     * @param str       待拆分字符串
     * @param separator 分隔符
     *
     * @return 拆分后的字符串
     */
    public static List<String> split(CharSequence str, String separator) {
        List<String> result = new ArrayList<>();
        int length = str == null ? 0 : str.length();
        if (length == 0) {
            return result;
        }
        String origin = str.toString();
        if (isEmpty(separator)) {
            result.add(origin);
            return result;
        }
        final int sepLen = separator.length();
        int startIdx = 0, endIdx = origin.indexOf(separator);
        while (endIdx >= 0) {
            result.add(origin.substring(startIdx, endIdx));
            startIdx = endIdx + sepLen;
            endIdx = origin.indexOf(separator, startIdx);
        }
        result.add(origin.substring(startIdx));
        return result;
    }
}
