package com.moon.mapping.annotation;

import java.lang.annotation.*;

/**
 * @author benshaoye
 */
@Repeatable(MappingField.List.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MappingField {

    /**
     * 来自于哪个类
     *
     * @return 默认是使用时传入的类，同一个字段注解的{@code MappingField}
     * 的两个字段{@code fromType}和{@code fromField}所指向的字段必须唯一
     */
    Class fromType() default Void.class;

    /**
     * 来自于哪个类的哪个字段
     *
     * @return 默认是同名字段
     */
    String fromField() default "";

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {

        MappingField[] value() default {};
    }
}
