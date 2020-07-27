package com.moon.core.util;

import java.util.Comparator;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class ComparatorUtil {

    private ComparatorUtil() { noInstanceError(); }

    /**
     * 多条件比较器
     *
     * @param comparators 自定义比较器
     * @param <T>         数据类型
     *
     * @return 多条件比较器
     */
    public static <T> Comparator<T> ofMulti(Comparator<T>... comparators) {
        return (o1, o2) -> {
            for (Comparator<T> comparator : comparators) {
                int result = comparator.compare(o1, o2);
                if (result != 0) {
                    return result;
                }
            }
            return 0;
        };
    }
}
