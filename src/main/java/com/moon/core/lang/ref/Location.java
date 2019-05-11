package com.moon.core.lang.ref;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public interface Location<X, Y, Z> {
    /**
     * 设置一个值
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    Location<X, Y, Z> put(X x, Y y, Z z);

    /**
     * 放置所有
     *
     * @param x
     * @param map
     * @return
     */
    Location<X, Y, Z> putAll(X x, Map<? extends Y, ? extends Z> map);

    /**
     * 获取一个值
     *
     * @param x
     * @param y
     * @return
     */
    Z get(X x, Y y);

    /**
     * 清空
     *
     * @return
     */
    Location<X, Y, Z> clear();

    /**
     * 清空
     *
     * @param x
     * @return
     */
    Location<X, Y, Z> clear(X x);

    /**
     * 获取值，或返回默认值
     *
     * @param x
     * @param y
     * @param defaultValue
     * @return
     */
    default Z getOrDefault(X x, Y y, Z defaultValue) {
        Z value = get(x, y);
        return value == null ? defaultValue : value;
    }

    /**
     * 获取值，或返回默认值
     *
     * @param x
     * @param y
     * @param supplier
     * @return
     */
    default Z getOrElse(X x, Y y, Supplier<Z> supplier) {
        Z value = get(x, y);
        return value == null ? supplier.get() : value;
    }

    /**
     * 获取值，或返回默认值
     *
     * @param x
     * @param y
     * @param defaultValue
     * @return
     */
    default Z getOrWithDefault(X x, Y y, Z defaultValue) {
        Z value = get(x, y);
        if (value == null) { put(x, y, value = defaultValue); }
        return value;
    }

    /**
     * 获取值，或返回执行结果
     *
     * @param x
     * @param y
     * @param supplier
     * @return
     */
    default Z getOrWithElse(X x, Y y, Supplier<Z> supplier) {
        Z value = get(x, y);
        if (value == null) { put(x, y, value = supplier.get()); }
        return value;
    }

    /**
     * 获取值，或返回执行结果
     *
     * @param x
     * @param y
     * @param computer
     * @return
     */
    default Z getOrWithCompute(X x, Y y, BiFunction<X, Y, Z> computer) {
        Z value = get(x, y);
        if (value == null) { put(x, y, value = computer.apply(x, y)); }
        return value;
    }
}
