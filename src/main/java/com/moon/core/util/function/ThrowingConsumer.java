package com.moon.core.util.function;

import com.moon.core.exception.DefaultException;

/**
 * @author benshaoye
 * @see java.util.function.Supplier
 */
@FunctionalInterface
public interface ThrowingConsumer<T> {
    /**
     * 消费
     *
     * @param value
     * @throws Throwable
     */
    void accept(T value) throws Throwable;

    /**
     * 应用并返回，如果异常，将包装成非检查异常抛出
     *
     * @param value
     * @return
     */
    default void orWithUnchecked(T value) {
        try {
            accept(value);
        } catch (Throwable t) {
            DefaultException.doThrow(t);
        }
    }
}
