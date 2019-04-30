package com.moon.core.util.require;

/**
 * @author benshaoye
 */
interface RequiresString extends RequiresFail {
    /**
     * 是否是空字符串
     *
     * @param value
     * @return
     */
    default boolean requireEmpty(String value) {
        return success("Test result: {}\n\tExpect: {}\n\tActual: {}",
            value == null || value.length() == 0, "<null or \"\">", value);
    }

    /**
     * 是否是空字符串
     *
     * @param value
     * @return
     */
    default boolean requireNotEmpty(String value) {
        return success("Test result: {}\n\tExpect: {}\n\tActual: {}",
            value != null && value.length() > 0, "<not null and not an EMPTY string>", value);
    }

    /**
     * 是否是空字符串
     *
     * @param value
     * @return
     */
    default boolean requireBlank(String value) {
        return success("Test result: {}\n\tExpect: {}\n\tActual: {}",
            value == null || value.trim().length() == 0, "<null, \"\" or like \" \">", value);
    }

    /**
     * 是否是空字符串
     *
     * @param value
     * @return
     */
    default boolean requireNotBlank(String value) {
        return success("Test result: {}\n\tExpect: {}\n\tActual: {}",
            value != null && value.trim().length() > 0, "<not null and all char isn't blank>", value);
    }
}
