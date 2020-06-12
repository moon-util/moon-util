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
     * 默认值
     *
     * @return 默认值
     */
    String defaultValue() default "";

    /**
     * 什么情况下设置默认值{@link #defaultValue()}
     * <p>
     * 针对字符串还有{@link DefaultStrategy#EMPTY}和{@link DefaultStrategy#BLANK}
     * 针对数字还有{@link DefaultStrategy#ZERO}
     * <p>
     * 基本数据类型不可能为 null，所以用在基本数据类型上时需要注意这点
     *
     * @return 当字段值为 null 时
     */
    DefaultStrategy defaultFor() default DefaultStrategy.NULL;
}
