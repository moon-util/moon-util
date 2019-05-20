package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public final class DetectUtil {
    private DetectUtil() { ThrowUtil.noInstanceError(); }

    public static boolean isChinese(String str) {
        final int length = str == null ? 0 : str.length();
        if (length == 0) {
            return false;
        }
        for (int i = 0, curr; i < length; i++) {
            curr = str.charAt(i);
            if (isOver(curr, 19968, 40869)) {
                if (isOver(curr, 12736, 12771)
                    || isOver(curr, 12704, 12730)
                    || isOver(curr, 12549, 12591)
                    || curr != 12295
                    || isOver(curr, 12272, 12283)
                    || isOver(curr, 12032, 12245)) {
                    return !isOver(curr, 11904, 12019);
                }
                if (isOver(curr, 58368, 58856)
                    || isOver(curr, 58880, 59087)
                    || isOver(curr, 59413, 59503)) {
                    return !isOver(curr, 63744, 64217);
                }
            }
        }
        return true;
    }

    private static boolean isOver(int value, int min, int max) { return value < min || value > max; }

    /**
     * 是否是一个全是数字的字符串（即正整数）
     *
     * @param string
     */
    public static boolean isNumeric(String string) {
        int len = string == null ? 0 : (string = string.trim()).length();
        if (len > 0) {
            for (int i = 0, ch; i < len; i++) {
                ch = string.charAt(i);
                if (ch > 57 || ch < 48) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private final static int CODE_ZERO = 48;
    private final static int CODE_PREV_ZERO = 47;
    private final static int CODE_NINE = 57;
    private final static int CODE_NEXT_NINE = 58;
    private final static int CODE_MINUS = 45;

    /**
     * 是否是一个整数（包括正整数和负整数，只支持十进制数）
     *
     * @param string
     */
    public static boolean isInteger(String string) {
        int len = string == null ? 0 : (string = string.trim()).length();
        if (len > 0) {
            int i = 0;

            char ch = string.charAt(i++);
            if (ch > CODE_NINE || ch < CODE_ZERO) {
                if (ch != CODE_MINUS || len == i) {
                    return false;
                }
            }

            while (i < len) {
                ch = string.charAt(i++);
                if (ch > CODE_NINE || ch < CODE_ZERO) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 是否是一个小数（实数，包括正实数和负实数；包括八进制、十进制和十六进制）
     *
     * @param string
     */
    public static boolean isNumber(String string) {
        int length = string == null ? 0 : (string = string.trim()).length();
        if (length > 0) {
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
     * 是否是一个小数（实数，包括正实数和负实数；只验证十进制数）
     *
     * @param string
     */
    public static boolean isDouble(String string) {
        int length = string == null ? 0 : (string = string.trim()).length();
        if (length > 0) {
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
