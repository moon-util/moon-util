package com.moon.more.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 扁平化集合数据，如果数据中有字段是集合或数组
 * <p>
 * 对字段注解{@link TableColumnFlatten}，会对数据进行合并行处理
 * <p>
 * 同时，这个字段的数据的对应多列表头在其实体中再次使用{@link TableColumn}声明
 *
 * @author benshaoye
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableColumnFlatten {

    /**
     * 列标题，可设置合并标题
     * <p>
     * 默认字段名首字母大写，如：name -&gt; Name；age -&gt; Age
     *
     * @return 列标题
     */
    String[] value() default {};

    /**
     * 当前列索引
     *
     * @return 索引号
     */
    int order() default -1;

    /**
     * 目标类
     *
     * @return 目标类
     */
    Class<?> targetClass() default Void.class;

    /**
     * 未指定目标类
     */
    Class UNSPECIFIED = Void.class;
}
