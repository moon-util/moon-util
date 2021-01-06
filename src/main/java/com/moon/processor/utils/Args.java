package com.moon.processor.utils;

import java.util.Collection;

/**
 * @author benshaoye
 */
public interface Args<C extends Collection<?>, T> {

    /**
     * 使用
     *
     * @param collect 集合
     * @param ts      数据
     */
    void with(C collect, T... ts);
}
