package com.moon.accessor.util;

/**
 * @author benshaoye
 */
public enum Math2 {
    ;

    public static int sum(int[] ints) {
        int value = 0;
        for (int anInt : ints) {
            value += anInt;
        }
        return value;
    }
}
