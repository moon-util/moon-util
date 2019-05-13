package com.moon.core.util.function;

import com.moon.core.exception.DefaultException;

/**
 * @author benshaoye
 * @see java.util.function.Supplier
 */
@FunctionalInterface
public interface ThrowingBiConsumer<T, S> {
    /**
     * 消费
     *
     * @param value1
     * @param value2
     * @throws Throwable
     */
    void accept(T value1, S value2) throws Throwable;

    /**
     * 消费，如果异常，将包装成非检查异常抛出
     *
     * @param value1
     * @param value2
     */
    default void orWithUnchecked(T value1, S value2) {
        try {
            accept(value1, value2);
        } catch (Throwable t) {
            DefaultException.doThrow(t);
        }
    }
}
