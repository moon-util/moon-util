package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;

import java.util.*;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public final class SetUtil extends CollectUtil {

    private SetUtil() { ThrowUtil.noInstanceError(); }

    public static Set empty() {return Collections.EMPTY_SET;}

    /*
     * ---------------------------------------------------------------------------------
     * of hash set
     * ---------------------------------------------------------------------------------
     */

    public static <T> HashSet<T> ofHashSet() { return new HashSet<>(); }

    public static <T> HashSet<T> ofHashSet(int initCapacity) { return new HashSet<>(initCapacity); }

    public static <T> HashSet<T> ofHashSet(T value) { return add(ofHashSet(), value); }

    public static <T> HashSet<T> ofHashSet(T value1, T value2) { return add(ofHashSet(value1), value2); }

    public static <T> HashSet<T> ofHashSet(T value1, T value2, T value3) {
        return add(ofHashSet(value1, value2), value3);
    }

    public static <T> HashSet<T> ofHashSet(T... values) { return addAll(ofHashSet(values.length), values); }

    public static <T> HashSet<T> ofHashSet(Collection<T> collect) {
        return collect == null ? ofHashSet() : new HashSet<>(collect);
    }

    public static <T> HashSet<T> ofHashSet(Iterable<T> iterable) {
        return iterable == null ? ofHashSet() : (iterable instanceof Collection ? new HashSet(
            (Collection) iterable) : addAll(ofHashSet(), iterable));
    }

    /*
     * ---------------------------------------------------------------------------------
     * of linked hash set
     * ---------------------------------------------------------------------------------
     */

    public static <T> LinkedHashSet<T> ofLinkedHashSet() { return new LinkedHashSet<>(); }

    public static <T> LinkedHashSet<T> ofLinkedHashSet(int initCapacity) { return new LinkedHashSet<>(initCapacity); }

    public static <T> LinkedHashSet<T> ofLinkedHashSet(T value) { return add(ofLinkedHashSet(), value); }

    public static <T> LinkedHashSet<T> ofLinkedHashSet(T value1, T value2) {
        return add(ofLinkedHashSet(value1), value2);
    }

    public static <T> LinkedHashSet<T> ofLinkedHashSet(T value1, T value2, T value3) {
        return add(ofLinkedHashSet(value1, value2), value3);
    }

    public static <T> LinkedHashSet<T> ofLinkedHashSet(T... values) {
        return addAll(ofLinkedHashSet(values.length), values);
    }

    public static <T> LinkedHashSet<T> ofLinkedHashSet(Collection<T> collect) {
        return collect == null ? ofLinkedHashSet() : new LinkedHashSet<>(collect);
    }

    public static <T> LinkedHashSet<T> ofLinkedHashSet(Iterable<T> iterable) {
        return iterable == null ? ofLinkedHashSet() : (iterable instanceof Collection ? new LinkedHashSet(
            (Collection) iterable) : addAll(ofLinkedHashSet(), iterable));
    }

    /*
     * ---------------------------------------------------------------------------------
     * of tree set
     * ---------------------------------------------------------------------------------
     */

    public static <T> TreeSet<T> ofTreeSet() { return new TreeSet<>(); }

    public static <T> TreeSet<T> ofTreeSet(T value) { return add(ofTreeSet(), value); }

    public static <T> TreeSet<T> ofTreeSet(T value1, T value2) { return add(ofTreeSet(value1), value2); }

    public static <T> TreeSet<T> ofTreeSet(T value1, T value2, T value3) {
        return add(ofTreeSet(value1, value2), value3);
    }

    public static <T> TreeSet<T> ofTreeSet(T... values) { return addAll(ofTreeSet(), values); }

    public static <T> TreeSet<T> ofTreeSet(Collection<T> collect) {
        return collect == null ? ofTreeSet() : new TreeSet<>(collect);
    }

    public static <T> TreeSet<T> ofTreeSet(Iterable<T> iterable) {
        return iterable == null ? ofTreeSet() : (iterable instanceof Collection ? new TreeSet(
            (Collection) iterable) : addAll(ofTreeSet(), iterable));
    }

    public static <T> TreeSet<T> ofTreeSet(SortedSet<T> sortedSet) { return new TreeSet<>(sortedSet); }

    public static <T> TreeSet<T> ofTreeSet(Comparator<? super T> comparator) { return new TreeSet<>(comparator); }

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
