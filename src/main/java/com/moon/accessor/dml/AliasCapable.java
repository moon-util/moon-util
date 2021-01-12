package com.moon.accessor.dml;

/**
 * @author benshaoye
 */
public interface AliasCapable<T> {
    /**
     * 查询时指定别名
     *
     * @param alias 别名
     */
    T as(String alias);
}
