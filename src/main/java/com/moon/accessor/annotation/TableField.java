package com.moon.accessor.annotation;

import com.moon.accessor.type.JdbcType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableField {

    /**
     * 指定列名，指定的列名会覆盖公共列名策略
     *
     * @return 列名
     */
    String name() default "";

    JdbcType jdbcType() default JdbcType.AUTO;
}
