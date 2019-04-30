package com.moon.core.util;

import com.moon.core.enums.Collects;
import com.moon.core.lang.ThrowUtil;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public final class GroupUtil {
    private GroupUtil() {
        ThrowUtil.noInstanceError();
    }

    public static <K, E> Map<K, List<E>> groupAsList(E[] arr, Function<? super E, ? extends K> function) {
        final Supplier supplier = Collects.ArrayList;
        return groupBy(arr, function, supplier);
    }

    public static <K, E> Map<K, Set<E>> groupAsSet(E[] arr, Function<? super E, ? extends K> function) {
        final Supplier supplier = Collects.HashSet;
        return groupBy(arr, function, supplier);
    }

    public static <E, K, CR extends Collection<E>>

    Map<K, CR> groupBy(E[] arr, Function<? super E, ? extends K> function, Supplier<CR> groupingSupplier) {
        Map<K, CR> grouped = new HashMap<>();
        final int len = arr == null ? 0 : arr.length;
        for (int i = 0; i < len; i++) {
            grouping(arr[i], grouped, function, groupingSupplier);
        }
        return grouped;
    }

    public static <K, E, L extends List<E>> Map<K, List<E>> groupAsList(L list, Function<? super E, ? extends K> function) {
        final Supplier supplier = Collects.getOrDefault(list, Collects.ArrayList);
        return groupBy(list, function, supplier);
    }

    public static <K, E, S extends Set<E>> Map<K, Set<E>> groupAsSet(S set, Function<? super E, ? extends K> function) {
        final Supplier supplier = Collects.getOrDefault(set, Collects.HashSet);
        return groupBy(set, function, supplier);
    }

    public static <K, E, C extends Collection<E>> Map<K, Collection<E>> groupBy(C collect, Function<? super E, ? extends K> function) {
        final Supplier supplier = Collects.getOrDefault(collect, Collects.ArrayList);
        return groupBy(collect, function, supplier);
    }

    public static <K, E, C extends Collection<E>, CR extends Collection<E>>

    Map<K, CR> groupBy(C collect, Function<? super E, ? extends K> function, Supplier<CR> groupingSupplier) {
        Map<K, CR> grouped = new HashMap<>();
        if (collect != null) {
            for (E item : collect) {
                grouping(item, grouped, function, groupingSupplier);
            }
        }
        return grouped;
    }

    private final static <E, K, CR extends Collection<E>> void grouping(
        E item, Map<K, CR> grouped, Function<? super E, ? extends K> function, Supplier<CR> groupingSupplier
    ) {
        K key = function.apply(item);
        CR group = grouped.get(key);
        if (group == null) {
            grouped.put(key, group = groupingSupplier.get());
        }
        group.add(item);
    }
}
