package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class DoubleParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final double value;

    public DoubleParameterSetter(int parameterIndex, double value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setDouble(getParameterIndex(), value);
    }
}
