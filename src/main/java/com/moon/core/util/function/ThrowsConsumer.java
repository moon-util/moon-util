package com.moon.core.util.function;

/**
 * @author benshaoye
 * @see java.util.function.Supplier
 */
@FunctionalInterface
public interface ThrowsConsumer<T> {
    /**
     * 消费
     *
     * @param value
     * @throws Throwable
     */
    void accept(T value) throws Throwable;
}
