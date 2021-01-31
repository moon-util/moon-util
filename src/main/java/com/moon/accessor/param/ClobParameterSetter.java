package com.moon.accessor.param;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class ClobParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final Clob value;

    public ClobParameterSetter(int parameterIndex, Clob value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setClob(getParameterIndex(), value);
    }
}
