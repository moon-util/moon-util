package com.moon.core.util.interfaces;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface IdSupplier<T> {
    /**
     * 获取 ID
     *
     * @return
     */
    T getId();
}
