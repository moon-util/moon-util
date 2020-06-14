package com.moon.more.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableIndexer {

    /**
     * 序号标题；
     * <p>
     * 序号列依附于普通列存在，表示插入到普通列前面
     *
     * @return 标题名称
     */
    String value() default "#";

    /**
     * 索引开始序号
     *
     * @return 序号值
     */
    int startingAt() default 1;

    /**
     * 每次索引增长数量
     *
     * @return 数值
     */
    int step() default 1;
}
