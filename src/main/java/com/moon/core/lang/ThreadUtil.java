package com.moon.core.lang;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class ThreadUtil {

    private ThreadUtil() { noInstanceError(); }

    public static Thread current() { return Thread.currentThread(); }

    public static void start(Runnable runnable) { of(runnable).start(); }

    public static Thread of(Runnable runnable) {
        return runnable == null ? new Thread()
            : (runnable instanceof Thread ? (Thread) runnable : new Thread(runnable));
    }
}
