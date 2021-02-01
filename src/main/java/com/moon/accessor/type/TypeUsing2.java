package com.moon.accessor.type;

import com.moon.accessor.exception.Exception2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

/**
 * @author benshaoye
 */
enum TypeUsing2 {
    ;

    static final int[] EMPTY_TYPES = {};

    static int[] as(int... types) { return types; }

    static <T> Object useIfNonNull(T value, Function<T, Object> converter) {
        return value == null ? null : converter.apply(value);
    }

    static URL toURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw Exception2.with(e, "URL error: " + url);
        }
    }

    static String toKey(Object jdbcType, Class<?> supportClass) {
        return jdbcType + ":" + supportClass;
    }
}
