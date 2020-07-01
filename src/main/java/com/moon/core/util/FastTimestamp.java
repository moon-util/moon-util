package com.moon.core.util;

import java.util.Date;
import java.util.function.LongSupplier;

/**
 * 并发场景下获取当前毫秒数，单独用一个线程不断获取并保存毫秒数，或者用 new Date().getTime() 方式获取
 *
 * @author moonsky
 */
public final class FastTimestamp {

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

    public enum Type {
        /**
         * 用一个新线程不断获取系统时间戳，其他线程从这里获取
         * 这样保证系统时间永远只有一个线程在访问，不会出现并发问题
         * 至于时间重复，在毫秒级时间获取下，重复的概率本来就很高
         */
        THREAD,
        NEW_DATE,
    }
}
