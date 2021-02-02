package com.moon.accessor.type;

import com.moon.accessor.exception.Exception2;
import org.joda.time.DateTime;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    private final static Map<Class<?>, TypeHandler<?>> DEFAULT_HANDLERS_MAP;
    private final static Map<Object, TypeHandler<?>> TYPED_HANDLERS_MAP;

    static {
        Map<Class<?>, TypeHandler<?>> handlerMap = new HashMap<>();
        Map<Object, TypeHandler<?>> typedHandlersMap = new HashMap<>();

        for (JavaTypeHandlersEnum value : JavaTypeHandlersEnum.values()) {
            handlerMap.putIfAbsent(value.supportClass, value);
            String typedKey = toKey(value.primaryJdbcType, value.supportClass);
            typedHandlersMap.put(typedKey, value);
            for (int compatibleType : value.getCompatibleTypes()) {
                typedKey = toKey(compatibleType, value.supportClass);
                typedHandlersMap.put(typedKey, value);
            }
        }

        if (JODA_IMPORTED) {
            for (JodaTypeHandlersEnum value : JodaTypeHandlersEnum.values()) {
                handlerMap.putIfAbsent(value.supportClass, value);
                String typedKey = toKey(value.primaryJdbcType, value.supportClass);
                typedHandlersMap.put(typedKey, value);
                for (int compatibleType : value.getCompatibleTypes()) {
                    typedKey = toKey(compatibleType, value.supportClass);
                    typedHandlersMap.put(typedKey, value);
                }
            }
        }

        TYPED_HANDLERS_MAP = Collections.unmodifiableMap(typedHandlersMap);
        DEFAULT_HANDLERS_MAP = Collections.unmodifiableMap(handlerMap);
    }

    @SuppressWarnings({"rawtypes"})
    public static <T> JdbcType findEffectiveJdbcType(Class<T> propertyType, JdbcType jdbcType) {
        if (jdbcType != JdbcType.AUTO) {
            return jdbcType;
        }
        TypeHandler handler = findBuiltInHandler(propertyType, jdbcType);
        if (handler instanceof JavaTypeHandlersEnum) {
            return JdbcType.valueOf(((JavaTypeHandlersEnum) handler).primaryJdbcType);
        }
        if (JODA_IMPORTED && handler instanceof JodaTypeHandlersEnum) {
            return JdbcType.valueOf(((JodaTypeHandlersEnum) handler).primaryJdbcType);
        }
        if (handler instanceof JavaEnumOrdinalHandler) {
            return JdbcType.INTEGER;
        }
        if (handler instanceof JavaEnumStringHandler) {
            return JdbcType.VARCHAR;
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
            handler = DEFAULT_HANDLERS_MAP.get(propertyType);
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
