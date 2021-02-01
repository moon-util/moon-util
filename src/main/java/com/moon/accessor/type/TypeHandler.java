package com.moon.accessor.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public interface TypeHandler<T> {

    void setParameter(PreparedStatement stmt, int index, T value, int jdbcType) throws SQLException;

    default void setNull(PreparedStatement stmt, int index, int jdbcType) throws SQLException {
        stmt.setNull(index, jdbcType);
    }

    T getResultValue(ResultSet resultSet, int columnIndex) throws SQLException;

    T getResultValue(ResultSet resultSet, String columnName) throws SQLException;
}
