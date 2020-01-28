package com.moon.core.util.function;

/**
 * @author ZhangDongMin
 */
@FunctionalInterface
public interface IntBiConsumer<T> {
    /**
     * valuesList handler
     *
     * @param value
     * @param index
     */
    void accept(T value, int index);
}
