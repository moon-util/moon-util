package com.moon.core.util.require;

import java.util.Objects;

/**
 * @author benshaoye
 */
interface RequiresObject extends RequiresFail {

    /**
     * 是否是 null
     *
     * @param value
     * @return
     */
    default boolean requireNull(Object value) {
        return success("Test result: {}\n\tExpect: {}\n\tActual: {}",
            value == null, null, value);
    }

    /**
     * 是否不是 null
     *
     * @param value
     * @return
     */
    default boolean requireNotNull(Object value) {
        return success("Test result: {}\n\tExpect: {}\n\tActual: {}",
            value != null, "<not null>", value);
    }

    /**
     * value1 和 value2 是否是同一个对象
     *
     * @param value1
     * @param value2
     * @return
     */
    default boolean requireSame(Object value1, Object value2) {
        return success("Test result: {}\n\tValue1: {}\n\tValue2: {}",
            value1 == value2, value1, value2);
    }

    /**
     * value1 和 value2 是否不是同一个对象
     *
     * @param value1
     * @param value2
     * @return
     */
    default boolean requireNotSame(Object value1, Object value2) {
        return success("Test result: {}\n\tValue1: {}\n\tValue2: {}",
            value1 != value2, value1, value2);
    }

    /**
     * 是否相等
     *
     * @param value1
     * @param value2
     * @return
     */
    default boolean requireEquals(Object value1, Object value2) {
        return success("Test result: {}\n\tValue1: {}\n\tValue2: {}",
            Objects.equals(value1, value2), value1, value2);
    }

    /**
     * 是否不相等
     *
     * @param value1
     * @param value2
     * @return
     */
    default boolean requireNotEquals(Object value1, Object value2) {
        return success("Test result: {}\n\tValue1: {}\n\tValue2: {}",
            !Objects.equals(value1, value2), value1, value2);
    }

    /**
     * 是否是指定类型的实例
     *
     * @param value
     * @param expectType
     * @return
     */
    default boolean requireInstanceOf(Object value, Class expectType) {
        return success("Test result: {}\n\tExpect type: {}\n\tActual type: {}",
            expectType.isInstance(value), expectType,
            value == null ? "<null>.class" : value.getClass());
    }

    /**
     * 是否不是指定类型的实例
     *
     * @param value
     * @param expectType
     * @return
     */
    default boolean requireNotInstanceOf(Object value, Class expectType) {
        return success("Test result: {}\n\tExpect not an instance of type: {}\n\tActual type: {}",
            !expectType.isInstance(value), expectType,
            value == null ? "<null>.class" : value.getClass());
    }
}
