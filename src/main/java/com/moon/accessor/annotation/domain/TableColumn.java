package com.moon.accessor.annotation.domain;

import com.moon.accessor.type.JdbcType;
import com.moon.accessor.type.TypeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.JDBCType;
import java.sql.Types;

/**
 * @author benshaoye
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableColumn {

    /**
     * 指定列名，指定的列名会覆盖公共列名策略
     *
     * @return 列名
     */
    String name() default "";

    /**
     * 字段对应的 jdbc 类型，参考: {@link JDBCType}，{@link Types}
     *
     * @return AUTO 为自动推断，不会应用到实际执行过程中
     */
    JdbcType jdbcType() default JdbcType.AUTO;

    Class<? extends TypeHandler> typeHandler() default TypeHandler.class;

    /**
     * 字段长度
     *
     * @return 字段长度
     */
    int length() default -1;

    /**
     * 有些数字类型（浮点数）有精度
     *
     * @return 精度位数（小数位数），不同浮点数类型有默认值，如果没有主动设置有效值，将自动设置默认精度
     */
    int precision() default -1;

    /**
     * 是否忽略这个字段作为表字段
     *
     * @return 默认不忽略
     */
    boolean ignored() default false;
}
