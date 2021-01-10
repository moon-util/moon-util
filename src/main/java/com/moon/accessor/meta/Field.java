package com.moon.accessor.meta;

import com.moon.accessor.Condition;

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

    Condition startsWith(Object value);

    Condition endsWith(Object value);

    Condition contains(Object value);

    Condition like(Object value);

    Condition lt(T value);

    Condition gt(T value);

    Condition le(T value);

    Condition ge(T value);

    Condition eq(T value);

    Condition ne(T value);

    Condition in(T... values);

    Condition notIn(T... values);

    Condition in(Iterable<T>... values);

    Condition notIn(Iterable<T>... values);

    Condition isNull();

    Condition notNull();

    default Condition isNotNull() {
        return notNull();
    }
}
