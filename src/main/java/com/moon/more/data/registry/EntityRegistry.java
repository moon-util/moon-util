package com.moon.more.data.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author moonsky
 */
public final class EntityRegistry {

    private EntityRegistry() {}

    private final static Map<Class, Function<String, ?>> container = new HashMap<>();

    public static synchronized void forceRegistry(Class type, Function<String, ?> converter) {
        container.put(type, converter);
    }

    public static synchronized void registryIfAbsent(Class type, Function<String, ?> converter) {
        container.putIfAbsent(type, converter);
    }

    public static synchronized void registry(Class type, Function<String, ?> converter) {
        if (container.containsKey(type)) {
            throw new EntityRegistryException(type);
        }
        container.put(type, converter);
    }

    public static <T> T getByRegistered(Class type, String id) { return (T) container.get(type).apply(id); }
}
