package com.moon.processor.model;

/**
 * @author benshaoye
 */
public interface ThrowingConsumer<T> {

    /**
     * 处理
     *
     * @param t
     *
     * @throws Throwable
     */
    void accept(T t) throws Throwable;
}
