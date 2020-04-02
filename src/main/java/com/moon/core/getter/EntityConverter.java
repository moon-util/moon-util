package com.moon.core.getter;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface EntityConverter<T> {

    /**
     * convert to typeof T
     *
     * @return
     */
    T toEntity();
}
