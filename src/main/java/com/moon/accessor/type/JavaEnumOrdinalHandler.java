package com.moon.accessor.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public class JavaEnumOrdinalHandler<T extends Enum<T>> implements TypeJdbcHandler<T> {

    private final T[] values;
    private final Class<T> enumClass;

    public JavaEnumOrdinalHandler(Class<T> enumClass) {
        if (!enumClass.isEnum()) {
            throw new IllegalStateException("Must be an enum: " + enumClass);
        }
        values = enumClass.getEnumConstants();
        this.enumClass = enumClass;
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

    @Override
    public Class<T> supportClass() { return enumClass; }

    @Override
    public int[] supportJdbcTypes() { return TypeUsing2.asInts(Types.INTEGER); }
}
