package com.moon.core.util.able;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface CompareAble<T> {
    /**
     * 比较
     *
     * @param o
     * @return
     */
    int compare(T o);

    /**
     * 小于
     *
     * @param o
     * @return
     */
    default boolean lt(T o) {
        return compare(o) < 0;
    }

    /**
     * 大于等于
     *
     * @param o
     * @return
     */
    default boolean gt(T o) {
        return compare(o) > 0;
    }

    /**
     * 等于
     *
     * @param o
     * @return
     */
    default boolean eq(T o) {
        return compare(o) == 0;
    }

    /**
     * 小于等于
     *
     * @param o
     * @return
     */
    default boolean ltOrEq(T o) {
        return compare(o) <= 0;
    }

    /**
     * 大于等于
     *
     * @param o
     * @return
     */
    default boolean gtOrEq(T o) {
        return compare(o) >= 0;
    }
}
