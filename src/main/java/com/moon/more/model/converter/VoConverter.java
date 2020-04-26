package com.moon.more.model.converter;

/**
 * @author benshaoye
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
