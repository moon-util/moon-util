package com.moon.accessor.util;

import java.util.Collection;

/**
 * @author benshaoye
 */
public enum Collect2 {
    ;

    public static boolean isEmpty(Collection<?> collect) {
        return collect == null || collect.isEmpty();
    }
}
