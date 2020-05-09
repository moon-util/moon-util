package com.moon.more.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义序号列，将{@link TableIndexer}注解在字段上，
 * <p>
 * 列标题合并规则与{@link TableColumn}一样
 *
 * <h3>一、如果该字段同时注解了{@link TableColumn}</h3>
 * 1. 就会在在列前插入序号列，可以插入多列序号
 * <p>
 *
 * <h3>二、如果该字段没有注解{@link TableColumn}</h3>
 * 1. 在第一列插入；
 *
 * <h3>二、注解{@link TableColumnFlatten}</h3>
 * 参考以上。
 *
 * @author benshaoye
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableIndexer {

    /**
     * 序号列标题，始终为所依附列同级
     * <p>
     * 默认字段名首字母大写，如：name -&gt; Name；age -&gt; Age
     *
     * @return 列标题
     */
    String value() default "#";

    /**
     * 开始序号
     *
     * @return 开始序号
     */
    int startingAt() default 1;

    /**
     * 步长
     *
     * @return 步长
     */
    int step() default 1;
}
