package com.moon.poi.excel.annotation.style;

import org.apache.poi.ss.usermodel.Sheet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用表格样式(用在单元格上)
 *
 * @author moonsky
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HeadStyle {

    /**
     * 使用样式
     *
     * @return 定义的样式
     *
     * @see DefinitionStyle#classname() 引用一个定义的样式名
     */
    String value() default "";

    /**
     * 应用表头行位置，注意这里指的是以“这个表格”起始位置为第 0 行
     * 而不是{@link Sheet}的第 0 行，实际的{@code Sheet}前面可能已经填充有数据
     * <p>
     * 1. 正数，为对应表头行设置样式（大于最大表头行数的值忽略）
     * 2. 负数，优先按大小、声明顺序排序后填充进没有指明位置的表头行
     *
     * @return
     */
    int rowIndex() default -1;
}
