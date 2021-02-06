package com.moon.processor.utils;

import java.util.*;

/**
 * @author benshaoye
 */
public enum Collect2 {
    ;

    public static int size(Collection<?> collect) { return collect == null ? 0 : collect.size(); }

    public static <T> List<? extends T> emptyIfNull(List<? extends T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    public static boolean isEmpty(Collection<?> collection) { return collection == null || collection.isEmpty(); }

    public static boolean isNotEmpty(Collection<?> collection) { return !isEmpty(collection); }

    @SafeVarargs
    public static <T> Set<T> set(T... values) {
        Set<T> list = new LinkedHashSet<>();
        if (values == null) {
            return list;
        }
        list.addAll(Arrays.asList(values));
        return list;
    }

    @SafeVarargs
    public static <T> List<T> list(T... values) {
        List<T> list = new ArrayList<>();
        if (values == null) {
            return list;
        }
        list.addAll(Arrays.asList(values));
        return list;
    }
}
