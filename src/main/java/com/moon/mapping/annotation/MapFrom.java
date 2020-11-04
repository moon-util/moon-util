package com.moon.mapping.annotation;

import java.lang.annotation.*;

/**
 * 注解在 setter 方法上
 *
 * @author moonsky
 */
@Repeatable(MapFrom.List.class)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface MapFrom {

    String value();

    Class<?> target() default void.class;

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    @interface List {

        MapFrom[] value() default {};
    }
}
