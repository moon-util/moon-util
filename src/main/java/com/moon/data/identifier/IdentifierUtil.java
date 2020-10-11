package com.moon.data.identifier;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.reflect.ConstructorUtil;
import com.moon.core.util.SetUtil;
import com.moon.core.util.TypeUtil;
import com.moon.core.util.converter.TypeCaster;
import com.moon.data.IdentifierGenerator;
import com.moon.data.Record;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * @author moonsky
 */
@SuppressWarnings("all")
public final class IdentifierUtil {

    private final static String packageName = IdentifierUtil.class.getPackage().getName();
    private final static Set<Class> USED_IDENTIFIER_TYPES = new HashSet<>();

    private IdentifierUtil() { ThrowUtil.noInstanceError(); }

    private static void assertNot(String classname, Class<?> type) {
        if (type.getName().equals(classname)) {
            throw new IllegalStateException("不允许：" + classname);
        }
    }

    private static String assertClassname(String classname) {
        assertNot(classname, IdentifierUtil.class);
        return classname;
    }

    private static Class toClassOrNull(String classname) {
        return ClassUtil.forNameOrNull(assertClassname(classname));
    }

    private static Class toIdentifierClass(String classname) {
        // 类全名
        Class type = toClassOrNull(classname);
        if (type == null) {
            // 类简写
            classname = packageName + "." + classname;
            type = toClassOrNull(classname);
        }
        if (type == null) {
            // 省略后缀的简写
            classname = classname + "Identifier";
            type = toClassOrNull(classname);
        }
        return Objects.requireNonNull(type);
    }

    /*
     * **********************************************************************************************
     * * default methods ****************************************************************************
     * **********************************************************************************************
     */

    static <T> T returnIfRecordIdNotNull(Object entity, Object o, BiFunction<Object, Object, T> generator) {
        if (entity instanceof Record) {
            Record record = (Record) entity;
            Object id = record.getId();
            if (id != null || !record.isNew()) {
                return (T) id;
            }
        }
        return generator.apply(entity, o);
    }

    static String returnIfRecordIdNotEmpty(Object entity, Object o, BiFunction<Object, Object, String> generator) {
        if (entity instanceof Record) {
            Record record = (Record) entity;
            Object id = record.getId();
            if (id instanceof CharSequence) {
                String strId = id.toString();
                if (!strId.isEmpty()) {
                    return strId;
                }
            }
            if (!record.isNew()) {
                return id == null ? null : id.toString();
            }
        }
        return generator.apply(entity, o);
    }


    /*
     * **********************************************************************************************
     * * public methods *****************************************************************************
     * **********************************************************************************************
     */

    public static void addUsedIdentifierType(Class idType) { USED_IDENTIFIER_TYPES.add(idType); }

    /**
     * 创建 id 生成器
     *
     * @param description 描述
     * @param key         spring properties key
     *
     * @return ID 生成器
     */
    public static IdentifierGenerator newIdentifierGenerator(String description, String key) {
        // 默认雪花算法
        if (StringUtil.isBlank(description)) {
            Set<Class> types = USED_IDENTIFIER_TYPES;
            if (types.size() == 1) {
                Class type = SetUtil.requireGet(types, 0);
                if (type == Long.class) {
                    return new LongSequenceIdentifier();
                }
                if (type == String.class) {
                    return new StringSequenceIdentifier();
                }
            }
            throw new IllegalStateException("Unknown identifier type, you must assign spring property of: " + key);
        }
        // 其他方式主键策略
        String[] descriptions = description.split(":");
        Class type = toIdentifierClass(descriptions[0]);
        final int length = descriptions.length;
        if (length == 1) {
            return (IdentifierGenerator) ConstructorUtil.newInstance(type);
        }
        final int argsLen = length - 1;
        TypeCaster caster = TypeUtil.cast();
        for (Constructor constructor : type.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == argsLen) {
                try {
                    Object[] args = new Object[argsLen];
                    Class[] types = constructor.getParameterTypes();
                    for (int i = 0; i < argsLen; i++) {
                        Class argType = types[i];
                        String param = descriptions[i + 1];
                        args[i] = caster.toType(param, argType);
                    }
                    return (IdentifierGenerator) ConstructorUtil.newInstance(type, args);
                } catch (Throwable ignored) {
                }
            }
        }
        throw new IllegalStateException(description);
    }
}
