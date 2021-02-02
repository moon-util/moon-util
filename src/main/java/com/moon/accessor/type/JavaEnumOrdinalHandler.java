package com.moon.accessor.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class JavaEnumOrdinalHandler<T extends Enum<T>> implements TypeHandler<T> {

    private final T[] values;

    public JavaEnumOrdinalHandler(Class<T> enumClass) {
        assert enumClass.isEnum() : "Must be an enum.";
        values = enumClass.getEnumConstants();
    }

    @Override
    public void setParameter(PreparedStatement stmt, int index, T value, int jdbcType) throws SQLException {
        stmt.setInt(index, value.ordinal());
    }

    @Override
    public T getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
        int ordinal = resultSet.getInt(columnIndex);
        return ordinal == 0 && resultSet.wasNull() ? null : values[ordinal];
    }

    @Override
    public T getResultValue(ResultSet resultSet, String columnName) throws SQLException {
        int ordinal = resultSet.getInt(columnName);
        return ordinal == 0 && resultSet.wasNull() ? null : values[ordinal];
    }
}
