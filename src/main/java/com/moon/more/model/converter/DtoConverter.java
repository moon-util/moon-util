package com.moon.more.model.converter;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface DtoConverter<T> extends Converter {

    /**
     * convert to typeof T
     *
     * @return
     */
    T toDto();
}
