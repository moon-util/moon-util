package com.moon.mapper.convert;

/**
 * @author benshaoye
 */
public enum Default {
    ;

    public static <T> T ifNull(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }
}
