package com.moon.data.annotation;

import com.moon.data.RecordConst;
import com.moon.spring.data.jpa.start.EnableJpaRecordCaching;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 【不建议使用，视情况将删除】
 * <p>
 * 实体缓存命名空间，主要用于项目存在同名不同包的实体类时
 *
 * @author moonsky
 * @see EnableJpaRecordCaching 这个注解用于开启 Record 缓存，未开启缓存而注解{@code RecordCacheNamespace}会无效
 */
@SuppressWarnings("all")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordCacheNamespace {

    /**
     * namespace 只有在不为空时有效
     *
     * @return namespace
     */
    String value() default "";

    /**
     * 局部所属分组（仅作用于被注解的实体类）
     *
     * @return 分组名
     *
     * @see EnableJpaRecordCaching#group() 全局所属分组
     */
    String group() default RecordConst.CACHE_GROUP;
}
