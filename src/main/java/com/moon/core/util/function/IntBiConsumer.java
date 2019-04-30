package com.moon.core.util.function;

/**
 * @author ZhangDongMin
 * @date 2018/9/11
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
