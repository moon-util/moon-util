package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class LongParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final long value;

    public LongParameterSetter(int parameterIndex, long value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setLong(getParameterIndex(), value);
    }
}
