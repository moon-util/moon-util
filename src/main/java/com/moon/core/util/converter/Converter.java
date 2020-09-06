package com.moon.core.util.converter;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface Converter<T, R> {

    /**
     * Alias for apply
     *
     * @param o
     *
     * @return
     */
    R convertTo(T o);
}
