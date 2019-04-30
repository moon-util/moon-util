package com.moon.core.util.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONStringify {
    String name() default "";

    int order() default 0;

    boolean ignore() default false;

    String format() default "";
}
