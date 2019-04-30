package com.moon.core.enums;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 排序实现
 *
 * @author benshaoye
 */
@FunctionalInterface
public interface Sortable<T> {
    /**
     * 具体实现
     *
     * @param arr
     * @param comparator
     * @return
     */
    T[] sort(T[] arr, Comparator<T> comparator);

    /**
     * 通过 toString 方法排序
     *
     * @param arr
     * @return
     */
    default T[] sort(T[] arr) {
        return sort(arr, Comparator.comparing(String::valueOf));
    }

    default int[] sort(int[] ints) {
        Arrays.sort(ints);
        return ints;
    }

    default long[] sort(long[] longs) {
        Arrays.sort(longs);
        return longs;
    }

    default float[] sort(float[] floats) {
        Arrays.sort(floats);
        return floats;
    }

    default double[] sort(double[] doubles) {
        Arrays.sort(doubles);
        return doubles;
    }

    default char[] sort(char[] chars) {
        Arrays.sort(chars);
        return chars;
    }

    default short[] sort(short[] shorts) {
        Arrays.sort(shorts);
        return shorts;
    }

    default byte[] sort(byte[] bytes) {
        Arrays.sort(bytes);
        return bytes;
    }

    default boolean[] sort(boolean[] booleans) {
        return booleans;
    }
}
