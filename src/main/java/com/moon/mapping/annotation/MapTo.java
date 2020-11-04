package com.moon.mapping.annotation;

import java.lang.annotation.*;

/**
 * 注解在 getter 方法上
 *
 * @author moonsky
 */
@Repeatable(MapTo.List.class)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface MapTo {

    String value();

    Class<?> target() default void.class;

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    @interface List {

        MapTo[] value() default {};
    }
}
