package com.moon.implement;

/**
 * @author benshaoye
 */
public abstract class AutoFactory {

    private AutoFactory() { }

    public static <T> Implementor<T> getImplementor(Class<T> interfaceClass) {
        throw new UnsupportedOperationException();
    }

    public static <T> T newInstance(Class<T> interfaceClass) {
        return getImplementor(interfaceClass).newInstance();
    }
}
