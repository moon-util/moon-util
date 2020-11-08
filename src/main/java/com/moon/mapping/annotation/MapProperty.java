package com.moon.mapping.annotation;

import java.lang.annotation.*;

/**
 * @author moonsky
 */
@Repeatable(MapProperty.List.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
@interface MapProperty {

    /**
     * 指定映射为{@link #target()}类里的{@link #value()}字段
     *
     * @return 目标类里的字段名
     */
    String value() default "";

    /**
     * 是否忽略这个字段的映射
     *
     * @return 当返回值为 true 时，会忽略这个字段到目标的映射
     */
    boolean ignore() default false;

    /**
     * 映射目标类，如果{@link MappingFor#value()}指定了多个类，这里
     * 可以为每个不同的类指定不同属性
     * <p>
     * 当指定的类为：byte、short、int、long、float、double、char、boolean 和对应包装类，void、Void、Object 时，
     * 且{@link #value()}上指定了值，则代表默认的目标字段
     *
     * @return 目标类，当返回值为
     */
    Class<?> target() default void.class;

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    @interface List {

        MapProperty[] value() default {};
    }
}
