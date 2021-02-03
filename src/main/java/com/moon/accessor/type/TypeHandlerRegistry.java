package com.moon.accessor.type;

import com.moon.accessor.exception.Exception2;
import org.joda.time.DateTime;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import static com.moon.accessor.type.TypeUsing2.toKey;

/**
 * @author benshaoye
 */
public class TypeHandlerRegistry {

    private final static boolean JODA_IMPORTED;

    static {
        boolean jodaImported;
        try {
            @SuppressWarnings("all")
            String v = DateTime.class.toString();
            jodaImported = true;
        } catch (Throwable ignored) {
            jodaImported = false;
        }
        JODA_IMPORTED = jodaImported;
    }

    private final static Map<Class<?>, TypeHandler<?>> HANDLERS_MAP;
    private final static Map<Object, TypeHandler<?>> TYPED_HANDLERS_MAP;
    private final static Map<Class<?>, TypeHandler<?>> DEF_HANDLERS_MAP;
    private final static Map<Object, TypeHandler<?>> DEF_TYPED_HANDLERS_MAP;

    static {
        Map<Class<?>, TypeHandler<?>> handlerMap = new HashMap<>();
        Map<Object, TypeHandler<?>> typedHandlersMap = new HashMap<>();
        @SuppressWarnings("rawtypes")
        ServiceLoader<TypeHandler> loader = ServiceLoader.load(TypeHandler.class);
        for (TypeHandler<?> handler : loader) {
        }
        DEF_TYPED_HANDLERS_MAP = Collections.unmodifiableMap(typedHandlersMap);
        DEF_HANDLERS_MAP = Collections.unmodifiableMap(handlerMap);
    }

    static {
        Map<Class<?>, TypeHandler<?>> handlerMap = new HashMap<>();
        Map<Object, TypeHandler<?>> typedHandlersMap = new HashMap<>();

        for (JavaTypeHandlersEnum value : JavaTypeHandlersEnum.values()) {
            handlerMap.putIfAbsent(value.supportClass, value);
            for (int type : value.supportJdbcTypes()) {
                typedHandlersMap.put(toKey(type, value.supportClass), value);
            }
        }

        if (JODA_IMPORTED) {
            for (JodaTypeHandlersEnum value : JodaTypeHandlersEnum.values()) {
                handlerMap.putIfAbsent(value.supportClass, value);
                for (int type : value.supportJdbcTypes()) {
                    typedHandlersMap.put(toKey(type, value.supportClass), value);
                }
            }
        }

        TYPED_HANDLERS_MAP = Collections.unmodifiableMap(typedHandlersMap);
        HANDLERS_MAP = Collections.unmodifiableMap(handlerMap);
    }

    @SuppressWarnings({"rawtypes"})
    public static <T> JdbcType findEffectiveJdbcType(Class<T> propertyType, JdbcType jdbcType) {
        if (jdbcType != JdbcType.AUTO) {
            return jdbcType;
        }
        TypeHandler handler = findBuiltInHandler(propertyType, jdbcType);
        if (handler instanceof TypeJdbcHandler) {
            return JdbcType.valueOf(((TypeJdbcHandler<?>) handler).supportJdbcTypes()[0]);
        }
        return JdbcType.JAVA_OBJECT;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> TypeHandler<T> findEffectiveHandler(
        Class<TypeHandler<T>> handlerClass, Class<T> propertyType, JdbcType jdbcType
    ) {
        TypeHandler handler = ifEffected(handlerClass);
        if (handler != null) {
            return handler;
        }
        return findBuiltInHandler(propertyType, jdbcType);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> TypeHandler findBuiltInHandler(Class<T> propertyType, JdbcType jdbcType) {
        TypeHandler handler;
        if (jdbcType == JdbcType.AUTO) {
            handler = HANDLERS_MAP.get(propertyType);
            if (handler == null && propertyType.isEnum()) {
                handler = new JavaEnumOrdinalHandler(propertyType);
            }
        } else {
            handler = TYPED_HANDLERS_MAP.get(toKey(jdbcType.getTypeCode(), propertyType));
            if (handler == null && propertyType.isEnum()) {
                if (jdbcType == JdbcType.INTEGER) {
                    handler = new JavaEnumOrdinalHandler(propertyType);
                } else if (jdbcType == JdbcType.VARCHAR ||
                    jdbcType == JdbcType.CHAR ||
                    jdbcType == JdbcType.LONGVARCHAR) {
                    handler = new JavaEnumStringHandler(propertyType);
                }
            }
        }
        handler = handler == null ? JavaTypeHandlersEnum.forObject : handler;
        return handler;
    }

    private static <T> TypeHandler<T> ifEffected(Class<TypeHandler<T>> handlerClass) {
        if (handlerClass != null && !Modifier.isAbstract(handlerClass.getModifiers()) && !handlerClass.isInterface()) {
            try {
                return handlerClass.newInstance();
            } catch (InstantiationException e) {
                throw Exception2.with(e, "实例化错误: " + handlerClass.getCanonicalName());
            } catch (IllegalAccessException e) {
                throw Exception2.with(e, "不可访问: " + handlerClass.getCanonicalName());
            }
        }
        return null;
    }
}
