package com.moon.more.model;

import java.util.function.Function;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface Encoder<T, R> extends Function<T, R> {

    /**
     * encode
     *
     * @param data
     *
     * @return
     */
    R encode(T data);

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     *
     * @return the function result
     */
    @Override
    default R apply(T t) { return encode(t); }
}
