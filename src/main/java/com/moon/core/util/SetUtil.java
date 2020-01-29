package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;

import java.util.*;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public final class SetUtil extends CollectUtil {

    private SetUtil() { ThrowUtil.noInstanceError(); }

    public static Set empty() {return Collections.EMPTY_SET;}

    /*
     * ---------------------------------------------------------------------------------
     * of hash set
     * ---------------------------------------------------------------------------------
     */

    public static <T> HashSet<T> newHashSet() { return new HashSet<>(); }

    public static <T> HashSet<T> newHashSet(int initCapacity) { return new HashSet<>(initCapacity); }

    public static <T> HashSet<T> newHashSet(T value) { return add(newHashSet(), value); }

    public static <T> HashSet<T> newHashSet(T value1, T value2) { return add(newHashSet(value1), value2); }

    public static <T> HashSet<T> newHashSet(T value1, T value2, T value3) {
        return add(newHashSet(value1, value2), value3);
    }

    public static <T> HashSet<T> newHashSet(T... values) { return addAll(newHashSet(values.length), values); }

    public static <T> HashSet<T> newHashSet(Collection<T> collect) {
        return collect == null ? newHashSet() : new HashSet<>(collect);
    }

    public static <T> HashSet<T> newHashSet(Iterable<T> iterable) {
        return iterable == null ? newHashSet() : (iterable instanceof Collection ? new HashSet((Collection) iterable) : addAll(
            newHashSet(),
            iterable));
    }

    /*
     * ---------------------------------------------------------------------------------
     * of linked hash set
     * ---------------------------------------------------------------------------------
     */

    public static <T> LinkedHashSet<T> newLinkedHashSet() { return new LinkedHashSet<>(); }

    public static <T> LinkedHashSet<T> newLinkedHashSet(int initCapacity) { return new LinkedHashSet<>(initCapacity); }

    public static <T> LinkedHashSet<T> newLinkedHashSet(T value) { return add(newLinkedHashSet(), value); }

    public static <T> LinkedHashSet<T> newLinkedHashSet(T value1, T value2) {
        return add(newLinkedHashSet(value1), value2);
    }

    public static <T> LinkedHashSet<T> newLinkedHashSet(T value1, T value2, T value3) {
        return add(newLinkedHashSet(value1, value2), value3);
    }

    public static <T> LinkedHashSet<T> newLinkedHashSet(T... values) {
        return addAll(newLinkedHashSet(values.length), values);
    }

    public static <T> LinkedHashSet<T> newLinkedHashSet(Collection<T> collect) {
        return collect == null ? newLinkedHashSet() : new LinkedHashSet<>(collect);
    }

    public static <T> LinkedHashSet<T> newLinkedHashSet(Iterable<T> iterable) {
        return iterable == null ? newLinkedHashSet() : (iterable instanceof Collection ? new LinkedHashSet((Collection) iterable) : addAll(
            newLinkedHashSet(),
            iterable));
    }

    /*
     * ---------------------------------------------------------------------------------
     * of tree set
     * ---------------------------------------------------------------------------------
     */

    public static <T> TreeSet<T> newTreeSet() { return new TreeSet<>(); }

    public static <T> TreeSet<T> newTreeSet(T value) { return add(newTreeSet(), value); }

    public static <T> TreeSet<T> newTreeSet(T value1, T value2) { return add(newTreeSet(value1), value2); }

    public static <T> TreeSet<T> newTreeSet(T value1, T value2, T value3) {
        return add(newTreeSet(value1, value2), value3);
    }

    public static <T> TreeSet<T> newTreeSet(T... values) { return addAll(newTreeSet(), values); }

    public static <T> TreeSet<T> newTreeSet(Collection<T> collect) {
        return collect == null ? newTreeSet() : new TreeSet<>(collect);
    }

    public static <T> TreeSet<T> newTreeSet(Iterable<T> iterable) {
        return iterable == null ? newTreeSet() : (iterable instanceof Collection ? new TreeSet((Collection) iterable) : addAll(
            newTreeSet(),
            iterable));
    }

    public static <T> TreeSet<T> newTreeSet(SortedSet<T> sortedSet) { return new TreeSet<>(sortedSet); }

    public static <T> TreeSet<T> newTreeSet(Comparator<? super T> comparator) { return new TreeSet<>(comparator); }

    public static <T> TreeSet<T> newTreeSet(Comparator<? super T> comparator, Iterable<T> iterable) {
        return addAll(newTreeSet(comparator), iterable);
    }

    public static <T> TreeSet<T> newTreeSet(Comparator<? super T> comparator, T... values) {
        return addAll(newTreeSet(comparator), values);
    }

    public static <S, T> Set<T> mapAsSet(Collection<S> src, Function<? super S, T> mapper) {
        Collection<T> collect = map(src, mapper);
        return collect instanceof List ? (Set<T>) collect : newHashSet(collect);
    }

    public static <T> Set<T> concat(Set<T> set, Set<T>... sets) { return (Set) concat0(set, sets); }

    public static <T> T requireGet(Set<T> set, int index) {
        return get(set, index);
    }

    public static <T> T nullableGet(Set<T> set, int index) {
        int size = set.size(), idx = 0;
        if (size <= index || index < 0) {
            return null;
        }
        for (T item : set) {
            if (idx++ == index) {
                return item;
            }
        }
        return null;
    }

    public static <T> T get(Set<T> set, int index) {
        int size = set.size(), idx = 0;
        if (size <= index || index < 0) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
        for (T item : set) {
            if (idx++ == index) {
                return item;
            }
        }
        throw new IndexOutOfBoundsException(String.valueOf(index));
    }

    public static <T> T getByObject(Object set, int index) {
        return get((Set<T>) set, index);
    }
}
