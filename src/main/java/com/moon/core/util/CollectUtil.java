package com.moon.core.util;

import com.moon.core.enums.Collects;
import com.moon.core.enums.Lists;
import com.moon.core.enums.Sets;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public class CollectUtil extends BaseCollectUtil {

    protected CollectUtil() { noInstanceError(); }

    public final static <E> int size(Collection<E> collect) {
        return collect == null ? 0 : collect.size();
    }

    public final static int sizeByObject(Object collect) { return collect == null ? 0 : ((Collection) collect).size(); }

    public final static int sizeOfAll(Collection... cs) {
        int size = 0, i = 0;
        for (; i < cs.length; size += size(cs[i++])) {
        }
        return size;
    }

    public final static <T> T[] toArray(Collection<? extends T> collect, IntFunction<? extends T[]> arrCreator) {
        return collect == null ? arrCreator.apply(0) : collect.toArray(arrCreator.apply(size(collect)));
    }

    public final static <T> T[] toArrayOrDefault(
        Collection<? extends T> collect, IntFunction<? extends T[]> arrCreator, T[] defaultArr
    ) { return collect == null ? defaultArr : collect.toArray(arrCreator.apply(size(collect))); }

    public final static <E> boolean isEmpty(Collection<E> collect) {
        return collect == null || collect.isEmpty();
    }

    public final static <E> boolean isNotEmpty(Collection<E> collect) { return !isEmpty(collect); }

    /*
     * ---------------------------------------------------------------------------------
     * adders
     * ---------------------------------------------------------------------------------
     */

    public final static <E, C extends Collection<? super E>> C add(C collect, E element) {
        if (collect != null) {
            collect.add(element);
        }
        return collect;
    }

    public final static <E, C extends Collection<? super E>> C add(C collect, E element1, E element2) {
        if (collect != null) {
            collect.add(element1);
            collect.add(element2);
        }
        return collect;
    }

    public final static <E, C extends Collection<? super E>> C addAll(C collect, E... elements) {
        if (collect != null && elements != null) {
            for (E element : elements) {
                collect.add(element);
            }
        }
        return collect;
    }

    public final static <E, C extends Collection<? super E>> C addAll(C collect, Collection<? extends E> collection) {
        if (collect != null && collection != null) {
            collect.addAll(collection);
        }
        return collect;
    }

    public final static <E, C extends Collection<? super E>> C addAll(C collect, Iterable<? extends E> iterable) {
        if (collect != null && iterable != null) {
            if (iterable instanceof Collection) {
                collect.addAll((Collection) iterable);
            } else {
                iterable.forEach(collect::add);
            }
        }
        return collect;
    }

    public final static <E, C extends Collection<? super E>> C addAll(C collect, Iterator<? extends E> iterator) {
        if (collect != null && iterator != null) {
            iterator.forEachRemaining(collect::add);
        }
        return collect;
    }

    /*
     * ---------------------------------------------------------------------------------
     * add if non null, 只有当待插入项非空时才执行插入操作
     * ---------------------------------------------------------------------------------
     */


    public final static <E, C extends Collection<? super E>> C addSkipNull(C collect, E item) {
        if (collect != null && item != null) {
            collect.add(item);
        }
        return collect;
    }

    public final static <E, C extends Collection<? super E>> C addSkipNulls(C collect, E item1, E item2) {
        if (collect != null) {
            if (item1 != null) {
                collect.add(item1);
            }
            if (item2 != null) {
                collect.add(item2);
            }
        }
        return collect;
    }

    public final static <E, C extends Collection<? super E>> C addSkipNulls(C collect, E... items) {
        if (collect != null) {
            E item;
            for (int i = 0; i < items.length; i++) {
                if ((item = items[i]) != null) {
                    collect.add(item);
                }
            }
        }
        return collect;
    }

    public final static <E, C extends Collection<? super E>> C addSkipNulls(C collect, Iterable<? extends E> iterable) {
        if (collect != null) {
            for (E elem : iterable) {
                if (elem != null) {
                    collect.add(elem);
                }
            }
        }
        return collect;
    }

    /*
     * ---------------------------------------------------------------------------------
     * converter
     * ---------------------------------------------------------------------------------
     */

    public final static <T, O, C1 extends Collection<T>> Collection<O> map(C1 src, Function<? super T, O> function) {
        return IteratorUtil.map(src, function);
    }

    /**
     * 转换集合中的每一项，并放进另一个集合中
     *
     * @param src       源集合
     * @param function  转换器
     * @param container 返回集合容器
     * @param <T>       源项数据类型
     * @param <O>       转换后的项数据类型
     * @param <C1>      源集合类型
     * @param <CR>      返回集合类型
     *
     * @return container
     *
     * @see Collects 一些常见的集合构造器，都是 JDK 中提供的
     * @see Lists 一些常见的 List 构造器，都是 JDK 中提供的
     * @see Sets 一些常见的 Set 构造器，都是 JDK 中提供的
     */
    public final static <T, O, C1 extends Collection<T>, CR extends Collection<O>> CR map(
        C1 src, Function<? super T, O> function, IntFunction<CR> container
    ) { return IteratorUtil.mapTo(src, function, container); }

    public final static <T> Collection<T> concat(Collection<T> collect, Collection<T>... collections) {
        return concat0(collect, collections);
    }

    public final static <T> Set<T> toSet(T... items) { return SetUtil.newHashSet(items); }

    public final static <T> List<T> toList(T... items) { return ListUtil.newArrayList(items); }

    public final static <E, T> T[] toArray(Collection<E> collection, Class<T> componentType) {
        int index = 0;
        Object array = Array.newInstance(componentType, collection.size());
        for (Object item : collection) {
            Array.set(array, index++, item);
        }
        return (T[]) array;
    }

    /*
     * ---------------------------------------------------------------------------------
     * contains
     * ---------------------------------------------------------------------------------
     */

    public final static <T> boolean contains(Collection<? super T> collect, T item) {
        return collect != null && collect.contains(item);
    }

    public final static <T> boolean containsAny(Collection<? super T> collect, T item1, T item2) {
        return collect != null && (collect.contains(item1) || collect.contains(item2));
    }

    public final static <T> boolean containsAll(Collection<? super T> collect, T item1, T item2) {
        return collect != null && (collect.contains(item1) && collect.contains(item2));
    }

    public final static <T> boolean containsAny(Collection<? super T> collect, T... items) {
        if (collect == null) {
            return false;
        }
        for (int i = 0, l = items.length; i < l; i++) {
            if (collect.contains(items[i])) {
                return true;
            }
        }
        return false;
    }

    public final static <T> boolean containsAll(Collection<? super T> collect, T... items) {
        if (collect == null) {
            return false;
        }
        for (int i = 0, l = items.length; i < l; i++) {
            if (!collect.contains(items[i])) {
                return false;
            }
        }
        return true;
    }

    public final static <T> boolean containsAny(Collection<T> collect1, Collection<T> collect2) {
        if (collect1 == collect2) {
            return true;
        }
        int size1 = collect1.size(), size2 = collect2.size();
        Collection<T> large = size1 > size2 ? collect1 : collect2;
        Collection<T> small = size1 > size2 ? collect2 : collect1;
        for (T item : large) {
            if (small.contains(item)) {
                return true;
            }
        }
        return false;
    }

    public final static <T> boolean containsAll(Collection<T> collect1, Collection<T> collect2) {
        return collect1 == collect2 || (collect1 != null && collect1.containsAll(collect2));
    }

    /**
     * 要求空集合，即集合中至少有一项数据
     *
     * @param collect 待测集合
     * @param message 自定义消息模板
     * @param <C>     集合泛型类型
     *
     * @return 若集合中至少有一项数据时返回集合本身
     *
     * @throws IllegalArgumentException 若集合为 null 或长度为 0 时抛出异常，
     *                                  异常消息由调用方自定义，
     *                                  可用“{}”占位符接收入参集合
     */
    static <E, C extends Collection<E>> C requireNotEmpty(C collect, String message) {
        return ValidateUtil.requireNotEmpty(collect, message);
    }
}
