package com.moon.implement;

/**
 * @author benshaoye
 */
public interface Implementor<T> {

    /**
     * new 一个新对象
     *
     * @return 新对象
     */
    T newInstance();
}
