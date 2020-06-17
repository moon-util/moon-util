package com.moon.more.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义列偏移
 *
 * @author benshaoye
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableColumnOffset {

    /**
     * 偏移
     * <p>
     * 多级表头偏移通过{@link #headerRows()}设置
     *
     * @return 偏移量
     */
    int value() default 0;

    /**
     * 表头偏移行数
     * <p>
     * 如：1 代表只偏移最后一级；2 只偏移后两级,...以此类推
     * 任何大于等于表头行数的值代表通列偏移(默认)
     *
     * @return true: 只偏移最后一级; false: 通列偏移
     */
    int headerRows() default Integer.MAX_VALUE;

    /**
     * 用空白是否用空白单元格填充偏移的单元格（设置行样式的时候不同情况会有影响）
     *
     * @return true: 用空白单元格填充; false: 直接跳过
     */
    boolean fillSkipped() default false;
}
