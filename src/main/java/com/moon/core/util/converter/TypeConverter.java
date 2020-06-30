package com.moon.core.util.converter;

/**
 * @author moonsky
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
    R convert(Object o);
}
