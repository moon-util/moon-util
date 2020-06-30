package com.moon.more.model.converter;

/**
 * @author moonsky
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
