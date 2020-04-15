package com.moon.more.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义序号列，将{@link DataIndexer}注解在字段上，
 * <p>
 * 列标题合并规则与{@link DataColumn}一样
 *
 * <h3>一、如果该字段同时注解了{@link DataColumn}</h3>
 * 1. 就会在在列前插入序号列，可以插入多列序号
 * <p>
 * 2. {@link #ending()}为{@code true}，序号列在后面插入
 *
 * <h3>二、如果该字段没有注解{@link DataColumn}</h3>
 * 1. 在第一列插入；
 * 2. {@link #ending()}为{@code true}，序号列在最后一列
 *
 * <h3>二、注解{@link DataColumnFlatten}</h3>
 * 参考以上。
 *
 * @author benshaoye
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataIndexer {

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
     * 是否渲染到标记列后面
     * <p>
     * 默认渲染在标记列前面
     *
     * @return true|false
     */
    boolean ending() default false;
}
