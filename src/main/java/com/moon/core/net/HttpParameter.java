package com.moon.core.net;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解在字段或者 getter 方法上，注解在 setter 或其他方法上会被忽略
 * @author moonsky
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface HttpParameter {}
