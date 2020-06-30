package com.moon.more.excel.table;

import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnGroup;

import java.lang.annotation.Annotation;
import java.util.function.Function;

/**
 * 字段描述器
 *
 * @author moonsky
 */
interface Descriptor {

    /**
     * 属性名
     *
     * @return 字段名
     */
    String getName();

    /**
     * 获取列标题数组
     *
     * @return 标题
     */
    String[] getTitles();

    /**
     * 获取声明的表头行高
     *
     * @return 行高数组
     */
    short[] getHeadHeightArr();

    /**
     * 获取列宽
     *
     * @return 列宽
     */
    Integer getColumnWidth();

    /**
     * 字段数据类型
     *
     * @return 字段类
     */
    Class getPropertyType();

    /**
     * 获取注解
     *
     * @param annotationType 注解类
     * @param <T>            注解类型
     *
     * @return 注解实例或 null
     */
    <T extends Annotation> T getAnnotation(Class<T> annotationType);

    /**
     * 获取字段注解
     *
     * @return TableColumn
     */
    TableColumn getTableColumn();

    /**
     * 实体字段注解
     *
     * @return TableColumnGroup
     */
    TableColumnGroup getTableColumnGroup();

    /**
     * 读取值，不存在的话返回默认值
     *
     * @param getter0 TableColumn getter
     * @param getter1 TableColumnGroup getter
     * @param dft     默认值
     * @param <T>     返回值类型
     *
     * @return 获取的值
     */
    default <T> T getOrDefault(
        Function<TableColumn, T> getter0, Function<TableColumnGroup, T> getter1, T dft
    ) {
        TableColumn column = getTableColumn();
        if (column != null) {
            return getter0.apply(column);
        }
        TableColumnGroup group = getTableColumnGroup();
        if (group != null) {
            return getter1.apply(group);
        }
        return dft;
    }

    /**
     * 偏移量
     *
     * @return 偏移的值
     */
    default int getOffsetVal() {
        return getOrDefault(col -> col.offset(), grp -> grp.offset(), 0);
    }

    /**
     * 表头行偏移参与偏移的行数
     * <p>
     * 任何大于等于表头行数的值代表所有行都便宜；
     * <p>
     * 小于表头行数的值代表倒数 n 行参与偏移
     *
     * @return
     */
    default int getOffsetHeadRows() {
        return getOrDefault(col -> col.offsetHeadRows(),

            grp -> grp.offsetHeadRows(), Integer.MAX_VALUE);
    }

    /**
     * 是否填充
     *
     * @return 偏移单元格
     */
    default boolean getOffsetFillSkipped() { return true; }

    /**
     * 是否是组合列
     *
     * @return true | false
     */
    default boolean isAnnotatedGroup() { return getTableColumnGroup() != null; }

    /**
     * 是否是数据列
     *
     * @return true | false
     */
    default boolean isAnnotatedColumn() { return getTableColumn() != null; }

    /**
     * 是否是标记列
     *
     * @return true | false
     */
    default boolean isAnnotated() { return isAnnotatedGroup() || isAnnotatedColumn(); }
}
