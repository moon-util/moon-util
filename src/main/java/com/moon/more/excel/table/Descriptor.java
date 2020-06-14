package com.moon.more.excel.table;

import com.moon.more.excel.annotation.TableColumn;

import java.lang.annotation.Annotation;

/**
 * @author benshaoye
 */
interface Descriptor {

    /**
     * 属性名
     *
     * @return
     */
    String getName();

    /**
     * 获取列标题数组
     *
     * @return
     */
    String[] getTitles();

    /**
     * 字段数据类型
     *
     * @return
     */
    Class getPropertyType();

    /**
     * 获取注解
     *
     * @param annotationType
     * @param <T>
     *
     * @return
     */
    <T extends Annotation> T getAnnotation(Class<T> annotationType);

    /**
     * 获取字段注解
     *
     * @return
     */
    TableColumn getTableColumn();
}
