package com.moon.core.util.function;

import com.moon.core.exception.DefaultException;

/**
 * 区别{@link Runnable}}
 *
 * @author benshaoye
 */
@FunctionalInterface
public interface ThrowingRunnable {
    /**
     * 运行
     *
     * @throws Throwable
     */
    void run() throws Throwable;

    /**
     * 应用并返回，如果异常，将包装成非检查异常抛出
     *
     * @return
     */
    default void orWithUnchecked() {
        try {
            run();
        } catch (Throwable t) {
            DefaultException.doThrow(t);
        }
    }
}
