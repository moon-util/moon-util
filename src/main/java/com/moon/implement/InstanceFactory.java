package com.moon.implement;

import java.util.function.Supplier;

/**
 * @author moonsky
 */
public abstract class InstanceFactory {

    private InstanceFactory() { }

    public static <T> T newInstance(Class<T> interfaceClass) {
        throw new UnsupportedOperationException();
    }

    public static <T> Supplier<T> get(Class<T> interfaceClass) {
        throw new UnsupportedOperationException();
    }
}
