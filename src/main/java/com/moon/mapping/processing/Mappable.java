package com.moon.mapping.processing;

import javax.lang.model.element.TypeElement;

/**
 * @author benshaoye
 */
interface Mappable {

    /**
     * property name
     *
     * @return property name
     */
    String getName();

    /**
     * 返回当前属性所属于的最终类
     *
     * @return 最终类
     */
    TypeElement getThisClass();

    /**
     * 返回当前属性所在的最终类
     *
     * <pre>
     * public class A {
     *     private String str;
     * }
     * public class B extends A {
     *     private int age;
     * }
     * </pre>
     * <p>
     * 不论是属性 age 或者 str，最终类均是 B
     *
     * @return 实际使用这个属性的最终类
     */
    default String getThisClassname() {
        return ElemUtils.getQualifiedName(getThisClass());
    }

    /**
     * 查找自定义类型转换器的方法名
     *
     * @param key {@link ElemUtils#toConvertKey(String, String, String)}
     *
     * @return
     */
    String findConvertMethod(String key);

    /**
     * 是否有 getter
     *
     * @return getter
     */
    boolean hasGetterMethod();

    /**
     * 是否有 setter
     *
     * @return setter
     */
    boolean hasSetterMethod();

    /**
     * getter 方法名
     *
     * @return getter 方法名
     */
    String getGetterName();

    /**
     * setter 方法名
     *
     * @return setter 方法名
     */
    String getSetterName();

    /**
     * getter 方法返回值声明类型
     *
     * @return getter 声明类型
     */
    String getGetterDeclareType();

    /**
     * setter 方法参数声明类型
     *
     * @return setter 声明类型
     */
    String getSetterDeclareType();

    /**
     * getter 返回值实际类型
     *
     * @return getter 返回值实际类型
     */
    String getGetterActualType();

    /**
     * setter 方法参数声明类型
     *
     * @return setter 声明类型
     */
    String getSetterActualType();

    /**
     * getter 方法最终实际返回值类型，不能是泛型
     *
     * @return getter 实际类型
     */
    String getGetterFinalType();

    /**
     * setter 方法参数最终实际类型，不能是泛型
     *
     * @return setter 实际类型
     */
    String getSetterFinalType();

    /**
     * getter 返回值类型是否是基本数据类型
     *
     * @return setter 返回值类型是否是基本数据类型
     */
    default boolean isPrimitiveGetter() {
        return StringUtils.isPrimitive(getGetterFinalType());
    }

    /**
     * setter 方法参数是否是基本数据类型
     *
     * @return setter 方法参数是否是基本数据类型
     */
    default boolean isPrimitiveSetter() {
        return StringUtils.isPrimitive(getSetterFinalType());
    }

    /**
     * getter 方法是否包含泛型
     *
     * @return
     */
    default boolean isGetterGenerify() {
        return getGetterActualType() != null;
    }

    /**
     * setter 方法是否包含泛型
     *
     * @return
     */
    default boolean isSetterGenerify() {
        return getSetterActualType() != null;
    }

    /**
     * 返回包装类，主要是基本数据类型到对应包装类的转换
     *
     * @return 包装类
     */
    default String getWrappedGetterType() {
        return StringUtils.toWrappedType(getGetterFinalType());
    }

    /**
     * 返回包装类，主要是基本数据类型到对应包装类的转换
     *
     * @return 包装类
     */
    default String getWrappedSetterType() {
        return StringUtils.toWrappedType(getSetterFinalType());
    }
}
