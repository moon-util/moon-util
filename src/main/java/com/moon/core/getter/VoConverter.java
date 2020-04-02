package com.moon.core.getter;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface VoConverter<T> {

    /**
     * convert to typeof T
     *
     * @return
     */
    T toVo();
}
