package com.moon.more.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultValue {


    /**
     * 默认值
     *
     * @return 默认值
     */
    String value() default "";

    /**
     * 什么情况下设置默认值{@link #value()}
     * <pre>
     * 1. 针对字符串还有:
     * 1.1. null 或空字符串: {@link DefaultStrategy#EMPTY}
     * 1.2. null 、空字符串或空白字符串: {@link DefaultStrategy#BLANK}
     * 2. 针对数字还有:
     * 2.1. 值为 0: {@link DefaultStrategy#ZERO}；
     * 2.2. 负数: {@link DefaultStrategy#NEGATIVE}
     * 2.3. 正数: {@link DefaultStrategy#POSITIVE}
     * </pre>
     * 基本数据类型不可能为 null，所以用在基本数据类型上时需要注意这点
     *
     * @return 默认字段值设置策略
     */
    DefaultStrategy defaultFor() default DefaultStrategy.NULL;
}
