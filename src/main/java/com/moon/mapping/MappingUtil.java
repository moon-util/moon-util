package com.moon.mapping;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public abstract class MappingUtil {

    private MappingUtil() { noInstanceError(); }

    public static <F, T> BeanMapping<F, T> resolve(Class<F> fromClass, Class<T> toClass) {
        return Mappings.resolve(fromClass, toClass);
    }

    public static <T> MapMapping<T> resolve(Class<T> fromClass) {
        return Mappings.resolve(fromClass);
    }
}
