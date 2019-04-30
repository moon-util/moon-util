package com.moon.core.util.function;

/**
 * @author benshaoye
 * @see java.util.function.Supplier
 */
@FunctionalInterface
public interface ThrowsSupplier<T> {
    /**
     * 运行并获取值
     *
     * @return
     * @throws Throwable
     */
    T get() throws Throwable;
}
