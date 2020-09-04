package com.moon.data.identifier;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.reflect.ConstructorUtil;
import com.moon.core.util.SetUtil;
import com.moon.core.util.TypeUtil;
import com.moon.core.util.converter.TypeCaster;
import com.moon.data.IdentifierGenerator;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author moonsky
 */
public final class IdentifierUtil {

    private final static String packageName = IdentifierUtil.class.getPackage().getName();

    private final static Set<Class> identifierTypes = new HashSet<>();

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

    public static synchronized void addIdentifierType(Class identifierClass) {
        identifierTypes.add(identifierClass);
    }

    public static IdentifierGenerator newInstance(String description, String key) {
        if (StringUtil.isBlank(description)) {
            Set<Class> types = identifierTypes;
            if (types.size() == 1) {
                Class type = SetUtil.requireGet(types, 0);
                if (type == Long.class) {
                    return new LongSnowflakeIdentifier();
                }
                if (type == String.class) {
                    return new StringSnowflakeIdentifier();
                }
            }
            throw new IllegalStateException("Unknown identifier type, you must assign spring property of: " + key);
        }
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
                } catch (Throwable t) {
                    continue;
                }
            }
        }
        throw new IllegalStateException(description);
    }
}
