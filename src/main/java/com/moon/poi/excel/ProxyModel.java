package com.moon.poi.excel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 代理模型
 * <p>
 * 这个类只限内部使用，所以对数据类型要求放宽了，比如要求的 classname 是 String 类型
 * 这里统一放宽为了 Object，这也是为了将来可能支持更多样式的 key
 *
 * @author moonsky
 */
class ProxyModel<FROM, R, K, B extends ProxyBuilder<FROM, R>, S extends ProxySetter<R, K>> {

    /**
     * 构建器列表
     */
    private volatile Map<Object, B> builderMap;
    /**
     * 已定义的构建器列表
     */
    private Map<String, R> defined;
    /**
     * 应用列表
     */
    private LinkedHashMap<S, String> setterMap;
    /**
     * 是否启用缓存已定义
     * <p>
     * 有些信息可以重复定义，有些则可以重复利用
     */
    private final boolean cacheDefined;

    private Map<Object, B> getBuilderMap() { return builderMap; }

    private Map<String, R> getDefined() { return defined; }

    private LinkedHashMap<S, String> getSetterMap() { return setterMap; }

    private Map<Object, B> ensureBuilderMap() {
        Map<Object, B> builderMap = getBuilderMap();
        if (builderMap == null) {
            builderMap = new HashMap<>(8);
            this.builderMap = builderMap;
        }
        return builderMap;
    }

    private Map<String, R> ensureDefined() {
        Map<String, R> defined = getDefined();
        if (defined == null) {
            defined = new HashMap<>(8);
            this.defined = defined;
        }
        return defined;
    }

    private LinkedHashMap<S, String> ensureSetterMap() {
        LinkedHashMap<S, String> setterMap = this.getSetterMap();
        if (setterMap == null) {
            setterMap = new LinkedHashMap<>(8);
            this.setterMap = setterMap;
        }
        return setterMap;
    }

    ProxyModel<FROM, R, K, B, S> addBuilder(B builder) {
        ensureBuilderMap().put(builder.getKey(), builder);
        return this;
    }

    ProxyModel<FROM, R, K, B, S> addSetter(String uniqueName, S setter) {
        ensureSetterMap().put(setter, uniqueName);
        return this;
    }

    B find(Object classname) { return ensureBuilderMap().get(classname); }

    Object find(S setter) { return ensureSetterMap().get(setter); }

    ProxyModel<FROM, R, K, B, S> removeSetter(S setter) {
        ensureSetterMap().remove(setter);
        return this;
    }

    protected ProxyModel() { this(true); }

    protected ProxyModel(boolean cacheDefined) { this.cacheDefined = cacheDefined; }

    private static boolean isEmpty(Map map) { return map == null || map.isEmpty(); }

    private LinkedHashMap<S, String> exchangeSetterMap() {
        LinkedHashMap map = this.getSetterMap();
        if (isEmpty(map)) {
            return null;
        }
        map = new LinkedHashMap(map);
        // 先获取持有，所以在这里设置 null
        this.builderMap = null;
        this.setterMap = null;
        return map;
    }

    void use(FROM from) {
        if (cacheDefined) {
            useCached(from);
        } else {
            useUncached(from);
        }
    }

    private void afterApplied(Map<Object, B> builderMap) {
        if (isEmpty(builderMap)) {
            return;
        }
        if (isEmpty(this.builderMap)) {
            synchronized (this) {
                if (isEmpty(this.builderMap)) {
                    this.builderMap = builderMap;
                } else {
                    this.builderMap.putAll(builderMap);
                }
            }
        } else {
            synchronized (this) {
                if (!isEmpty(this.builderMap)) {
                    this.builderMap.putAll(builderMap);
                } else {
                    this.builderMap = builderMap;
                }
            }
        }
    }

    private void useUncached(FROM from) {
        Map<Object, B> builderMap = getBuilderMap();
        if (isEmpty(builderMap)) {
            return;
        }
        LinkedHashMap<S, String> setters = exchangeSetterMap();
        if (isEmpty(setters)) {
            return;
        }

        R style;
        B builder;
        for (Map.Entry<S, String> entry : setters.entrySet()) {
            S setter = entry.getKey();
            String classname = entry.getValue();
            builder = builderMap.get(classname);
            if (builder != null) {
                style = builder.build(from);
                if (style != null) {
                    setter.setup(style);
                }
            }
        }
        afterApplied(builderMap);
    }

    private void useCached(FROM from) {
        Map<String, R> defined = getDefined();
        Map<Object, B> builderMap = getBuilderMap();
        if (isEmpty(defined) && isEmpty(builderMap)) {
            return;
        }
        LinkedHashMap<S, String> setters = exchangeSetterMap();
        if (isEmpty(setters)) {
            return;
        }
        R style;
        B builder;
        for (Map.Entry<S, String> entry : setters.entrySet()) {
            S setter = entry.getKey();
            String classname = entry.getValue();
            if (builderMap == null) {
                style = defined.get(classname);
            } else {
                if (defined == null) {
                    defined = ensureDefined();
                }
                builder = builderMap.remove(classname);
                if (builder == null) {
                    style = defined.get(classname);
                } else {
                    style = builder.build(from);
                    defined.put(classname, style);
                }
            }
            if (style != null) {
                setter.setup(style);
            }
        }
        afterApplied(builderMap);
    }
}
