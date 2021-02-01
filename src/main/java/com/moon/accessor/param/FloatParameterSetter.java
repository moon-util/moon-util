package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public class FloatParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final float value;

    public FloatParameterSetter(int parameterIndex, float value) {
        this(parameterIndex, Types.REAL, value);
    }

    public FloatParameterSetter(int parameterIndex, int sqlType, float value) {
        super(parameterIndex, sqlType);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setFloat(getParameterIndex(), value);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
