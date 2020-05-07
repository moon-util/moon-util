package com.moon.more.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据格式化：
 * <p>
 * 可注解于数字字段或日期字段，{@link #value()}即为对应格式
 *
 * @author benshaoye
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableFieldFormat {

    String value() default "yyyy-MM-dd";
}
