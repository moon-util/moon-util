package com.moon.random.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解于日期字段
 * <p>
 * 限 JDK 和 joda、moon 支持的日期、String、long 等字段数据类型
 *
 * @author moonsky
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RandomAfter {

    /**
     * 日期最小值
     *
     * @return 日期最小值
     */
    String value() default "";
}
