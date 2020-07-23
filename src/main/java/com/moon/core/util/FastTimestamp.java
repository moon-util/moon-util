package com.moon.core.util;

import java.util.Date;
import java.util.function.LongSupplier;

/**
 * 并发场景下获取当前毫秒数，单独用一个线程不断获取并保存毫秒数
 *
 * @author moonsky
 */
public final class FastTimestamp implements LongSupplier {

    private static volatile FastTimestamp NEW_DATE;
    private static volatile FastTimestamp THREAD;

    private final LongSupplier supplier;
    private long timestamp;

    @SuppressWarnings("all")
    private FastTimestamp(Type type) {
        if (type == null || type == Type.NEW_DATE) {
            supplier = () -> new Date().getTime();
        } else {
            new Thread(() -> timestamp = System.currentTimeMillis());
            supplier = () -> timestamp;
        }
    }

    public static FastTimestamp getNewDateInstance() {
        if (NEW_DATE != null) {
            return NEW_DATE;
        }
        synchronized (FastTimestamp.class) {
            if (NEW_DATE != null) {
                return NEW_DATE;
            }
            NEW_DATE = new FastTimestamp(null);
        }
        return NEW_DATE;
    }

    public static FastTimestamp getThreadInstance() {
        if (THREAD != null) {
            return THREAD;
        }
        synchronized (FastTimestamp.class) {
            if (THREAD != null) {
                return THREAD;
            }
            THREAD = new FastTimestamp(Type.THREAD);
        }
        return THREAD;
    }

    /**
     * 获取当前毫秒数
     *
     * @return 时间戳
     */
    public long getTimestamp() { return supplier.getAsLong(); }

    @Override
    public long getAsLong() { return supplier.getAsLong(); }

    public enum Type {
        /**
         * 用一个新线程不断获取系统时间戳，其他线程从这里获取
         * 这样保证系统时间“永远只有”一个线程在访问，不会出现并发问题
         */
        THREAD,
        NEW_DATE,
    }
}
