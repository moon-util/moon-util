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
public @interface RandomMax {

    /**
     * 范围最大值
     * <p>
     * String、StringBuilder、StringBuffer：字符串最大长度
     * <p>
     * Number：数字最大值
     * <p>
     * int、short、long、byte 会自动进行类型强转
     *
     * @return 最大值
     */
    double value() default 0;
}
