package com.moon.more.excel.table;

import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnGroup;

import java.lang.annotation.Annotation;

/**
 * 字段描述器
 *
 * @author benshaoye
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
