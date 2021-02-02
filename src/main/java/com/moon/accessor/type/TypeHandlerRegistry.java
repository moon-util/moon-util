package com.moon.accessor.type;

import org.joda.time.DateTime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author benshaoye
 */
public class TypeHandlerRegistry {

    private final static Map<Class<?>, TypeHandler<?>> DEFAULT_HANDLERS_MAP;
    private final Map<Class<?>, TypeHandler<?>> handlersMap;

    public final static TypeHandlerRegistry DEFAULT = new TypeHandlerRegistry(Collections.emptyMap());

    static {
        Map<Class<?>, TypeHandler<?>> handlerMap = new HashMap<>();

        for (JavaTypeHandlersEnum value : JavaTypeHandlersEnum.values()) {
            handlerMap.put(value.supportClass, value);
        }

        try {
            @SuppressWarnings("all") String stringify = DateTime.class.toString();
            for (JodaTypeHandlersEnum value : JodaTypeHandlersEnum.values()) {
                handlerMap.put(value.supportClass, value);
            }
        } catch (Throwable ignored) { }

        DEFAULT_HANDLERS_MAP = Collections.unmodifiableMap(handlerMap);
    }

    public TypeHandlerRegistry() { this(new ConcurrentHashMap<>()); }

    private TypeHandlerRegistry(Map<Class<?>, TypeHandler<?>> handlersMap) { this.handlersMap = handlersMap; }

    public <T> TypeHandler<T> findFor(Class<T> propertyType) {
        TypeHandler<?> handler = DEFAULT_HANDLERS_MAP.get(propertyType);
        if (handler == null && propertyType.isEnum()) {
        }
        // support jdbc type
        return (TypeHandler<T>) handler;
    }
}
