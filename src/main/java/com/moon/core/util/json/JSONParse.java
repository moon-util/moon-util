package com.moon.core.util.json;

import com.moon.core.util.TypeUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONParse {
    String name() default "";

    Class converter() default TypeUtil.class;
}
