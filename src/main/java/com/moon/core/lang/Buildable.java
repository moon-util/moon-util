package com.moon.core.lang;

/**
 * @author benshaoye
 * @date 2018/9/14
 */
@FunctionalInterface
public interface Buildable<T> {
    /**
     * 构建一个指定类型对象
     *
     * @return
     */
    T build();
}
