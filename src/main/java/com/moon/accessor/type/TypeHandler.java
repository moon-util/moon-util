package com.moon.accessor.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public interface TypeHandler<T> {

    /**
     * 设置当前参数值
     *
     * @param stmt     SQL 预编译语句
     * @param index    参数索引
     * @param value    参数值
     * @param jdbcType 字段类型{@link Types}
     *
     * @throws SQLException SQLException
     */
    void setParameter(PreparedStatement stmt, int index, T value, int jdbcType) throws SQLException;

    /**
     * 设置当前参数为 null 值
     *
     * @param stmt     SQL 预编译语句
     * @param index    参数索引
     * @param jdbcType 字段类型{@link Types}
     *
     * @throws SQLException SQLException
     */
    default void setNull(PreparedStatement stmt, int index, int jdbcType) throws SQLException {
        stmt.setNull(index, jdbcType);
    }

    /**
     * 获取字段值
     *
     * @param resultSet   SQL 结果集
     * @param columnIndex 列索引
     *
     * @return 字段值
     *
     * @throws SQLException SQLException
     */
    T getResultValue(ResultSet resultSet, int columnIndex) throws SQLException;

    /**
     * 获取字段值
     *
     * @param resultSet  SQL 结果集
     * @param columnName 列名
     *
     * @return 字段值
     *
     * @throws SQLException SQLException
     */
    T getResultValue(ResultSet resultSet, String columnName) throws SQLException;
}
