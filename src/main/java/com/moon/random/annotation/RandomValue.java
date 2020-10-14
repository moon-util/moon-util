package com.moon.random.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 被注解的字段自动填充随机值且不限制任何范围
 *
 * @author moonsky
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RandomValue {

    /**
     * 随机值：
     * <p>
     * String、StringBuilder、StringBuffer：指定长度随机值
     * <p>
     * Number 字段：就是指定值，没有随机值，但不同数据类型有强转
     * <p>
     * int、short、long、byte 会自动进行类型强转
     *
     * @return
     */
    double value() default -1;
}
