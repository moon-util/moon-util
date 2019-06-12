package com.moon.core.util.converter;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface Converter<T, R>{
    /**
     * Alias for apply
     *
     * @param o
     * @return
     */
    R convert(T o);
}
