package com.moon.core.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Deprecated
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperProperty {
    Class fromType();

    String propertyName();

    boolean useToReverse() default false;

    String pattern() default "";
}
