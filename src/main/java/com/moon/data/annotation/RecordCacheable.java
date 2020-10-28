package com.moon.data.annotation;

import com.moon.spring.data.jpa.start.EnableJpaRecordCaching;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解于实体类
 * <p>
 * 实体缓存命名空间，主要用于项目存在同名不同包的实体类时
 * <p>
 * "group:name" 等于 cache name，
 * group 为空时没有冒号（":"），
 * name 为空时自动取为类名。
 *
 * @author moonsky
 * @see EnableJpaRecordCaching 这个注解用于开启 Record 缓存，未开启缓存而注解{@code RecordCacheable}会无效
 */
@SuppressWarnings("all")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordCacheable {

    /**
     * 是否开启 record 缓存
     *
     * @return 返回 true 时有效，否则不缓存当前实体
     */
    boolean value() default true;

    /**
     * namespace 只有在不为空时有效
     * <p>
     * 一般用于存在
     *
     * @return namespace
     */
    String name() default "";
}
