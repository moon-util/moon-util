package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public final class SetUtil extends CollectUtil {
    private SetUtil() {
        ThrowUtil.noInstanceError();
    }

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

    public static <T> HashSet<T> ofHashSet(T value1, T value2, T value3) { return add(ofHashSet(value1, value2), value3); }

    public static <T> HashSet<T> ofHashSet(T... values) { return addAll(ofHashSet(values.length), values); }

    public static <T> HashSet<T> ofHashSet(Collection<T> collect) { return collect == null ? ofHashSet() : new HashSet<>(collect); }

    public static <T> HashSet<T> ofHashSet(Iterable<T> iterable) {
        return iterable == null ? ofHashSet()
            : (iterable instanceof Collection ? new HashSet((Collection) iterable)
            : addAll(ofHashSet(), iterable));
    }

    public static <T> Set<T> concat(Set<T> set, Set<T>... sets) { return (Set) concat0(set, sets); }
}
