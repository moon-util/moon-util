package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class IntParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final int value;

    public IntParameterSetter(int parameterIndex, int value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setInt(getParameterIndex(), value);
    }
}
