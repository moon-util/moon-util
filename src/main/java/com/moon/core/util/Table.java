package com.moon.core.util;

import com.moon.core.util.function.TableConsumer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public interface Table<X, Y, Z> {

    /**
     * 指定坐标设置值
     *
     * @param x X轴
     * @param y Y轴
     * @param z 值
     *
     * @return 旧值或 null
     */
    Z put(X x, Y y, Z z);

    /**
     * 指定坐标获取值
     *
     * @param x X轴
     * @param y Y轴
     *
     * @return 存在的值或 null
     */
    Z get(Object x, Object y);

    /**
     * 指定行设置值（替换）
     *
     * @param x   X轴
     * @param map 行数据
     *
     * @return 旧的行数据
     */
    Map<Y, Z> put(X x, Map<? extends Y, ? extends Z> map);

    /**
     * 指定行设置值（增量）
     *
     * @param x   X轴
     * @param map 行数据
     */
    void putAll(X x, Map<? extends Y, ? extends Z> map);

    /**
     * 获取指定行数据
     *
     * @param x X轴
     *
     * @return 行数据
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
     * @param x X轴
     * @param y Y轴
     *
     * @return 值
     */
    Z remove(Object x, Object y);

    /**
     * 删除指定行
     *
     * @param x X轴
     *
     * @return 返回被删除的行数据
     */
    Map<Y, Z> remove(Object x);

    /**
     * 是否包含值
     *
     * @param value 待测试值
     *
     * @return 是否包含
     *
     * @see Map#containsValue(Object)
     */
    boolean containsValue(Object value);

    /**
     * 所有 x
     *
     * @return 所有存在数据的 X 轴键
     */
    Set<X> keys();

    /**
     * 所有行
     *
     * @return 所有行数据
     */
    Collection<Map<Y, Z>> rows();

    /**
     * 所有行
     *
     * @return 所有行数据，X 轴 ——> 行数据
     */
    Set<Map.Entry<X, Map<Y, Z>>> rowsEntrySet();

    /**
     * 遍历行列处理每一个值
     *
     * @param consumer 处理器
     */
    void forEach(TableConsumer<X, Y, Z> consumer);

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
     * @param x X轴
     *
     * @return
     */
    default boolean contains(X x) { return get(x) != null; }

    /**
     * 是否包含
     *
     * @param x X轴
     * @param y Y轴
     *
     * @return
     */
    default boolean contains(X x, Y y) { return get(x, y) != null; }
}
