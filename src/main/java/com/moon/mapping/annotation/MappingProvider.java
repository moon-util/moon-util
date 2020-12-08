package com.moon.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义转换器【注意与{@link MappingConverter}的区别】
 * <p>
 * 注解在{@code getter}方法上，意为：
 * <pre>
 * 为{@link #provideFor()}类中{@link #value()}字段提供值。
 * </pre>
 * 要求 getter 是公共成员方法，且不包含参数，返回值和转换对应的字段类型完全相同，否则就忽略掉
 *
 * @author benshaoye
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface MappingProvider {

    /**
     * 对应{@link #provideFor()}类中的一个属性，如果为空字符串
     * 就从 getter 方法名中解析，getter 方法名可以有前缀 get/provide；
     * <p>
     * 截取掉前缀后执行{@link java.beans.Introspector#decapitalize(String)}
     * 的结果就是指定的字段名，最终如果不存在指定字段就忽略掉
     * <p>
     * 也可以直接设置字段，此时方法名可以为任意名称
     *
     * @return 指定目标类中的某一字段
     */
    String value() default "";

    /**
     * 为指定类的指定属性提供值，{@code void}没有特殊指向，适用于所有目标类
     *
     * @return 目标类
     */
    Class<?> provideFor() default void.class;
}
