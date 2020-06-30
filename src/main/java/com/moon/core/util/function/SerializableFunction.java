package com.moon.core.util.function;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     *
     * @return the function result
     */
    @Override
    R apply(T t);
}
