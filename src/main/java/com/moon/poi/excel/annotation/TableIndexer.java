package com.moon.poi.excel.annotation;

import com.moon.core.lang.Unsupported;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author moonsky
 */
@Unsupported
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableIndexer {

    /**
     * 序号标题；
     * <p>
     * 序号列依附于普通列存在，表示插入到普通列前面
     * <p>
     * 索引列的标题名称与所在列最末级标题同级，并自动继承所在列的"高级"标题
     *
     * @return 标题名称
     */
    String value() default "#";

    /**
     * 索引开始序号
     *
     * @return 序号值
     */
    int startFrom() default 1;

    /**
     * 增量
     *
     * @return 数值
     */
    int step() default 1;

    /**
     * 默认在所在列前面，当{@link #ending()}为{@code true}时，放在所在列后面
     *
     * @return true | false
     */
    boolean ending() default false;

    /**
     * 集合字段是否全局计数
     *
     * @return true: 全局计数
     */
    boolean joinGlobal() default false;
}
