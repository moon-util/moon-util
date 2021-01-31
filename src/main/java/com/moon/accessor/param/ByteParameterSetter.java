package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class ByteParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final byte value;

    public ByteParameterSetter(int parameterIndex, byte value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setByte(getParameterIndex(), value);
    }
}
