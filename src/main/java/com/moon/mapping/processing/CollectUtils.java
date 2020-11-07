package com.moon.mapping.processing;

import java.util.*;
import java.util.function.BiFunction;

/**
 * @author benshaoye
 */
final class CollectUtils {

    private CollectUtils() {}

    static <F, T> T reduceReversed(Collection<? extends F> collect, BiFunction<T, F, T> converter, T totalValue) {
        List<? extends F> list = new ArrayList<>(collect);
        Collections.reverse(list);
        for (F property : list) {
            totalValue = converter.apply(totalValue, property);
        }
        return totalValue;
    }

    static <F, T> T reduce(Iterable<? extends F> collect, BiFunction<T, F, T> converter, T totalValue) {
        if (collect != null) {
            for (F item : collect) {
                totalValue = converter.apply(totalValue, item);
            }
        }
        return totalValue;
    }

    static <F, T> T reduce(Iterable<? extends F> collect, BiIntFunction<T, F, T> converter, T totalValue) {
        if (collect != null) {
            int index = 0;
            for (F item : collect) {
                totalValue = converter.apply(totalValue, item, index++);
            }
        }
        return totalValue;
    }

    static <E> List<E> emptyIfNull(List<E> list) {
        return list == null ? new ArrayList<>() : list;
    }
}
