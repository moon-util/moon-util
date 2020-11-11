package com.moon.mapping.processing;

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
