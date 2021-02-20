package com.moon.processor.utils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
public enum Collect2 {
    ;

    public static int size(Collection<?> collect) { return collect == null ? 0 : collect.size(); }

    public static <T> List<? extends T> emptyIfNull(List<? extends T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    @SafeVarargs
    public static <T, C extends Collection<T>> C addAll(C collect, T... values) {
        if (values != null) {
            Collections.addAll(collect, values);
        }
        return collect;
    }

    @SafeVarargs
    public static <E, T, C extends Collection<T>> C addAll(Function<E, T> mapper, C collect, E... values) {
        if (values != null) {
            collect.addAll(Arrays.stream(values).map(mapper).collect(Collectors.toList()));
        }
        return collect;
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
