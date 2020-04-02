package com.moon.core.getter;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface SnapshotConverter<T> {

    /**
     * convert to typeof T
     *
     * @return
     */
    T toSnapshot();
}
