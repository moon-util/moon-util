package com.moon.office.excel.core;

import com.moon.core.lang.ThrowUtil;

/**
 * @author benshaoye
 */
final class OtherUtil {
    private OtherUtil() {
        ThrowUtil.noInstanceError();
    }

    final static String TRUE = Boolean.TRUE.toString();
    final static String FALSE = Boolean.FALSE.toString();
    final static String cutWrapped(String when, String[] formattedDelimiters) {
        String str1 = formattedDelimiters[0];
        String str2 = formattedDelimiters[1];
        int length1 = str1.length();
        int length2 = str2.length();
        int index2 = when.indexOf(str2, length1);
        if (index2 + length2 == when.length()) {
            return when.substring(length1, index2).trim();
        } else {
            throw new IllegalArgumentException(when);
        }
    }
}
