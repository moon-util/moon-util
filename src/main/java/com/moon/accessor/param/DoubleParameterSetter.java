package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public class DoubleParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final double value;

    public DoubleParameterSetter(int parameterIndex, double value) {
        this(parameterIndex, Types.DOUBLE, value);
    }

    public DoubleParameterSetter(int parameterIndex, int sqlType, double value) {
        super(parameterIndex, sqlType);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setDouble(getParameterIndex(), value);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
