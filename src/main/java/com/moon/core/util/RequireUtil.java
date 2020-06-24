package com.moon.core.util;

import com.moon.core.enums.Patterns;

/**
 * @author benshaoye
 * @see Patterns
 */
public final class RequireUtil extends TestUtil {

    private RequireUtil() { super(); }

    /**
     * 要求非 null 值
     *
     * @param obj 待测数据
     * @param <T> 数据类型
     *
     * @return 当数据 obj 不为 null 时，返回数据本身；
     *
     * @throws IllegalArgumentException 当检测不通过时抛出异常
     */
    public static <T> T requireNotNull(T obj) {
        return requireNotNull(obj, "Invalid data, expected not null, but got null.");
    }

    /**
     * 要求非 null 值
     *
     * @param obj 待测数据
     * @param <T> 数据类型
     *
     * @return 当数据 obj 不为 null 时，返回数据本身；
     *
     * @throws IllegalArgumentException 当检测不通过时抛出异常
     */
    public static <T> T requireNotNull(T obj, String message) {
        if (isNotNull(obj)) { return obj; }
        throw new IllegalArgumentException(message);
    }

    /**
     * 要求数字
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws IllegalArgumentException 当检测不通过时抛出异常，异常消息是自定义消息
     */
    public static <C extends CharSequence> C requireDigit(C str) {
        if (isDigit(str)) {
            return str;
        }
        throw new IllegalArgumentException("Invalid digit string: " + str);
    }


    /**
     * 要求数字
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws IllegalArgumentException 当检测不通过时抛出异常，异常消息是自定义消息
     */
    public static <C extends CharSequence> C requireDigit(C str, String message) {
        if (isDigit(str)) {
            return str;
        }
        throw new IllegalArgumentException(message);
    }

    /**
     * 要求数字
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws IllegalArgumentException 当检测不通过时抛出异常
     */
    public static <C extends CharSequence> C requireNumeric(C str) { return requireDigit(str); }

    /**
     * 要求数字
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws IllegalArgumentException 当检测不通过时抛出异常，异常消息是自定义消息
     */
    public static <C extends CharSequence> C requireNumeric(C str, String message) {
        return requireDigit(str, message);
    }

    /**
     * 要求字母
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws IllegalArgumentException 当检测不通过时抛出异常
     */
    public static <C extends CharSequence> C requireLetter(C str) {
        if (isLetter(str)) {
            return str;
        }
        throw new IllegalArgumentException("Invalid letter string: " + str);
    }

    /**
     * 要求字母
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws IllegalArgumentException 当检测不通过时抛出异常，异常消息是自定义消息
     */
    public static <C extends CharSequence> C requireLetter(C str, String message) {
        if (isLetter(str)) {
            return str;
        }
        throw new IllegalArgumentException(message);
    }

    /**
     * 要求小写字母
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws IllegalArgumentException 当检测不通过时抛出异常
     */
    public static <C extends CharSequence> C requireLower(C str) {
        if (isLower(str)) {
            return str;
        }
        throw new IllegalArgumentException("Invalid lower case string: " + str);
    }

    /**
     * 要求小写字母
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws IllegalArgumentException 当检测不通过时抛出异常，异常消息是自定义消息
     */
    public static <C extends CharSequence> C requireLower(C str, String message) {
        if (isLower(str)) {
            return str;
        }
        throw new IllegalArgumentException(message);
    }

    /**
     * 要求大写字母
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws IllegalArgumentException 当检测不通过时抛出异常
     */
    public static <C extends CharSequence> C requireUpper(C str) {
        if (isUpper(str)) {
            return str;
        }
        throw new IllegalArgumentException("Invalid upper case string: " + str);
    }

    /**
     * 要求大写字母
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws IllegalArgumentException 当检测不通过时抛出异常，异常消息是自定义消息
     */
    public static <C extends CharSequence> C requireUpper(C str, String message) {
        if (isUpper(str)) {
            return str;
        }
        throw new IllegalArgumentException(message);
    }

    /**
     * 要求居民身份证号
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws IllegalArgumentException 当检测不通过时抛出异常
     */
    public static <C extends CharSequence> C requireResidentID(C str) {
        if (isResidentID(str)) {
            return str;
        }
        throw new IllegalArgumentException("Invalid resident ID: " + str);
    }

    /**
     * 要求居民身份证号
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws IllegalArgumentException 当检测不通过时抛出异常，异常消息是自定义消息
     */
    public static <C extends CharSequence> C requireResidentID(C str, String message) {
        if (isResidentID(str)) {
            return str;
        }
        throw new IllegalArgumentException(message);
    }

    /**
     * 要求中国 11 位手机号
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws IllegalArgumentException 当检测不通过时抛出异常
     */
    public static <C extends CharSequence> C requireChineseMobile(C str) {
        if (isChineseMobile(str)) {
            return str;
        }
        throw new IllegalArgumentException("Invalid resident ID: " + str);
    }

    /**
     * 要求中国 11 位手机号
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回原字符串
     *
     * @throws IllegalArgumentException 当检测不通过时抛出异常，异常消息是自定义消息
     */
    public static <C extends CharSequence> C requireChineseMobile(C str, String message) {
        if (isChineseMobile(str)) {
            return str;
        }
        throw new IllegalArgumentException(message);
    }
}
