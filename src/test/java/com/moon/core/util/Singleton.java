package com.moon.core.util;

/**
 * @author moonsky
 */
public abstract class Singleton {

    private static class SingletonImpl extends Singleton {

        private static final SingletonImpl INSTANCE = new SingletonImpl();

        private SingletonImpl() {}

        private void add() {}
    }

    public final static Singleton getInstance() {
        return SingletonImpl.INSTANCE;
    }
}
