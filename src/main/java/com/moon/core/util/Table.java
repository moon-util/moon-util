package com.moon.core.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author benshaoye
 */
public interface Table<X, Y, Z> {

    /**
     * 指定坐标设置值
     *
     * @param x
     * @param y
     * @param z
     *
     * @return
     */
    Z put(X x, Y y, Z z);

    /**
     * 指定坐标获取值
     *
     * @param x
     * @param y
     *
     * @return
     */
    Z get(Object x, Object y);

    /**
     * 指定行设置值（替换）
     *
     * @param x
     * @param map
     *
     * @return
     */
    Map<Y, Z> put(X x, Map<? extends Y, ? extends Z> map);

    /**
     * 指定行设置值（增量）
     *
     * @param x
     * @param map
     */
    void putAll(X x, Map<? extends Y, ? extends Z> map);

    /**
     * 获取指定行数据
     *
     * @param x
     *
     * @return
     */
    Map<Y, Z> get(Object x);

    /**
     * 增量设置值
     *
     * @param table
     */
    void putAll(Table<? extends X, ? extends Y, ? extends Z> table);

    /**
     * 删除
     *
     * @param x
     * @param y
     *
     * @return
     */
    Z remove(Object x, Object y);

    /**
     * 删除指定行
     *
     * @param x
     *
     * @return
     */
    Map<Y, Z> remove(Object x);

    /**
     * 是否包含值
     *
     * @param value
     *
     * @return
     */
    boolean containsValue(Object value);

    /**
     * 所有 x
     *
     * @return
     */
    Set<X> keys();

    /**
     * 所有行
     *
     * @return
     */
    Collection<Map<Y, Z>> rows();

    /**
     * 所有行
     *
     * @return
     */
    Set<Map.Entry<X, Map<Y, Z>>> rowsEntrySet();

    /**
     * 清空
     */
    void clear();

    /**
     * 行数
     *
     * @return
     */
    int sizeOfRows();

    /**
     * 最大列数
     *
     * @return
     */
    int maxSizeOfColumns();

    /**
     * 最小列数
     *
     * @return
     */
    int minSizeOfColumns();

    /**
     * 映射对数量
     *
     * @return
     */
    int size();

    /**
     * 是否为空
     *
     * @return
     */
    default boolean isEmpty() { return sizeOfRows() == 0 || maxSizeOfColumns() == 0; }

    /**
     * 是否包含
     *
     * @param x
     *
     * @return
     */
    default boolean contains(X x) { return get(x) != null; }

    /**
     * 是否包含
     *
     * @param x
     * @param y
     *
     * @return
     */
    default boolean contains(X x, Y y) { return get(x, y) != null; }
}
