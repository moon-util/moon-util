package com.moon.core.util.require;

/**
 * @author benshaoye
 */
interface RequiresMath extends RequiresFail {
    /**
     * 两个数值是否相等
     *
     * @param value
     * @param value1
     * @return
     */
    default boolean requireEq(long value, long value1) {
        return success("Test result: {}\n\tValue1: {}\n\tValue2: {}",
            value == value1, value1, value);
    }
    /**
     * 两个数值是否相等
     *
     * @param value
     * @param value1
     * @return
     */
    default boolean requireEq(double value, double value1) {
        return success("Test result: {}\n\tValue1: {}\n\tValue2: {}",
            value == value1, value1, value);
    }

    /**
     * value 是否小于 value1
     *
     * @param value
     * @param value1
     * @return
     */
    default boolean requireLt(long value, long value1) {
        return success("Test result: {}\n\tExpect: less than {}\n\tActual: {}",
            value < value1, value1, value);
    }

    /**
     * value 是否小于 value1
     *
     * @param value
     * @param value1
     * @return
     */
    default boolean requireLt(double value, double value1) {
        return success("Test result: {}\n\tExpect: less than {}\n\tActual: {}",
            value < value1, value1, value);
    }

    /**
     * value 是否大于 value1
     *
     * @param value
     * @param value1
     * @return
     */
    default boolean requireGt(long value, long value1) {
        return success("Test result: {}\n\tExpect: great than {}\n\tActual: {}",
            value > value1, value1, value);
    }

    /**
     * value 是否大于 value1
     *
     * @param value
     * @param value1
     * @return
     */
    default boolean requireGt(double value, double value1) {
        return success("Test result: {}\n\tExpect: great than {}\n\tActual: {}",
            value > value1, value1, value);
    }
}
