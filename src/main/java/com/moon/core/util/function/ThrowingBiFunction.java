package com.moon.core.util.function;

import com.moon.core.exception.DefaultException;

/**
 * @author benshaoye
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface ThrowingBiFunction<T, O, R> {
    /**
     * 应用并返回
     *
     * @param value1
     * @param value2
     * @return
     * @see Throwable
     */
    R apply(T value1, O value2) throws Throwable;

    /**
     * 应用并返回，如果异常，将包装成非检查异常抛出
     *
     * @param value1
     * @param value2
     */
    default R orWithUnchecked(T value1, O value2) {
        try {
            return apply(value1, value2);
        } catch (Throwable t) {
            throw DefaultException.with(t);
        }
    }
}
