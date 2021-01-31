package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class BooleanParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final boolean value;

    public BooleanParameterSetter(int parameterIndex, boolean value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setBoolean(getParameterIndex(), value);
    }
}
