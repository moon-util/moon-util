package com.moon.mapping.processing;

/**
 * @author benshaoye
 */
interface Mappable {

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
    boolean isPrimitiveGetter();

    /**
     * setter 方法参数是否是基本数据类型
     *
     * @return setter 方法参数是否是基本数据类型
     */
    boolean isPrimitiveSetter();
}
