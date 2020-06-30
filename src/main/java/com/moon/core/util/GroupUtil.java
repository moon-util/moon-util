package com.moon.core.util;

import com.moon.core.enums.Collects;
import com.moon.core.lang.ThrowUtil;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 分组工具，用于对数组、集合和{@link Iterator}进行分组
 *
 * @author moonsky
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class GroupUtil {

    private GroupUtil() { ThrowUtil.noInstanceError(); }

    /**
     * 对数组分组，分组集合为{@code List}
     *
     * @param arr      待分组数组
     * @param function 分组函数，接受数组单项为参数，返回值为分组参照键
     *                 相同键名为同一组
     * @param <K>      参照键数据类型
     * @param <E>      数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code function}的返回值，
     * 值为数组项组成的{@link List}集合，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E> Map<K, List<E>> groupAsList(E[] arr, Function<? super E, ? extends K> function) {
        final Supplier supplier = Collects.ArrayLists;
        return groupBy(arr, function, supplier);
    }

    /**
     * 对数组分组，分组集合为{@code Set}
     *
     * @param arr      待分组数组
     * @param function 分组函数，接受数组单项为参数，返回值为分组参照键
     *                 相同键名为同一组
     * @param <K>      参照键数据类型
     * @param <E>      数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code function}的返回值，
     * 值为数组项组成的{@link Set}集合，每个分组的值不可能为 null 或空集合，
     * 但由于 Set 的性质，可能会有数据总数减少的情况。
     */
    public static <K, E> Map<K, Set<E>> groupAsSet(E[] arr, Function<? super E, ? extends K> function) {
        final Supplier supplier = Collects.HashSets;
        return groupBy(arr, function, supplier);
    }

    /**
     * 对数组分组，分组集合由调用方自定义
     *
     * @param arr              待分组数组
     * @param function         分组函数，接受数组单项为参数，返回值为分组参照键
     *                         相同键名为同一组
     * @param groupingSupplier 每个分组集合由“创建器”提供
     * @param <K>              参照键数据类型
     * @param <E>              数组元素类型
     * @param <CR>             分组集合类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code function}的返回值，每个分组的值不可能为 null 或空集合。
     */
    public static <E, K, CR extends Collection<E>>

    Map<K, CR> groupBy(E[] arr, Function<? super E, ? extends K> function, Supplier<CR> groupingSupplier) {
        Map<K, CR> grouped = new HashMap<>();
        final int len = arr == null ? 0 : arr.length;
        for (int i = 0; i < len; i++) {
            grouping(arr[i], grouped, function, groupingSupplier);
        }
        return grouped;
    }

    /**
     * 对{@code List}集合分组
     *
     * @param list     待分组集合
     * @param function 分组函数，接受数组单项为参数，返回值为分组参照键
     *                 相同键名为同一组
     * @param <K>      参照键数据类型
     * @param <E>      数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code function}的返回值，
     * 值为数组项组成的{@link List}集合，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E> Map<K, List<E>> groupAsList(
        List<E> list, Function<? super E, ? extends K> function
    ) {
        final Supplier supplier = Collects.getOrDefault(list, Collects.ArrayLists);
        return groupBy(list, function, supplier);
    }

    /**
     * 对集合分组
     *
     * @param iterator 待分组集合
     * @param function 分组函数，接受数组单项为参数，返回值为分组参照键
     *                 相同键名为同一组
     * @param <K>      参照键数据类型
     * @param <E>      数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code function}的返回值，
     * 值为数组项组成的{@link List}集合，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E> Map<K, List<E>> groupAsList(
        Iterator<E> iterator, Function<? super E, ? extends K> function
    ) {
        final Supplier supplier = Collects.ArrayLists;
        return groupBy(iterator, function, supplier);
    }

    /**
     * 对{@code List}集合分组
     *
     * @param set      待分组集合
     * @param function 分组函数，接受数组单项为参数，返回值为分组参照键
     *                 相同键名为同一组
     * @param <K>      参照键数据类型
     * @param <E>      数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code function}的返回值，
     * 值为数组项组成的{@link Set}集合，每个分组的值不可能为 null 或空集合，
     * 但由于 Set 的性质，可能会有数据总数减少的情况。
     */
    public static <K, E> Map<K, Set<E>> groupAsSet(
        Set<E> set, Function<? super E, ? extends K> function
    ) {
        final Supplier supplier = Collects.getOrDefault(set, Collects.HashSets);
        return groupBy(set, function, supplier);
    }

    /**
     * 对集合分组
     *
     * @param iterator 待分组集合
     * @param function 分组函数，接受数组单项为参数，返回值为分组参照键
     *                 相同键名为同一组
     * @param <K>      参照键数据类型
     * @param <E>      数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code function}的返回值，
     * 值为数组项组成的{@link Set}集合，每个分组的值不可能为 null 或空集合，
     * 但由于 Set 的性质，可能会有数据总数减少的情况。
     */
    public static <K, E> Map<K, Set<E>> groupAsSet(
        Iterator<E> iterator, Function<? super E, ? extends K> function
    ) {
        final Supplier supplier = Collects.HashSets;
        return groupBy(iterator, function, supplier);
    }

    /**
     * 对集合分组
     *
     * @param collect  待分组集合
     * @param function 分组函数，接受数组单项为参数，返回值为分组参照键
     *                 相同键名为同一组
     * @param <K>      参照键数据类型
     * @param <E>      数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code function}的返回值，
     * 值为数组项组成的{@link ArrayList}集合，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E> Map<K, Collection<E>> groupBy(
        Collection<E> collect, Function<? super E, ? extends K> function
    ) {
        final Supplier supplier = Collects.getOrDefault(collect, Collects.ArrayLists);
        return groupBy(collect, function, supplier);
    }

    /**
     * 对集合分组
     *
     * @param collect          待分组集合
     * @param function         分组函数，接受数组单项为参数，返回值为分组参照键
     *                         相同键名为同一组
     * @param groupingSupplier 每个分组集合由“创建器”提供
     * @param <K>              参照键数据类型
     * @param <E>              数组元素类型
     * @param <CR>             分组集合类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code function}的返回值，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E, CR extends Collection<E>> Map<K, CR> groupBy(
        Iterable<E> collect, Function<? super E, ? extends K> function, Supplier<CR> groupingSupplier
    ) {
        Map<K, CR> grouped = new HashMap<>(8);
        if (collect != null) {
            for (E item : collect) {
                grouping(item, grouped, function, groupingSupplier);
            }
        }
        return grouped;
    }

    /**
     * 对集合分组
     *
     * @param iterator         待分组集合
     * @param function         分组函数，接受数组单项为参数，返回值为分组参照键
     *                         相同键名为同一组
     * @param groupingSupplier 每个分组集合由“创建器”提供
     * @param <K>              参照键数据类型
     * @param <E>              数组元素类型
     * @param <CR>             分组集合类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code function}的返回值，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E, CR extends Collection<E>> Map<K, CR> groupBy(
        Iterator<E> iterator, Function<? super E, ? extends K> function, Supplier<CR> groupingSupplier
    ) {
        Map<K, CR> grouped = new HashMap<>(8);
        if (iterator != null) {
            while (iterator.hasNext()) {
                grouping(iterator.next(), grouped, function, groupingSupplier);
            }
        }
        return grouped;
    }

    /**
     * 单项分组执行器
     *
     * @param item             数据单项
     * @param grouped          分组容器
     * @param function         分组函数
     * @param groupingSupplier 集合构造器
     * @param <E>              数据项类型
     * @param <K>              分组参照键类型
     * @param <CR>             返回集合类型
     */
    private static <E, K, CR extends Collection<E>> void grouping(
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
