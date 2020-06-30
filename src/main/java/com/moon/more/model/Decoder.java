package com.moon.more.model;

import java.util.function.Function;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface Decoder<T, R> extends Function<T, R> {

    /**
     * encode
     *
     * @param data
     *
     * @return
     */
    R decode(T data);

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     *
     * @return the function result
     */
    @Override
    default R apply(T t) { return decode(t); }
}
