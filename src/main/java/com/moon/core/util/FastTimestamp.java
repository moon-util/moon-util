package com.moon.core.util;

/**
 * 并发场景下获取当前毫秒数，单独用一个线程不断获取并保存毫秒数
 *
 * @author moonsky
 */
public final class FastTimestamp {

    private long timestamp = System.currentTimeMillis();

    FastTimestamp() {
        new Thread(() -> timestamp = System.currentTimeMillis());
    }

    public long getTimestamp() {
        return timestamp;
    }

    private enum Type {
        /**
         * 用一个新线程不断获取系统时间戳，其他线程从这里获取
         * 这样保证系统时间永远只有一个线程在访问，不会出现并发问题
         * 至于时间重复，在毫秒级时间获取下，重复的概率本来就很高
         */
        THREAD,
        NEW_DATE
    }
}
