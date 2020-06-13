package com.moon.core.lang;

import com.moon.core.lang.support.ClassSupport;
import com.moon.core.util.ListUtil;

import java.util.*;

import static com.moon.core.lang.ThrowUtil.runtime;
import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

/**
 * @author benshaoye
 */
public final class ClassUtil {

    private ClassUtil() { noInstanceError(); }

    static final Map<Class, Class> PRIMITIVE_TO_WRAPPER_MAP;
    static final Map<Class, Class> WRAPPER_TO_PRIMITIVE_MAP;

    static {
        final Class[] PRIMITIVE_CLASSES = {
            boolean.class, byte.class, char.class, short.class, int.class, long.class, float.class, double.class
        }, WRAPPER_CLASSES = {
            Boolean.class,
            Byte.class,
            Character.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class
        };

        Map<Class, Class> map1 = new HashMap<>();
        Map<Class, Class> map2 = new HashMap<>();

        for (int i = 0; i < PRIMITIVE_CLASSES.length; i++) {
            map1.put(PRIMITIVE_CLASSES[i], WRAPPER_CLASSES[i]);
            map2.put(WRAPPER_CLASSES[i], PRIMITIVE_CLASSES[i]);
        }

        PRIMITIVE_TO_WRAPPER_MAP = unmodifiableMap(map1);
        WRAPPER_TO_PRIMITIVE_MAP = unmodifiableMap(map2);
    }


    public static List<Class> getAllInterfaces(Class type) {
        return ClassSupport.addAllInterfaces(ListUtil.newArrayList(), type);
    }

    public static List<Class> getAllSuperclasses(Class type) {
        List<Class> classes = new ArrayList<>();
        do {
            if ((type = type.getSuperclass()) != null) {
                classes.add(type);
            } else {
                return unmodifiableList(classes);
            }
        } while (true);
    }

    public static boolean isAssignableFrom(Class cls1, Class cls2) {
        return cls1 != null && cls2 != null && cls1.isAssignableFrom(cls2);
    }

    public static boolean isInstanceOf(Object o, Class c) { return c != null && c.isInstance(o); }

    public static boolean isInnerClass(Class clazz) { return clazz != null && clazz.getName().indexOf('$') > 0; }

    public static Class[] getClasses(Object... objects) {
        int len = objects.length;
        Class[] classes = new Class[len];
        for (int i = 0; i < len; i++) {
            if (objects[i] != null) {
                classes[i] = objects[i].getClass();
            }
        }
        return classes;
    }

    /**
     * 内部工具方法
     *
     * @param clazz
     */
    public final static Class toWrapperClass(Class clazz) { return PRIMITIVE_TO_WRAPPER_MAP.get(clazz); }

    public final static Class toPrimitiveClass(Class clazz) { return WRAPPER_TO_PRIMITIVE_MAP.get(clazz); }

    public static Class forName(String className) {
        try {
            return Class.forName(className);
        } catch (Exception e) {
            return runtime(e);
        }
    }

    public static Class forNameOrNull(String className) {
        try {
            return Class.forName(className);
        } catch (Exception e) {
            return null;
        }
    }

    public static final <T> Class<T> requireExtendsOf(Class<T> type, Class expectType) {
        if (!expectType.isAssignableFrom(type)) {
            throw new IllegalArgumentException(String.valueOf(type));
        }
        return type;
    }

    public static <T> T newInstance(Class<T> targetClass) {
        try {
            return targetClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Can not new instance of: " + targetClass, e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Can not access class of: " + targetClass, e);
        }
    }
}
