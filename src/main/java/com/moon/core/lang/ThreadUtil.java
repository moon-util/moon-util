package com.moon.core.lang;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class ThreadUtil {
    private ThreadUtil() {
        noInstanceError();
    }

    public final static void start(Runnable runnable) {
        new Thread(runnable).start();
    }
}
