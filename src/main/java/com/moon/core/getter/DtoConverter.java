package com.moon.core.getter;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface DtoConverter<T> {

    /**
     * convert to typeof T
     *
     * @return
     */
    T toDto();
}
