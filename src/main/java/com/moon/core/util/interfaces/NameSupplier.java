package com.moon.core.util.interfaces;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface NameSupplier<T> {
    /**
     * 获取 KEY
     *
     * @return
     */
    T getName();
}
