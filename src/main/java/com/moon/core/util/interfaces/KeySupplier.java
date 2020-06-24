package com.moon.core.util.interfaces;

import java.util.function.Supplier;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface KeySupplier<T> {

    /**
     * 获取 KEY
     *
     * @return key
     */
    T getKey();

    /**
     * transfer to a {@link Supplier}
     *
     * @return a new supplier
     */
    default Supplier<T> asKeySupplier() { return this::getKey; }
}
