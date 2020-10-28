package com.moon.core.util;

import java.lang.annotation.*;

/**
 * @author moonsky
 */
@Repeatable(MapField.List.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@interface MapField {

    // String from

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {

        MapField[] value() default {};
    }
}
