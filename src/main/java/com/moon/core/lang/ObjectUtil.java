package com.moon.core.lang;

import java.util.Objects;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class ObjectUtil {

    private ObjectUtil() { noInstanceError(); }

    public static <T> T defaultIfNull(T obj, T defaultValue) { return obj == null ? defaultValue : obj; }

    public static boolean equals(Object o1, Object o2) {
        return Objects.equals(o1, o2);
    }

    public static boolean contentEquals(ContentEquals a, ContentEquals b) {
        return (a == b) || (a != null && a.contentEquals(b));
    }
}
