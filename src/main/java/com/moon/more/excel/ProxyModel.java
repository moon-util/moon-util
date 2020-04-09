package com.moon.more.excel;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
class ProxyModel<FROM, R, K, B extends ProxyBuilder<FROM, R>, S extends ProxySetter<R, K>> {

    private Map<String, B> builderMap;
    private Map<String, R> defined;
    private LinkedHashMap<S, String> setterMap;
    private final boolean cacheDefined;

    private Map<String, B> getBuilderMap() { return builderMap; }

    private Map<String, R> getDefined() { return defined; }

    private LinkedHashMap<S, String> getSetterMap() { return setterMap; }

    private Map<String, B> ensureBuilderMap() {
        Map<String, B> builderMap = getBuilderMap();
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

    ProxyModel<FROM, R, K, B, S> removeSetter(S setter) {
        ensureSetterMap().remove(setter);
        return this;
    }

    protected ProxyModel() { this(true); }

    protected ProxyModel(boolean cacheDefined) {
        this.cacheDefined = cacheDefined;
    }

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

    private void useUncached(FROM from) {
        Map<String, B> builderMap = getBuilderMap();
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
    }

    private void useCached(FROM from) {
        Map<String, R> defined = getDefined();
        Map<String, B> builderMap = getBuilderMap();
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
    }
}
