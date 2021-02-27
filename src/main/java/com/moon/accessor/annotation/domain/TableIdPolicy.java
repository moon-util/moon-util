package com.moon.accessor.annotation.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableIdPolicy {

    Class<?> generateBy();

    IdStrategy useStrategy() default IdStrategy.ALWAYS;

    enum IdStrategy {
        /**
         * 总是使用主键生器生成的主键
         */
        ALWAYS,
        /**
         * 指定优先
         * <p>
         * 先检查是否存在主键值（字符串用 isEmpty 判断，其他类型判断是否是 null）
         * <p>
         * 如果存在就使用已存在的，否则就用主键生成器
         */
        ASSIGNED_FIRST
    }
}
