package com.moon.core.lang.ref;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public class DefaultLocation<X, Y, Z> implements Location<X, Y, Z> {

    private final Supplier<Map> creator;

    private Map value;

    public DefaultLocation(Supplier<Map> creator) { this.creator = creator; }

    public static final <X, Y, Z> DefaultLocation<X, Y, Z> of(Supplier<Map> creator) {
        return new DefaultLocation<>(creator);
    }

    private Map<Y, Z> onlyGetX(X x) { return value == null ? null : (Map) value.get(x); }

    private Map<Y, Z> ensureGetX(X x) {
        Map map = this.value, res;
        if (map == null) {
            this.value = map = createSub();
            map.put(x, res = createSub());
        } else if ((res = (Map) map.get(x)) == null) {
            map.put(x, res = createSub());
        }
        return res;
    }

    /**
     * 设置一个值
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    @Override
    public DefaultLocation<X, Y, Z> put(X x, Y y, Z z) {
        ensureGetX(x).put(y, z);
        return this;
    }

    /**
     * 放置所有
     *
     * @param x
     * @param map
     * @return
     */
    @Override
    public DefaultLocation<X, Y, Z> putAll(X x, Map<? extends Y, ? extends Z> map) {
        ensureGetX(x).putAll(map);
        return this;
    }

    /**
     * 获取一个值
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public Z get(X x, Y y) {
        Map onlyX = onlyGetX(x);
        return onlyX == null ? null : (Z) onlyX.get(y);
    }

    /**
     * 清空
     *
     * @return
     */
    @Override
    public DefaultLocation<X, Y, Z> clear() {
        this.value = null;
        return this;
    }

    /**
     * 清空
     *
     * @param x
     * @return
     */
    @Override
    public DefaultLocation<X, Y, Z> clear(X x) {
        Map onlyX = onlyGetX(x);
        if (onlyX != null) { value.remove(x); }
        return this;
    }

    private Map createSub() { return creator.get(); }
}
