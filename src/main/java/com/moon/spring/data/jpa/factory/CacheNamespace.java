package com.moon.spring.data.jpa.factory;

import com.moon.data.Record;

import java.util.HashMap;
import java.util.Map;

/**
 * @author moonsky
 */
public abstract class CacheNamespace {

    private final static Map<Class, String> CACHE_NAMESPACE = new HashMap<>();

    /**
     * 只有在初始化时创建
     *
     * @param type
     * @param namespace
     */
    final synchronized static void putNamespace(Class type, String namespace) {
        CACHE_NAMESPACE.put(type, namespace);
    }

    protected static String obtainCacheNamespace(Record record) {
        return CACHE_NAMESPACE.get(record.getClass());
    }

    protected static String deduceCacheNamespace(Record record) {
        final Class recordClass = record.getClass();
        Class thisClass = recordClass;
        do {
            // 优先直接获取
            String namespace = CACHE_NAMESPACE.get(thisClass);
            if (namespace != null) {
                return namespace;
            }
            // 或者从直接父类中获取
            thisClass = thisClass.getSuperclass();
            if (thisClass == Object.class || thisClass == null) {
                break;
            }
        } while (true);
        // 查找唯一子类
        String implementationName = null;
        for (Map.Entry<Class, String> entry : CACHE_NAMESPACE.entrySet()) {
            Class presentClass = entry.getKey();
            if (recordClass.isAssignableFrom(presentClass)) {
                if (implementationName == null) {
                    implementationName = entry.getValue();
                } else {
                    return null;
                }
            }
        }
        return implementationName;
    }
}
