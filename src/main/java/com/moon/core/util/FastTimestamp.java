package com.moon.core.util;

import java.util.function.LongSupplier;

/**
 * 并发场景下获取当前毫秒数，单独用一个线程不断获取并保存毫秒数
 *
 * @author moonsky
 */
public final class FastTimestamp implements LongSupplier {

    public static long getTimeInMillis() {
        return FastTimestampRunner.TIMER.value;
    }

    @Override
    public long getAsLong() { return getTimeInMillis(); }

    private enum FastTimestampRunner {
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
