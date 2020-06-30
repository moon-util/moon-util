package com.moon.core.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author moonsky
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONStringify {
    String name() default "";

    int order() default 0;

    boolean ignore() default false;

    String format() default "";
}
