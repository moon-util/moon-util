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
     * 单元素分组，将集合或数组元素按元素进行分组，如果存在多个相同“键”，最终只会保留一个
     * <p>
     * 如（伪代码）：
     * <pre>
     * var list = [
     *   {id: 1, name: '上海'},
     *   {id: 2, name: '北京'},
     *   {id: 1, name: '广州'},
     * ]
     *
     * var map = simplifyGroup(list, obj => obj.id);
     * map like: {
     *     1: {id: 1, name: '广州'}, // 只保留最后一个同名 KEY
     *     2: {id: 2, name: '北京'},
     * }
     * </pre>
     * <p>
     * 如需自定义 Map 类型，可参考如下：
     * <pre>
     * CollectUtil.reduce(collect, (resultMap, item, idx) -> {
     *    resultMap.put(grouper.apply(item), item);
     * }, new LinkedHashMap());
     * </pre>
     *
     * @param iterator 集合
     * @param grouper  分组函数
     * @param <K>      键类型
     * @param <E>      元素类型
     *
     * @return 分组集合
     */
    public static <K, E> Map<K, E> simplifyGroup(
        Iterator<? extends E> iterator, Function<? super E, ? extends K> grouper
    ) {
        HashMap resultMap = new HashMap(16);
        if (iterator != null) {
            while (iterator.hasNext()) {
                E item = iterator.next();
                resultMap.put(grouper.apply(item), item);
            }
        }
        return resultMap;
    }

    /**
     * 单元素分组，将集合或数组元素按元素进行分组，如果存在多个相同“键”，最终只会保留一个
     *
     * @param collect 集合
     * @param grouper 分组函数
     * @param <K>     键类型
     * @param <E>     元素类型
     *
     * @return 分组集合
     */
    public static <K, E> Map<K, E> simplifyGroup(
        Iterable<? extends E> collect, Function<? super E, ? extends K> grouper
    ) {
        if (collect instanceof Collection) {
            return simplifyGroup((Collection) collect, grouper);
        }
        HashMap resultMap = new HashMap(16);
        if (collect != null) {
            for (E item : collect) {
                resultMap.put(grouper.apply(item), item);
            }
        }
        return resultMap;
    }

    /**
     * 单元素分组，将集合或数组元素按元素进行分组，如果存在多个相同“键”，最终只会保留一个
     *
     * @param collect 集合
     * @param grouper 分组函数
     * @param <K>     键类型
     * @param <E>     元素类型
     *
     * @return 分组集合
     */
    public static <K, E> Map<K, E> simplifyGroup(
        Collection<? extends E> collect, Function<? super E, ? extends K> grouper
    ) {
        int len = collect == null ? 0 : collect.size();
        HashMap resultMap = new HashMap(len);
        if (collect != null) {
            for (E item : collect) {
                resultMap.put(grouper.apply(item), item);
            }
        }
        return resultMap;
    }

    /**
     * 单元素分组，将集合或数组元素按元素进行分组，如果存在多个相同“键”，最终只会保留一个
     *
     * @param arr     数组
     * @param grouper 分组函数
     * @param <K>     键类型
     * @param <E>     元素类型
     *
     * @return 分组集合
     */
    public static <K, E> Map<K, E> simplifyGroup(E[] arr, Function<? super E, ? extends K> grouper) {
        int len = arr == null ? 0 : arr.length;
        HashMap resultMap = new HashMap(len);
        if (arr != null) {
            for (E item : arr) {
                resultMap.put(grouper.apply(item), item);
            }
        }
        return resultMap;
    }

    /**
     * 对数组分组，分组集合为{@code List}
     *
     * @param arr     待分组数组
     * @param grouper 分组函数，接受数组单项为参数，返回值为分组参照键
     *                相同键名为同一组
     * @param <K>     参照键数据类型
     * @param <E>     数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，
     * 值为数组项组成的{@link List}集合，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E> Map<K, List<E>> groupAsList(E[] arr, Function<? super E, ? extends K> grouper) {
        final Supplier supplier = Collects.ArrayLists;
        return groupBy(arr, grouper, supplier);
    }

    /**
     * 对数组分组，分组集合为{@code List}
     *
     * @param groupResultMap 分组结果容器
     * @param arr            待分组数组
     * @param grouper        分组函数，接受数组单项为参数，返回值为分组参照键
     *                       相同键名为同一组
     * @param <K>            参照键数据类型
     * @param <E>            数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，
     * 值为数组项组成的{@link List}集合，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E> Map<K, List<E>> groupAsList(
        Map<K, List<E>> groupResultMap, E[] arr, Function<? super E, ? extends K> grouper
    ) {
        final Supplier supplier = Collects.ArrayLists;
        return groupBy(groupResultMap, arr, grouper, supplier);
    }

    /**
     * 对数组分组，分组集合为{@code Set}
     *
     * @param arr     待分组数组
     * @param grouper 分组函数，接受数组单项为参数，返回值为分组参照键
     *                相同键名为同一组
     * @param <K>     参照键数据类型
     * @param <E>     数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，
     * 值为数组项组成的{@link Set}集合，每个分组的值不可能为 null 或空集合，
     * 但由于 Set 的性质，可能会有数据总数减少的情况。
     */
    public static <K, E> Map<K, Set<E>> groupAsSet(E[] arr, Function<? super E, ? extends K> grouper) {
        final Supplier supplier = Collects.HashSets;
        return groupBy(arr, grouper, supplier);
    }

    /**
     * 对数组分组，分组集合为{@code Set}
     *
     * @param groupResultMap 分组结果容器
     * @param arr            待分组数组
     * @param grouper        分组函数，接受数组单项为参数，返回值为分组参照键
     *                       相同键名为同一组
     * @param <K>            参照键数据类型
     * @param <E>            数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，
     * 值为数组项组成的{@link Set}集合，每个分组的值不可能为 null 或空集合，
     * 但由于 Set 的性质，可能会有数据总数减少的情况。
     */
    public static <K, E> Map<K, Set<E>> groupAsSet(
        Map<K, Set<E>> groupResultMap, E[] arr, Function<? super E, ? extends K> grouper
    ) {
        final Supplier supplier = Collects.HashSets;
        return groupBy(groupResultMap, arr, grouper, supplier);
    }

    /**
     * 对数组分组，分组集合由调用方自定义
     *
     * @param arr              待分组数组
     * @param grouper          分组函数，接受数组单项为参数，返回值为分组参照键
     *                         相同键名为同一组
     * @param groupingSupplier 每个分组集合由“创建器”提供
     * @param <K>              参照键数据类型
     * @param <E>              数组元素类型
     * @param <CR>             分组集合类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，每个分组的值不可能为 null 或空集合。
     */
    public static <E, K, CR extends Collection<E>>

    Map<K, CR> groupBy(E[] arr, Function<? super E, ? extends K> grouper, Supplier<CR> groupingSupplier) {
        return groupBy(new HashMap<>(), arr, grouper, groupingSupplier);
    }

    /**
     * 对数组分组，分组集合由调用方自定义
     *
     * @param groupResultMap   分组结果容器
     * @param arr              待分组数组
     * @param grouper          分组函数，接受数组单项为参数，返回值为分组参照键
     *                         相同键名为同一组
     * @param groupingSupplier 每个分组集合由“创建器”提供
     * @param <K>              参照键数据类型
     * @param <E>              数组元素类型
     * @param <CR>             分组集合类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，每个分组的值不可能为 null 或空集合。
     */
    public static <E, K, CR extends Collection<E>> Map<K, CR> groupBy(
        Map<K, CR> groupResultMap, E[] arr, Function<? super E, ? extends K> grouper, Supplier<CR> groupingSupplier
    ) {
        final int len = arr == null ? 0 : arr.length;
        for (int i = 0; i < len; i++) {
            grouping(arr[i], groupResultMap, grouper, groupingSupplier);
        }
        return groupResultMap;
    }

    /**
     * 对{@code List}集合分组
     *
     * @param list    待分组集合
     * @param grouper 分组函数，接受集合单项为参数，返回值为分组参照键
     *                相同键名为同一组
     * @param <K>     参照键数据类型
     * @param <E>     数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，
     * 值为数组项组成的{@link List}集合，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E> Map<K, List<E>> groupAsList(
        Iterable<E> list, Function<? super E, ? extends K> grouper
    ) { return groupBy(list, grouper, asListGrouped(list)); }

    /**
     * 对{@code List}集合分组
     *
     * @param groupResultMap 分组结果容器
     * @param list           待分组集合
     * @param grouper        分组函数，接受集合单项为参数，返回值为分组参照键
     *                       相同键名为同一组
     * @param <K>            参照键数据类型
     * @param <E>            数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，
     * 值为数组项组成的{@link List}集合，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E> Map<K, List<E>> groupAsList(
        Map<K, Set<E>> groupResultMap, Iterable<E> list, Function<? super E, ? extends K> grouper
    ) { return groupBy(groupResultMap, list, grouper, asListGrouped(list)); }

    /**
     * 对集合分组
     *
     * @param iterator 待分组集合
     * @param grouper  分组函数，接受集合单项为参数，返回值为分组参照键
     *                 相同键名为同一组
     * @param <K>      参照键数据类型
     * @param <E>      数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，
     * 值为数组项组成的{@link List}集合，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E> Map<K, List<E>> groupAsList(
        Iterator<E> iterator, Function<? super E, ? extends K> grouper
    ) {
        Supplier supplier = Collects.ArrayLists;
        return groupBy(iterator, grouper, supplier);
    }

    /**
     * 对集合分组
     *
     * @param groupResultMap 分组结果容器
     * @param iterator       待分组集合
     * @param grouper        分组函数，接受集合单项为参数，返回值为分组参照键
     *                       相同键名为同一组
     * @param <K>            参照键数据类型
     * @param <E>            数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，
     * 值为数组项组成的{@link List}集合，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E> Map<K, List<E>> groupAsList(
        Map<K, Set<E>> groupResultMap, Iterator<E> iterator, Function<? super E, ? extends K> grouper
    ) {
        Supplier supplier = Collects.ArrayLists;
        return groupBy(groupResultMap, iterator, grouper, supplier);
    }

    /**
     * 对{@code List}集合分组
     *
     * @param set     待分组集合
     * @param grouper 分组函数，接受集合单项为参数，返回值为分组参照键
     *                相同键名为同一组
     * @param <K>     参照键数据类型
     * @param <E>     数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，
     * 值为数组项组成的{@link Set}集合，每个分组的值不可能为 null 或空集合，
     * 但由于 Set 的性质，可能会有数据总数减少的情况。
     */
    public static <K, E> Map<K, Set<E>> groupAsSet(
        Iterable<E> set, Function<? super E, ? extends K> grouper
    ) { return groupBy(set, grouper, asSetGrouped(set)); }

    /**
     * 对{@code List}集合分组
     *
     * @param groupResultMap 分组结果容器
     * @param set            待分组集合
     * @param grouper        分组函数，接受集合单项为参数，返回值为分组参照键
     *                       相同键名为同一组
     * @param <K>            参照键数据类型
     * @param <E>            数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，
     * 值为数组项组成的{@link Set}集合，每个分组的值不可能为 null 或空集合，
     * 但由于 Set 的性质，可能会有数据总数减少的情况。
     */
    public static <K, E> Map<K, Set<E>> groupAsSet(
        Map<K, Set<E>> groupResultMap, Iterable<E> set, Function<? super E, ? extends K> grouper
    ) { return groupBy(groupResultMap, set, grouper, asSetGrouped(set)); }

    /**
     * 对集合分组
     *
     * @param iterator 待分组集合
     * @param grouper  分组函数，接受集合单项为参数，返回值为分组参照键
     *                 相同键名为同一组
     * @param <K>      参照键数据类型
     * @param <E>      数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，
     * 值为数组项组成的{@link Set}集合，每个分组的值不可能为 null 或空集合，
     * 但由于 Set 的性质，可能会有数据总数减少的情况。
     */
    public static <K, E> Map<K, Set<E>> groupAsSet(
        Iterator<E> iterator, Function<? super E, ? extends K> grouper
    ) {
        Supplier supplier = Collects.HashSets;
        return groupBy(iterator, grouper, supplier);
    }

    /**
     * 对集合分组
     *
     * @param groupResultMap 分组结果容器
     * @param iterator       待分组集合
     * @param grouper        分组函数，接受集合单项为参数，返回值为分组参照键
     *                       相同键名为同一组
     * @param <K>            参照键数据类型
     * @param <E>            数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，
     * 值为数组项组成的{@link Set}集合，每个分组的值不可能为 null 或空集合，
     * 但由于 Set 的性质，可能会有数据总数减少的情况。
     */
    public static <K, E> Map<K, Set<E>> groupAsSet(
        Map<K, Set<E>> groupResultMap, Iterator<E> iterator, Function<? super E, ? extends K> grouper
    ) {
        Supplier supplier = Collects.HashSets;
        return groupBy(groupResultMap, iterator, grouper, supplier);
    }

    /**
     * 对集合分组
     *
     * @param collect 待分组集合
     * @param grouper 分组函数，接受集合单项为参数，返回值为分组参照键
     *                相同键名为同一组
     * @param <K>     参照键数据类型
     * @param <E>     数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，
     * 值为数组项组成的{@link ArrayList}集合，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E> Map<K, Collection<E>> groupBy(
        Iterable<E> collect, Function<? super E, ? extends K> grouper
    ) {
        Supplier supplier = Collects.getOrDefault(collect, Collects.ArrayLists);
        return groupBy(collect, grouper, supplier);
    }

    /**
     * 对集合分组
     *
     * @param groupResultMap 分组结果容器
     * @param collect        待分组集合
     * @param grouper        分组函数，接受集合单项为参数，返回值为分组参照键
     *                       相同键名为同一组
     * @param <K>            参照键数据类型
     * @param <E>            数组元素类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，
     * 值为数组项组成的{@link ArrayList}集合，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E> Map<K, Collection<E>> groupBy(
        Map<K, Collection<E>> groupResultMap, Iterable<E> collect, Function<? super E, ? extends K> grouper
    ) {
        Supplier supplier = Collects.getOrDefault(collect, Collects.ArrayLists);
        return groupBy(groupResultMap, collect, grouper, supplier);
    }

    /**
     * 对集合分组
     *
     * @param collect                   待分组集合
     * @param grouper                   分组函数，接受集合单项为参数，返回值为分组参照键
     *                                  相同键名为同一组
     * @param groupingContainerSupplier 每个分组集合由“创建器”提供
     * @param <K>                       参照键数据类型
     * @param <E>                       数组元素类型
     * @param <CR>                      分组集合类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E, CR extends Collection<E>> Map<K, CR> groupBy(
        Iterable<E> collect, Function<? super E, ? extends K> grouper, Supplier<CR> groupingContainerSupplier
    ) { return groupBy(new HashMap<>(8), collect, grouper, groupingContainerSupplier); }

    /**
     * 对集合分组
     *
     * @param groupResultMap            分组结果容器
     * @param collect                   待分组集合
     * @param grouper                   分组函数，接受集合单项为参数，返回值为分组参照键
     *                                  相同键名为同一组
     * @param groupingContainerSupplier 每个分组集合由“创建器”提供
     * @param <K>                       参照键数据类型
     * @param <E>                       数组元素类型
     * @param <CR>                      分组集合类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E, CR extends Collection<E>> Map<K, CR> groupBy(
        Map<K, CR> groupResultMap,
        Iterable<E> collect,
        Function<? super E, ? extends K> grouper,
        Supplier<CR> groupingContainerSupplier
    ) {
        if (collect != null) {
            for (E item : collect) {
                grouping(item, groupResultMap, grouper, groupingContainerSupplier);
            }
        }
        return groupResultMap;
    }

    /**
     * 对集合分组
     *
     * @param iterator                  待分组集合
     * @param grouper                   分组函数，接受集合单项为参数，返回值为分组参照键
     *                                  相同键名为同一组
     * @param groupingContainerSupplier 每个分组集合由“创建器”提供
     * @param <K>                       参照键数据类型
     * @param <E>                       数组元素类型
     * @param <CR>                      分组集合类型
     *
     * @return 分组后的元素以 Map 形式返回，Map 的键即为{@code grouper}的返回值，每个分组的值不可能为 null 或空集合。
     */
    public static <K, E, CR extends Collection<E>> Map<K, CR> groupBy(
        Iterator<E> iterator, Function<? super E, ? extends K> grouper, Supplier<CR> groupingContainerSupplier
    ) { return groupBy(new HashMap<>(8), iterator, grouper, groupingContainerSupplier); }

    /**
     * 对集合分组，并将分组后的结果放进指定{@code groupResultMap}容器中
     *
     * @param groupResultMap            分组结果容器
     * @param iterator                  待分组集合
     * @param grouper                   分组函数，接受集合单项为参数，返回值为分组参照键
     *                                  相同键名为同一组
     * @param groupingContainerSupplier 每个分组集合由“创建器”提供
     * @param <K>                       参照键数据类型
     * @param <E>                       数组元素类型
     * @param <CR>                      分组集合类型
     *
     * @return groupResultMap
     */
    public static <K, E, CR extends Collection<E>> Map<K, CR> groupBy(
        Map<K, CR> groupResultMap,
        Iterator<E> iterator,
        Function<? super E, ? extends K> grouper,
        Supplier<CR> groupingContainerSupplier
    ) {
        if (iterator != null) {
            while (iterator.hasNext()) {
                grouping(iterator.next(), groupResultMap, grouper, groupingContainerSupplier);
            }
        }
        return groupResultMap;
    }

    /**
     * 单项分组执行器
     *
     * @param item                      数据单项
     * @param grouped                   分组容器
     * @param grouper                   分组函数
     * @param groupingContainerSupplier 集合构造器
     * @param <E>                       数据项类型
     * @param <K>                       分组参照键类型
     * @param <CR>                      返回集合类型
     */
    private static <E, K, CR extends Collection<E>> void grouping(
        E item, Map<K, CR> grouped, Function<? super E, ? extends K> grouper, Supplier<CR> groupingContainerSupplier
    ) {
        K key = grouper.apply(item);
        CR group = grouped.get(key);
        if (group == null) {
            grouped.put(key, group = groupingContainerSupplier.get());
        }
        group.add(item);
    }

    private static Supplier asSetGrouped(Object data) {
        return data instanceof Set ? Collects.getOrDefault(data, Collects.HashSets) : Collects.HashSets;
    }

    private static Supplier asListGrouped(Object data) {
        return data instanceof List ? Collects.getOrDefault(data, Collects.ArrayLists) : Collects.ArrayLists;
    }
}
