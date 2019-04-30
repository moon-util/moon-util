package com.moon.core.lang.ref;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * 二维键值对管理（坐标）
 *
 * @author ZhangDongMin
 * @date 2018/9/11
 */
public class FinalCoordinate<X, Y, Z> {

    protected final static int DEFAULT_CAPACITY = 16;
    protected final static Boolean IS_MANAGE = false;
    protected final static Boolean TRUE = true;

    private final static Object NULL = new Object();

    protected final boolean isManage;

    /**
     * 获得一个被管理的缓存坐标对象
     *
     * @param <X>
     * @param <Y>
     * @param <Z>
     * @return
     */
    public static <X, Y, Z> FinalCoordinate<X, Y, Z> manageOne() {
        return new FinalCoordinate(DEFAULT_CAPACITY, TRUE);
    }

    /**
     * 获得一个普通缓存坐标对象
     *
     * @param <X>
     * @param <Y>
     * @param <Z>
     * @return
     */
    public static <X, Y, Z> FinalCoordinate<X, Y, Z> one() {
        return new FinalCoordinate();
    }

    private final int capacity;

    private Map<Object, Map<Object, Object>> container;

    protected FinalCoordinate() {
        this(DEFAULT_CAPACITY, IS_MANAGE);
    }

    protected FinalCoordinate(int capacity, boolean isManage) {
        this.capacity = capacity;
        this.isManage = isManage;
        this.clear();
    }

    public boolean put(X x, Y y, Z z) {
        return this.putVal(x, y, z);
    }

    public boolean putAll(X x, Map<? extends Y, ? extends Z> map) {
        if (map == null) {
            return true;
        }
        Map<Object, Object> exist = container.get(x);
        if (exist == null) {
            exist = newSub(map.size());
            exist.putAll(map);
            container.put(x, exist);
        } else {
            exist.putAll(map);
        }
        return true;
    }

    private boolean putVal(Object x, Object y, Object z) {
        Map<Object, Object> map = container.get(x);
        if (map == null) {
            map = newSub(DEFAULT_CAPACITY);
            map.put(y, z);
            container.put(x, map);
        } else {
            map.put(y, z);
        }
        return true;
    }

    public Z get(X x, Y y) {
        Map<Object, Object> map = container.get(x);
        if (map == null) {
            return null;
        }
        Object val = map.get(y);
        if (val == NULL) {
            return null;
        }
        return (Z) val;
    }

    public Z get(X x, Y y, Supplier<Z> supplier) {
        Z value = get(x, y);
        if (value == null) {
            value = supplier.get();
            if (value == null) {
                this.putVal(x, y, NULL);
            } else {
                this.put(x, y, value);
            }
        }
        return value;
    }

    public Z get(X x, Y y, BiFunction<X, Y, Z> function) {
        Z value = get(x, y);
        if (value == null) {
            value = function.apply(x, y);
            if (value == null) {
                this.putVal(x, y, NULL);
            } else {
                this.put(x, y, value);
            }
        }
        return value;
    }

    public void clear() {
        this.container = new HashMap<>(capacity);
    }

    protected Map<Object, Object> newSub(int capacity) {
        return isManage ? new HashMap<>(capacity) : new HashMap<>();
    }
}
