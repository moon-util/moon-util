package com.moon.core.util.interfaces;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface IdGenerator<T> {
    /**
     * 下一个 ID
     *
     * @return
     */
    T nextId();
}
