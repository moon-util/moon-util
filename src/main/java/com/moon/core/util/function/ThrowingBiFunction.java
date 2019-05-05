package com.moon.core.util.function;

/**
 * @author benshaoye
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface ThrowingBiFunction<T, O, R> {
    /**
     * 应用并返回
     *
     * @param value1
     * @param value2
     * @return
     * @throws Throwable
     */
    R apply(T value1, O value2) throws Throwable;
}
