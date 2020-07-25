package com.moon.core.util;

import java.util.function.LongSupplier;

/**
 * 并发场景下获取当前毫秒数，单独用一个线程不断获取并保存毫秒数
 *
 * @author moonsky
 */
public final class FastTimestamp implements LongSupplier {

    public final static FastTimestamp INSTANCE = new FastTimestamp();

    private FastTimestamp() { }

    public static FastTimestamp getInstance() { return INSTANCE; }

    public static long getTimeInMillis() { return FastTimestampRunner.TIMER.value; }

    @Override
    public long getAsLong() { return getTimeInMillis(); }

    private enum FastTimestampRunner {
        /**
         * 单例计数器
         */
        TIMER;
        private long value;

        FastTimestampRunner() {
            Thread runner = new Thread(() -> {
                while (true) {
                    value = System.currentTimeMillis();
                }
            });
            runner.setName(Thread.currentThread().getName() + ":" + getClass().getSimpleName());
            runner.setDaemon(true);
            runner.start();
        }
    }
}
