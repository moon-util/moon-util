package com.moon.accessor.util;

import java.util.Collection;
import java.util.Map;

/**
 * @author benshaoye
 */
public enum Functions2 {
    ;

    public static boolean isNull(Object value) { return value == null; }

    public static boolean isNotNull(Object value) { return value != null; }

    public static boolean isEmpty(CharSequence sequence) {
        return sequence == null || sequence.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence sequence) { return !isEmpty(sequence); }

    public static boolean isEmpty(Collection<?> collect) { return collect == null || collect.isEmpty(); }

    public static boolean isNotEmpty(Collection<?> collect) { return !isEmpty(collect); }

    public static boolean isEmpty(Map<?, ?> map) { return map == null || map.isEmpty(); }

    public static boolean isNotEmpty(Map<?, ?> map) { return !isEmpty(map); }

    public static boolean isBlank(CharSequence sequence) {
        int length = sequence == null ? 0 : sequence.length();
        if (length == 0) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(sequence.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence sequence) { return !isBlank(sequence); }
}
