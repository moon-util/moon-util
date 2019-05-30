package com.moon.core.util.interfaces;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface Parser<T, S> {

    /**
     * 执行解析
     *
     * @param source
     *
     * @return
     */
    T parse(S source);
}
