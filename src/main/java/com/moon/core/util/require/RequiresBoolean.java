package com.moon.core.util.require;

/**
 * @author benshaoye
 */
interface RequiresBoolean extends RequiresFail {
    /**
     * 是否是 true
     *
     * @param value
     * @return
     */
    default boolean requireTrue(boolean value) {
        return success("Test result: {}\n\tExpect: {}\n\tActual: {}", value, true, value);
    }

    /**
     * 是否是 false
     *
     * @param value
     * @return
     */
    default boolean requireFalse(boolean value) {
        return success("Test result: {}\n\tExpect: {}\n\tActual: {}", !value, false, value);
    }
}
