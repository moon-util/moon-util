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

    private final static Map<Object, TypeHandler<?>> DEFAULT_HANDLERS_MAP;
    private final Map<Object, TypeHandler<?>> handlersMap;

    public final static TypeHandlerRegistry DEFAULT = new TypeHandlerRegistry(Collections.emptyMap());

    static {
        Map<Object, TypeHandler<?>> handlerMap = new HashMap<>();

        for (JavaTypeHandlersEnum value : JavaTypeHandlersEnum.values()) {
            handlerMap.put(TypeUsing2.toKey(value.jdbcType, value.supportClasses), value);
        }

        try {
            @SuppressWarnings("all")
            String stringify = DateTime.class.toString();
            for (JodaTypeHandlersEnum value : JodaTypeHandlersEnum.values()) {
                handlerMap.put(TypeUsing2.toKey(value.jdbcType, value.supportClasses), value);
            }
        } catch (Throwable ignored) { }

        DEFAULT_HANDLERS_MAP = Collections.unmodifiableMap(handlerMap);
    }

    public TypeHandlerRegistry() { this(new ConcurrentHashMap<>()); }

    private TypeHandlerRegistry(Map<Object, TypeHandler<?>> handlersMap) { this.handlersMap = handlersMap; }
}
