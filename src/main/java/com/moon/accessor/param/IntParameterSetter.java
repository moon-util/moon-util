package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public class IntParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final int value;

    public IntParameterSetter(int parameterIndex, int value) {
        this(parameterIndex, Types.INTEGER, value);
    }

    public IntParameterSetter(int parameterIndex, int sqlType, int value) {
        super(parameterIndex, sqlType);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setInt(getParameterIndex(), value);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
