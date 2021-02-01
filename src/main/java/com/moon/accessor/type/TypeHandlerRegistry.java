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

    private final static boolean JODA_IMPORTED;
    private final static Map<Object, TypeHandler<?>> DEFAULT_HANDLERS_MAP;
    private final Map<Object, TypeHandler<?>> handlersMap;

    public final static TypeHandlerRegistry DEFAULT = new TypeHandlerRegistry(Collections.emptyMap());

    static {
        boolean jodaImported;
        try {
            jodaImported = DateTime.class.toString() != null;
        } catch (Throwable t) {
            jodaImported = false;
        }
        JODA_IMPORTED = jodaImported;
    }

    static {
        Map<Object, TypeHandler<?>> handlerMap = new HashMap<>();

        for (JavaTypeHandlersEnum value : JavaTypeHandlersEnum.values()) {
            handlerMap.put(TypeUsing2.toKey(value.jdbcType, value.supportClasses), value);
        }

        if (JODA_IMPORTED) {
            for (JodaTypeHandlersEnum value : JodaTypeHandlersEnum.values()) {
                handlerMap.put(TypeUsing2.toKey(value.jdbcType, value.supportClasses), value);
            }
        }

        DEFAULT_HANDLERS_MAP = Collections.unmodifiableMap(handlerMap);
    }

    public TypeHandlerRegistry() { this(new ConcurrentHashMap<>()); }

    public TypeHandlerRegistry(Map<Object, TypeHandler<?>> handlersMap) { this.handlersMap = handlersMap; }
}
