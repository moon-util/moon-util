package com.moon.core.util.function;

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
}
