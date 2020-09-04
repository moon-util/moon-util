package com.moon.data.registry;

import com.moon.core.lang.StringUtil;
import com.moon.data.service.DataService;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author moonsky
 */
public final class RecordRegistry {

    private RecordRegistry() {}

    private final static Map<Class, Function<Serializable, ?>> container = new HashMap<>();

    public static synchronized void forceRegistry(Class type, Function<Serializable, ?> converter) {
        container.put(type, converter);
    }

    public static synchronized void registryIfAbsent(Class type, Function<Serializable, ?> converter) {
        container.putIfAbsent(type, converter);
    }

    public static synchronized void registry(Class type, Function<Serializable, ?> converter) {
        if (container.containsKey(type)) {
            throw new RecordRegistryException(type);
        }
        container.put(type, converter);
    }

    public static <T> T getByRegistered(Class type, Serializable id) { return (T) container.get(type).apply(id); }
}
