package com.moon.core.util.interfaces;

import java.util.Iterator;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface IteratorSupplier<T> {
    /**
     * 获取一个迭代器
     *
     * @param t
     * @return
     */
    Iterator<T> iterator(T t);
}
