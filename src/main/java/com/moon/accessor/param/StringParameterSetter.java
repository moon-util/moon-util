package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class StringParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final String value;

    public StringParameterSetter(int parameterIndex, String value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setString(getParameterIndex(), value);
    }
}
