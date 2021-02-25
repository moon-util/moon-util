package com.moon.poi.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author moonsky
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SheetColumnGroup {

    /**
     * 组合列目标类
     *
     * @return 目标类，指定了优先以指定的为准，不指定也会自动推测
     */
    Class<?> targetClass() default Void.class;
}
