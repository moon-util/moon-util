package com.moon.core.util.interfaces;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface ValueSupplier<T> {
    /**
     * 获取值
     *
     * @return
     */
    T getValue();
}
