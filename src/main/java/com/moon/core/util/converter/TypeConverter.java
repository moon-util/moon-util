package com.moon.core.util.converter;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface TypeConverter<R> extends Converter<Object, R> {
    /**
     * Alias for apply
     *
     * @param o
     * @return
     */
    @Override
    R cast(Object o);
}
