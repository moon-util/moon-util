package com.moon.accessor.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public class JavaEnumStringHandler<T extends Enum<T>> implements TypeJdbcHandler<T> {

    private final Class<T> enumClass;

    public JavaEnumStringHandler(Class<T> enumClass) { this.enumClass = enumClass; }

    @Override
    public void setParameter(PreparedStatement stmt, int index, T value, int jdbcType) throws SQLException {
        stmt.setString(index, value.name());
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    @Override
    public T getResultValue(ResultSet resultSet, int columnIndex) throws SQLException {
        String value = resultSet.getString(columnIndex);
        return isEmpty(value) ? null : Enum.valueOf(enumClass, value);
    }

    @Override
    public T getResultValue(ResultSet resultSet, String columnName) throws SQLException {
        String value = resultSet.getString(columnName);
        return isEmpty(value) ? null : Enum.valueOf(enumClass, value);
    }

    @Override
    public Class<T> supportClass() { return enumClass; }

    @Override
    public int[] supportJdbcTypes() { return TypeUsing2.asInts(Types.VARCHAR); }
}
