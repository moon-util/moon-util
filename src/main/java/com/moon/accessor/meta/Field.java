package com.moon.accessor.meta;

/**
 * @author benshaoye
 */
public interface Field<T, R, TB extends Table<R>> {

    /**
     * 领域模型类
     *
     * @return 与数据表对应的实体类
     */
    Class<R> getDomainClass();

    /**
     * 返回字段数据类型
     *
     * @return 字段 java 类型
     */
    Class<T> getPropertyType();

    /**
     * 实体字段名
     *
     * @return 实体字段名
     */
    String getPropertyName();

    /**
     * 获取数据库对应的列名
     *
     * @return 列名
     */
    String getColumnName();

    /**
     * 返回所属数据表
     *
     * @return 数据表描述
     */
    TB getTable();
}
