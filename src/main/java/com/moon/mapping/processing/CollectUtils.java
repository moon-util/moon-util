package com.moon.mapping.processing;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author benshaoye
 */
final class CollectUtils {

    private CollectUtils() {}

    static <K, V> Map<K, V> simpleGroup(Collection<V> collect, Function<V, K> grouper) {
        int size = collect == null ? 0 : collect.size();
        Map<K, V> grouped = new HashMap<>(size);
        if (size == 0) {
            return grouped;
        }
        for (V v : collect) {
            grouped.put(grouper.apply(v), v);
        }
        return grouped;
    }

    static <F, T> T reduce(Iterable<? extends F> collect, BiFunction<T, F, T> converter, T totalValue) {
        if (collect != null) {
            for (F item : collect) {
                totalValue = converter.apply(totalValue, item);
            }
        }
        return totalValue;
    }

    static <E> List<E> emptyIfNull(List<E> list) {
        return list == null ? new ArrayList<>() : list;
    }
}
