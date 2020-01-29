package com.moon.core.lang;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface Buildable<T> {
    /**
     * 构建一个指定类型对象
     *
     * @return 构建对象
     */
    T build();
}
