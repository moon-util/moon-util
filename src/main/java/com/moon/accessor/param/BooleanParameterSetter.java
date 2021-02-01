package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public class BooleanParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final boolean value;

    public BooleanParameterSetter(int parameterIndex, boolean value) {
        this(parameterIndex, Types.BOOLEAN, value);
    }

    public BooleanParameterSetter(int parameterIndex, int sqlType, boolean value) {
        super(parameterIndex, sqlType);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setBoolean(getParameterIndex(), value);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
