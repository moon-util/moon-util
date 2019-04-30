package com.moon.core.util.able;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface Stringify<T> {
    /**
     * 字符串化对象
     *
     * @param t
     * @return
     */
    String stringify(T t);
}
