package com.moon.core.util.convert;

import com.moon.core.lang.ArrayUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.util.ListUtil;
import com.moon.core.util.SetUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author benshaoye
 */
final class ToUtil {

    private ToUtil() { ThrowUtil.noInstanceError(); }

    static <T> T[] concat(T[] arr1, T[] arr2) {
        return ArrayUtil.splice(arr1, arr1.length, 0, arr2);
    }

    static <T> T[] concat(T[] first, T[]... arrays) {
        List<T> list = ListUtil.newList(first);
        for (T[] arr : arrays) {
            ListUtil.addAll(list, arr);
        }
        return ListUtil.toArray(list, (Class<T>) first.getClass().getComponentType());
    }

    /**
     * 如果是 null，为了缩短长度
     *
     * @param value 待测值
     *
     * @return 是否是 null
     */
    static boolean ifn(Object value) { return value == null; }

    /**
     * 如果不是 null，为了缩短长度
     *
     * @param value 待测值
     *
     * @return 是否不是 null
     */
    static boolean inn(Object value) { return value != null; }

    /**
     * 缓存支持的数据类型
     *
     * @param classes 类型列表
     *
     * @return set 集合，方便进行 contains 等操作
     */
    static Set<Class<?>> unmodifiableHashSet(Class<?>... classes) {
        return Collections.unmodifiableSet(SetUtil.newSet(classes));
    }

    /**
     * 转为数组
     *
     * @param values 值列表
     *
     * @return 数组
     */
    @SafeVarargs
    static <T> T[] arr(T... values) { return values; }
}
