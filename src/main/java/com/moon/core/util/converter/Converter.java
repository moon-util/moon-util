package com.moon.core.util.converter;

import java.util.function.Function;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface Converter<T, R> extends Function<T, R> {
    /**
     * Alias for apply
     *
     * @param o
     * @return
     */
    R convert(T o);

    /**
     * Applies this function to the given argument.
     *
     * @param o the function argument
     * @return the function result
     */
    @Override
    default R apply(T o) { return convert(o); }
}
