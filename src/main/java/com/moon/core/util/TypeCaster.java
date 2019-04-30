package com.moon.core.util;

import java.util.function.Function;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface TypeCaster<R> extends Function<Object, R> {
    /**
     * Alias for apply
     *
     * @param o
     * @return
     */
    R cast(Object o);

    /**
     * Applies this function to the given argument.
     *
     * @param o the function argument
     * @return the function result
     */
    @Override
    default R apply(Object o) {
        return cast(o);
    }
}
