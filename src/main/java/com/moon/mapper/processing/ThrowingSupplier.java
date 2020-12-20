package com.moon.mapper.processing;

/**
 * @author benshaoye
 */
@FunctionalInterface
interface ThrowingSupplier<T> {

    /**
     * get an object
     *
     * @return object
     */
    T get();
}
