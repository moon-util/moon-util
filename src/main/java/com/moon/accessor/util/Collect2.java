package com.moon.accessor.util;

import java.util.Collection;

/**
 * @author benshaoye
 */
public abstract class Collect2 {

    protected Collect2() { throw new AssertionError("No Collect2 instance for you."); }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Collection<?> collect) {
        return collect == null || collect.isEmpty();
    }
}
