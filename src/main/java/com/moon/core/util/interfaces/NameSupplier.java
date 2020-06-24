package com.moon.core.util.interfaces;

import java.util.function.Supplier;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface NameSupplier<T> {

    /**
     * 获取 name
     *
     * @return name
     */
    T getName();

    /**
     * transfer to a {@link Supplier}
     *
     * @return a new supplier
     */
    default Supplier<T> asNameSupplier() { return this::getName; }
}
