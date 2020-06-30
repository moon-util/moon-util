package com.moon.core.lang;

import java.util.Objects;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class ObjectUtil {

    private ObjectUtil() { noInstanceError(); }

    public static <T> T defaultIfNull(T obj, T defaultValue) { return obj == null ? defaultValue : obj; }

    public static boolean equals(Object o1, Object o2) { return Objects.equals(o1, o2); }

    public static boolean contentEquals(ContentEquals a, ContentEquals b) {
        return (a == b) || (a != null && a.contentEquals(b));
    }

    public static <T> T getInitializeValue(Class<T> type) {
        if (type == null) { return null; }
        if (type.isPrimitive()) {
            if (type == int.class) { return (T) Integer.valueOf(0); }
            if (type == long.class) { return (T) Long.valueOf(0); }
            if (type == double.class) { return (T) Double.valueOf(0); }
            if (type == boolean.class) { return (T) Boolean.FALSE; }
            if (type == byte.class) { return (T) Byte.valueOf((byte) 0); }
            if (type == short.class) { return (T) Short.valueOf((short) 0); }
            if (type == float.class) { return (T) Float.valueOf(0); }
            if (type == char.class) { return (T) Character.valueOf(' '); }
        }
        return null;
    }
}
