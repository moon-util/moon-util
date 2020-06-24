package com.moon.core.util;

import com.moon.core.enums.Patterns;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;

/**
 * @author benshaoye
 */
public class TestUtil {

    protected TestUtil() { ThrowUtil.noInstanceError(); }

    /**
     * 验证 null 值
     *
     * @param data 待测数据
     *
     * @return 如果数据 data 是 null 返回 true，否则返回 false
     */
    public static boolean isNull(Object data) { return data == null; }

    /**
     * 验证非 null 值
     *
     * @param data 待测数据
     *
     * @return 如果数据 data 不是 null 返回 true，否则返回 false
     */
    public static boolean isNotNull(Object data) { return data != null; }

    /**
     * 验证数字
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public static <C extends CharSequence> boolean isDigit(C str) {
        return StringUtil.isAllMatched(str, Character::isDigit);
    }

    /**
     * 验证数字
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public static <C extends CharSequence> boolean isNumeric(C str) { return isDigit(str); }

    /**
     * 验证字母
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public static <C extends CharSequence> boolean isLetter(C str) {
        return StringUtil.isAllMatched(str, Character::isLetter);
    }

    /**
     * 验证小写字母
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public static <C extends CharSequence> boolean isLower(C str) {
        return StringUtil.isAllMatched(str, Character::isLowerCase);
    }

    /**
     * 验证大写字母
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public static <C extends CharSequence> boolean isUpper(C str) {
        return StringUtil.isAllMatched(str, Character::isUpperCase);
    }

    /**
     * 验证居民身份证号
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public static <C extends CharSequence> boolean isResidentID(C str) { return Patterns.RESIDENT_ID.test(str); }

    /**
     * 验证 11 位手机号
     *
     * @param str 待测字符串
     * @param <C> 字符串泛型类型
     *
     * @return 如果检测通过，返回 true，否则返回 false
     */
    public static <C extends CharSequence> boolean isChineseMobile(C str) { return Patterns.CHINESE_MOBILE.test(str); }

    /**
     * 是否是扩展数字：正实数、负实数、八进制、十进制、十六进制
     *
     * @param str 待测数字字符串
     * @param <C> 字符串实际类型
     *
     * @return 符合验证时返回 true，否则返回 false
     */
    @SuppressWarnings("all")
    public final static <C extends CharSequence> boolean isExpandNumber(C str) {
        int length = str == null ? 0 : str.length();
        if (length > 0) {
            String string = str.toString();
            boolean point = false;
            int i = 0;
            char ch = string.charAt(i++);
            if (ch == 46) {
                point = true;
            } else if (!((ch > 47 && ch < 58) || (ch > 64 && ch < 71) || (ch > 96 && ch < 103))) {
                if (ch != 45 || length == i) {
                    return false;
                }
            }

            ch = string.charAt(i++);
            if (ch == 48) {
                if (length > i) {
                    ch = string.charAt(i++);
                    if (ch == 88 || ch == 120) {
                        for (; i < length; i++) {
                            ch = string.charAt(i);
                            if ((ch > 47 && ch < 58) || (ch > 64 && ch < 71) || (ch > 96 && ch < 103)) {
                                continue;
                            } else {
                                if (ch != 46) {
                                    return false;
                                } else {
                                    if (point) {
                                        return false;
                                    } else {
                                        point = true;
                                    }
                                }
                            }
                        }
                        return true;
                    }
                } else {
                    return true;
                }
            }
            for (; i < length; i++) {
                ch = string.charAt(i);
                if (ch > 57 || ch < 48) {
                    if (ch == 46) {
                        if (point) {
                            return false;
                        } else {
                            point = true;
                        }
                    } else {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 是否是一个十进制小数（包括正数和负数）
     *
     * @param str 待测数字字符串
     * @param <C> 字符串实际类型
     *
     * @return 符合验证时返回 true，否则返回 false
     */
    @SuppressWarnings("all")
    public final static <C extends CharSequence> boolean isDoubleValue(C str) {
        int length = str == null ? 0 : str.length();
        if (length > 0) {
            String string = str.toString();
            boolean point = false;

            int i = 0, ch = string.charAt(0);
            if (ch == 45) {
                i = 1;
            } else if (ch == 46) {
                i = 1;
                point = true;
            }

            if (length > i) {
                for (; i < length; i++) {
                    ch = string.charAt(i);
                    if (ch > 57 || ch < 48) {
                        if (ch == 46) {
                            if (!point) {
                                point = true;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
}
