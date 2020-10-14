package com.moon.random.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author moonsky
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RandomMin {

    /**
     * 范围最小值
     * <p>
     * String、StringBuilder、StringBuffer：字符串最大长度
     * <p>
     * Number：数字最大值
     * <p>
     * int、short、long、byte 会自动进行类型强转
     *
     * @return 最小值
     */
    double value() default 0;
}
