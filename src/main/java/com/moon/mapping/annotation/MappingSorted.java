package com.moon.mapping.annotation;

import com.moon.mapping.ObjectMapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 主要用于设置{@link ObjectMapping#toString(Object)}返回值的字段顺序，针对其他字段无意义
 *
 * @author moonsky
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@interface MappingSorted {

    String[] value() default {};
}
