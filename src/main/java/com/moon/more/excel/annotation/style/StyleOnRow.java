package com.moon.more.excel.annotation.style;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用表格样式(用在整行的单元格上)
 *
 * @author moonsky
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StyleOnRow {

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

    /**
     * 行应用样式方式
     *
     * @return
     */
    Type type() default Type.CELL;

    enum Type {
        /**
         * 只应用在受管理的单元格
         *
         * @see Cell#setCellStyle(CellStyle)
         */
        CELL,
        /**
         * 只应用在当前行
         *
         * @see Row#setRowStyle(CellStyle)
         */
        ROW,
        /**
         * 受管理的单元格 + 当前行
         */
        ALL
    }
}
