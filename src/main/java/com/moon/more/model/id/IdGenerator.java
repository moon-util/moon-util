package com.moon.more.model.id;

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
