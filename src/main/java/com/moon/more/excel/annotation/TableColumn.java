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
public @interface TableColumn {

    /**
     * 列标题，可设置合并标题
     * <p>
     * 默认字段名首字母大写，如：name -&gt; Name；age -&gt; Age
     *
     * @return 列标题
     */
    String[] value() default {};

    /**
     * 排序顺序
     *
     * @return 顺序号
     */
    int order() default 0;

    /**
     * 偏移
     *
     * @return 偏移量
     */
    int offset() default 0;

    /**
     * 表头是否“通列偏移”，而不只是最后一级偏移
     *
     * @return true: 通列偏移; false: 只偏移最后一级
     */
    boolean offsetOnFull() default false;
}
