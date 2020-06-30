package com.moon.core.json;

import com.moon.core.util.TypeUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author moonsky
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONParse {
    String name() default "";

    Class converter() default TypeUtil.class;
}
