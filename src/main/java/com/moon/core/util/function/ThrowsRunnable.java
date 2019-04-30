package com.moon.core.util.function;

/**
 * 区别{@link Runnable}}
 *
 * @author benshaoye
 */
@FunctionalInterface
public interface ThrowsRunnable {
    /**
     * 运行
     *
     * @throws Throwable
     */
    void run() throws Throwable;
}
