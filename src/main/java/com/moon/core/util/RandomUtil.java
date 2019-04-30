package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机数工具，线程安全
 *
 * @author benshaoye
 */
public final class RandomUtil {
    private RandomUtil() {
        ThrowUtil.noInstanceError();
    }

    private volatile static ThreadLocalRandom random = current();

    public final static ThreadLocalRandom get() {
        ThreadLocalRandom r = random;
        return r == null ? next() : r;
    }

    public final static synchronized ThreadLocalRandom next() {
        return random = current();
    }

    public final static ThreadLocalRandom current() {
        return ThreadLocalRandom.current();
    }

    public final static int nextInt() {
        return get().nextInt();
    }

    public final static int nextInt(int bound) {
        return get().nextInt(bound);
    }

    public final static int nextInt(int min, int max) {
        return get().nextInt(min, max);
    }

    public final static long nextLong() {
        return get().nextLong();
    }

    public final static long nextLong(long bound) {
        return get().nextLong(bound);
    }

    public final static long nextLong(long min, long max) {
        return get().nextLong(min, max);
    }

    public final static double nextDouble() {
        return get().nextDouble();
    }

    public final static double nextDouble(double bound) {
        return get().nextDouble(bound);
    }

    public final static double nextDouble(double min, double max) {
        return get().nextDouble(min, max);
    }

    public final static boolean nextBoolean() {
        return get().nextBoolean();
    }

    /**
     * 数组乱序
     *
     * @param arr
     * @param <T>
     * @return
     */
    public final static <T> T[] randomOrder(T[] arr) {
        Arrays.sort(arr, (o1, o2) -> nextInt());
        return arr;
    }
}
