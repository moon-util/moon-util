package com.moon.core.util.function;

import com.moon.core.exception.DefaultException;

/**
 * @author benshaoye
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface ThrowingFunction<T, R> {
    /**
     * 应用并返回
     *
     * @param value
     * @return
     * @throws Throwable
     */
    R apply(T value) throws Throwable;

    /**
     * 应用并返回，如果异常，将包装成非检查异常抛出
     *
     * @param value
     * @return
     */
    default R orWithUnchecked(T value) {
        try {
            return apply(value);
        } catch (Throwable t) {
            throw DefaultException.with(t);
        }
    }
}
