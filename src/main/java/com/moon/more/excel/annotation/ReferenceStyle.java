package com.moon.more.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReferenceStyle {

    /**
     * 使用样式
     *
     * @return 以定义的样式
     *
     * @see DefinitionStyle#classname() 引用一个定义好的样式
     */
    String value() default "";
}
