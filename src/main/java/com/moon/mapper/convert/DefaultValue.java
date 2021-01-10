package com.moon.mapper.convert;

/**
 * @author benshaoye
 */
public enum DefaultValue {
    ;

    public static <T> T ifNull(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static String ifEmpty(String value, String defaultValue) {
        return value == null || value.isEmpty() ? defaultValue : value;
    }
}
