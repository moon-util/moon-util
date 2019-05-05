package com.moon.core.util.function;

/**
 * @author benshaoye
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface ThrowingFunction<T, R> {
    /**
     * 应用并返回
     *
     * @param value
     * @return
     * @throws Throwable
     */
    R apply(T value) throws Throwable;
}
