package com.moon.spring.jpa.identity;

import com.moon.core.lang.ClassUtil;
import com.moon.core.lang.reflect.ConstructorUtil;
import com.moon.core.util.Assert;
import com.moon.core.util.TypeUtil;
import com.moon.core.util.converter.TypeCaster;
import org.hibernate.id.IdentifierGenerator;

import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * @author moonsky
 */
final class IdentifierUtil {

    private final static String packageName;

    static {
        packageName = IdentifierUtil.class.getPackage().getName();
    }

    private IdentifierUtil() { }

    private static void assertNot(String classname, Class<?> type) {
        if (type.getName().equals(classname)) {
            throw new IllegalStateException("不允许：" + classname);
        }
    }

    private static String assertClassname(String classname) {
        assertNot(classname, IdentifierUtil.class);
        assertNot(classname, Identifier.class);
        return classname;
    }

    private static Class toClassOrNull(String classname) {
        return ClassUtil.forName(assertClassname(classname));
    }

    private static Class toIdentifierClass(String classname) {
        Class type = toClassOrNull(classname);
        if (type == null) {
            classname = packageName + "." + classname;
            type = toClassOrNull(classname);
        }
        if (type == null) {
            classname = classname + "Identifier";
            type = toClassOrNull(classname);
        }
        return Objects.requireNonNull(type);
    }

    public static IdentifierGenerator newInstance(String description) {
        Assert.hasText(description);
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
