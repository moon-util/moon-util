package com.moon.more.model.converter;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface SnapshotConverter<T> extends Converter {

    /**
     * convert to typeof T
     *
     * @return
     */
    T toSnapshot();
}
