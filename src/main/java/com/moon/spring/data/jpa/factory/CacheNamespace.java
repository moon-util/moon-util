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
        final Class recordClass = record.getClass();
        Class thisClass = recordClass;
        do {
            String namespace = CACHE_NAMESPACE.get(thisClass);
            if (namespace != null) {
                return namespace;
            }
            thisClass = thisClass.getSuperclass();
            if (thisClass == Object.class || thisClass == null) {
                break;
            }
        } while (true);
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
