package com.moon.core.util.interfaces;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface EndSupplier<T> {
    /**
     * 结束，返回源对象
     *
     * @return
     */
    T end();
}
