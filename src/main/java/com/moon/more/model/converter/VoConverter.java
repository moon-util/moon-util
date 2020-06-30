package com.moon.more.model.converter;

/**
 * @author moonsky
 */
@FunctionalInterface
public interface VoConverter<T> extends Converter {

    /**
     * convert to typeof T
     *
     * @return
     */
    T toVo();
}
