package com.moon.more.excel.annotation.style;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用表格样式(用在单元格上)
 *
 * @author benshaoye
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UseStyleOnCell {

    /**
     * 使用样式
     *
     * @return 以定义的样式
     *
     * @see DefinitionStyle#classname() 引用一个定义的样式名
     */
    String value() default "";

    /**
     * 条件样式
     *
     * @return
     */
    Class conditional() default Void.class;
}
