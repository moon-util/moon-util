package com.moon.data.annotation;

import com.moon.data.RecordConst;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author moonsky
 * @see com.moon.spring.data.jpa.start.EnableJpaRecordCache 这个注解用于开启 Record 缓存
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
     * 所属分组
     *
     * @return 分组名
     */
    String group() default RecordConst.CACHE_GROUP;
}
