package com.moon.core.util.interfaces;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface KeySupplier<T> {
    /**
     * 获取 KEY
     *
     * @return
     */
    T getKey();
}
