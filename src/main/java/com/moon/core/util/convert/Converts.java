package com.moon.core.util.convert;

import com.moon.core.util.converter.Converter;

import java.util.Set;
import java.util.function.Function;

/**
 * @author benshaoye
 * @see Function
 * @see #asFunction()
 */
public interface Converts<F, T> extends Converter<F, T> {

    /**
     * 执行转换
     *
     * @param o 数据源
     *
     * @return 转换后的值
     */
    @Override
    T convert(F o);

    /**
     * 支持转换成什么数据类型
     *
     * @return 目标数据类型集合
     */
    Set<Class> supportsTo();

    /**
     * 支持从什么数据类型转换
     *
     * @return 数据类型来源
     */
    Set<Class> supportsFrom();

    /**
     * 转换为{@link Function}
     *
     * @return Function 函数
     */
    default Function<F, T> asFunction() { return f -> convert(f); }

    /**
     * 是否支持转换成目标类型
     *
     * @param type 期望的目标类型
     *
     * @return 当前转换器支持转换成目标类型时返回 true，否则返回 false
     */
    default boolean supportsTo(Class type) { return supportsTo().contains(type); }

    /**
     * 是否支持从指定类型转换
     *
     * @param type 期望从什么类型转换
     *
     * @return 当前转换器支持从指定类型转换成目标类型时返回 true，否则返回 false
     */
    default boolean supportsFrom(Class type) { return supportsFrom().contains(type); }
}
