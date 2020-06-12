package com.moon.more.excel.table;

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
     * 字段数据类型
     *
     * @return
     */
    Class getPropertyType();
}
