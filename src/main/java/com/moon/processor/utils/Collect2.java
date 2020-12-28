package com.moon.processor.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author benshaoye
 */
public enum Collect2 {
    ;

    public static <T> List<? extends T> emptyIfNull(List<? extends T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
}
