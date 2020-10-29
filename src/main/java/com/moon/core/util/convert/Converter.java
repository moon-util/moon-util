package com.moon.core.util.convert;

import java.util.Set;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public interface Converter {

    /**
     * 支持从什么类型转换
     *
     * @return 支持的类型
     */
    Set<Class<?>> getSupportFromTypes();

    /**
     * 支持转换成什么类型
     *
     * @return 支持转换结果的目标类型
     */
    Set<Class<?>> getSupportToTypes();

    /**
     * 是否支持从{@code fromType}到{@code toType}的转换
     *
     * @param fromType 源类型
     * @param toType   转换结果类型
     *
     * @return 支持从源类型转换到结果类型返回 true，否则返回 false
     */
    default boolean isSupported(Class<?> fromType, Class<?> toType) {
        return getSupportFromTypes().contains(fromType) && getSupportFromTypes().contains(fromType);
    }

    /**
     * 执行转换
     *
     * @param fromValue 源值
     *
     * @return 转换结果值
     */
    Object exec(Object fromValue);

    /**
     * 执行转换
     *
     * @param fromValue 源值
     * @param <F>       源类型
     * @param <T>       转换结果数据类型
     *
     * @return 转换结果值
     */
    default <F, T> T convert(F fromValue) { return (T) exec(fromValue); }

    /**
     * 转换为{@link Function}
     *
     * @return Function 函数
     */
    default <F, T> Function<F, T> asFunction() { return this::convert; }
}
