package com.moon.core.util.interfaces;

import java.util.function.Supplier;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface ValueSupplier<T> {

    /**
     * 获取值
     *
     * @return value
     */
    T getValue();

    /**
     * transfer to a {@link Supplier}
     *
     * @return a new supplier
     */
    default Supplier<T> asValueSupplier() { return this::getValue; }
}
