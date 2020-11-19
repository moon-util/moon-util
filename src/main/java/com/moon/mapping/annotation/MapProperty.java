package com.moon.mapping.annotation;

import java.lang.annotation.*;

/**
 * 映射属性
 *
 * @author moonsky
 */
@Repeatable(MapProperty.List.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface MapProperty {

    /**
     * 指定映射为{@link #target()}类里的字段
     *
     * @return 目标类里的字段名
     */
    String value() default "";

    /**
     * 映射目标类，如果{@link MappingFor#value()}指定了多个类，这里
     * 可以为每个不同的类指定不同属性
     * <p>
     * 当指定的类为：基本数据类型和对应包装类 Object 时，
     * 且{@link #value()}上指定了值，则代表默认目标类里的字段
     *
     * @return 目标类
     */
    Class<?> target() default void.class;

    /**
     * 针对数字、日期字段到{@code String}的格式化转换
     * <p>
     * 这里采用{@code isNotBlank}判断，所以只要没有非空内容，这个字段就无效，
     * <p>
     * 如果有实际内容，两端不会{@code trim()}掉空格
     *
     * @return 数字或日期格式
     */
    String format() default "";

    /**
     * 默认值，当映射过程中获取到{@code null}时，将设置为默认值
     * <p>
     * 注解在不同字段类型上时，有不同要求：
     * 1. 数字字段: 基本数据类型及包装类、{@code BigDecimal}、{@code BigInteger}）；
     * 2. 枚举: 数字或枚举项名称。如果是数字就取枚举项第N项(从 0 开始)，如果是名称就取对应的枚举项；
     * 3. boolean/Boolean: 只能为 true/false
     * 4. 字符串
     *
     * @return 字段默认值
     */
    String defaultValue() default "";

    /**
     * 是否忽略这个字段的映射
     *
     * @return 当返回值为 true 时，会忽略这个字段到目标的映射
     */
    IgnoreMode ignore() default IgnoreMode.NONE;

    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    @interface List {

        MapProperty[] value() default {};
    }
}
