package com.moon.more.model.id;

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
