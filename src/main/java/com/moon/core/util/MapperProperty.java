package com.moon.core.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author moonsky
 */
@Deprecated
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperProperty {

    boolean serialize() default true;

    boolean deserialize() default true;

    String propertyName() default "";

    String pattern() default "";
}
