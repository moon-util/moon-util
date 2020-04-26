package com.moon.more.model.converter;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface EntityConverter<T> extends Converter {

    /**
     * convert to typeof T
     *
     * @return
     */
    T toEntity();
}
