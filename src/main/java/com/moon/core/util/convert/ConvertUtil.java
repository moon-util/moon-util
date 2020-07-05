package com.moon.core.util.convert;

import com.moon.core.lang.ArrayUtil;
import com.moon.core.lang.ThrowUtil;

/**
 * @author benshaoye
 */
public final class ConvertUtil {

    private ConvertUtil() { ThrowUtil.noInstanceError(); }

    @SafeVarargs
    static <T> T[] concat(T[] arr1, T... arr2) {
        return ArrayUtil.splice(arr1, arr1.length, 0, arr2);
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
     * 转为数组
     *
     * @param values 值列表
     *
     * @return 数组
     */
    @SafeVarargs
    static <T> T[] arr(T... values) { return values; }
}
