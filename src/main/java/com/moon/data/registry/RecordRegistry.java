package com.moon.data.registry;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 注册数据读取器
 *
 * @author moonsky
 */
public final class RecordRegistry {

    private RecordRegistry() {}

    private final static Map<Class<?>, Function<Serializable, ?>> CONTAINER = new HashMap<>();

    public static synchronized void forceRegister(Class<?> type, Function<Serializable, ?> converter) {
        CONTAINER.put(type, converter);
    }

    public static synchronized void registerIfAbsent(Class<?> type, Function<Serializable, ?> converter) {
        CONTAINER.putIfAbsent(type, converter);
    }

    public static synchronized void register(Class<?> type, Function<Serializable, ?> converter) {
        if (CONTAINER.containsKey(type)) {
            throw new RecordDuplicateRegistryException(type);
        }
        CONTAINER.put(type, converter);
    }

    public static <T> T getByRegistered(Class<T> type, Serializable id) { return (T) CONTAINER.get(type).apply(id); }
}
