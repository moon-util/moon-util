package com.moon.processor.utils;

import java.util.Collection;

/**
 * @author benshaoye
 */
public enum Collect2 {

    ;

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
}
