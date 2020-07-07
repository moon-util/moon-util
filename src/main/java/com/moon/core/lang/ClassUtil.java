package com.moon.core.lang;

import com.moon.core.lang.support.ClassSupport;
import com.moon.core.util.SetUtil;

import java.util.*;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

/**
 * @author moonsky
 */
public final class ClassUtil {

    private ClassUtil() { noInstanceError(); }

    /**
     * 基本数据类型到包装类的映射
     */
    static final Map<Class, Class> PRIMITIVE_TO_WRAPPER_MAP;
    /**
     * 包装类到基本数据类型的映射
     */
    static final Map<Class, Class> WRAPPER_TO_PRIMITIVE_MAP;

    static {
        final Class[] PRIMITIVE_CLASSES = {
            boolean.class, byte.class, char.class, short.class,

            int.class, long.class, float.class, double.class
        }, WRAPPER_CLASSES = {
            Boolean.class, Byte.class, Character.class, Short.class,

            Integer.class, Long.class, Float.class, Double.class
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

    /**
     * 获取指定类实现(或继承)的所有接口
     *
     * @param type 目标类
     *
     * @return 所实现的所有接口集合
     */
    public static Set<Class> getAllInterfaces(Class<?> type) {
        return ClassSupport.addAllInterfaces(SetUtil.newSet(), type);
    }

    /**
     * 获取指定类实现(或继承)的所有接口，以数组形式返回
     *
     * @param type 目标类
     *
     * @return 所实现的所有接口数组
     */
    public static Class<?>[] getAllInterfacesArr(Class type) {
        return SetUtil.toArray(getAllInterfaces(type), Class[]::new);
    }

    /**
     * 获取类继承的所有父类集合(只包含类，不包含接口)
     *
     * @param type 目标类
     *
     * @return 父类集合
     */
    public static List<Class> getAllSuperclasses(Class type) {
        return getAllSuperclasses(type, null);
    }

    /**
     * 获取类继承的所有父类集合(只包含类，不包含接口)
     *
     * @param type      目标类
     * @param stopClass 停止查找位置
     *
     * @return 父类集合
     */
    public static List<Class> getAllSuperclasses(Class type, Class stopClass) {
        List<Class> classes = new ArrayList<>();
        do {
            if ((type = type.getSuperclass()) != null && type != stopClass) {
                classes.add(type);
            } else {
                return unmodifiableList(classes);
            }
        } while (true);
    }

    /**
     * 检查类是否是另一个类的子类
     *
     * @param thisClass  待测类
     * @param superClass 超类
     *
     * @return true：待测类是继承自超类；否则返回 false
     */
    public static boolean isExtendOf(Class thisClass, Class superClass) {
        return thisClass != null && superClass != null && superClass.isAssignableFrom(thisClass);
    }

    /**
     * 检查对象是否是类的实例
     *
     * @param o 数据对象
     * @param c 目标类
     *
     * @return 当数据对象是目标类的实例时返回 true；否则返回 false
     */
    public static boolean isInstanceOf(Object o, Class c) { return c != null && c.isInstance(o); }

    /**
     * 检查目标类是否是内部类
     *
     * @param clazz 待测目标类
     *
     * @return 当目标类是内部类时返回 true；否则返回 false
     */
    public static boolean isInnerClass(Class clazz) { return clazz != null && clazz.getName().indexOf('$') > 0; }

    /**
     * 获取一组数据的类名，跳过 null 值
     *
     * @param objects 待测数据
     *
     * @return 按待测数据位置，返回每个数据的数据类型
     */
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
     * 返回基本数据类型的包装类
     *
     * @param clazz 基本数据类型
     *
     * @return 基本数据类型对应的包装类；如果 clazz 不是基本数据类，而是其他类时返回 null
     */
    public static Class toWrapperClass(Class clazz) { return PRIMITIVE_TO_WRAPPER_MAP.get(clazz); }

    /**
     * 返回包装类对应的基本数据类型
     *
     * @param clazz 包装类
     *
     * @return 包装类对应的基本数据类型；如果 clazz 不是包装类，而是其他类时返回 null
     */
    public static Class toPrimitiveClass(Class clazz) { return WRAPPER_TO_PRIMITIVE_MAP.get(clazz); }

    /**
     * 参考{@link Class#forName(String)}, 屏蔽了检查异常
     *
     * @param className 类全名
     *
     * @return 目标类
     *
     * @throws ClassNotFoundException 找不到类时抛出异常
     * @throws IllegalStateException  找不到类时抛出异常
     */
    public static Class forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 参考{@link Class#forName(String)}, 屏蔽了检查异常
     *
     * @param className 类全名
     *
     * @return 目标类或 null
     */
    public static Class forNameOrNull(String className) {
        try {
            return Class.forName(className);
        } catch (Exception e) {
            return null;
        }
    }

    public static final <T> Class<T> requireExtendBy(Class<T> type, Class expectType) {
        if (!expectType.isAssignableFrom(type)) {
            throw new IllegalArgumentException(String.valueOf(type));
        }
        return type;
    }

    /**
     * 根据类实例化一个这个类的对象
     *
     * @param targetClass 目标类
     * @param <T>         目标类类型
     *
     * @return 目标类实例化后的实例
     *
     * @throws IllegalStateException 当实例化过程中遇到错误是抛出异常，如因修饰符导致的无法访问
     *                               或构造器内部错误等
     */
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
