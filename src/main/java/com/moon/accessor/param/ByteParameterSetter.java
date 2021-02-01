package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public class ByteParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final byte value;

    public ByteParameterSetter(int parameterIndex, byte value) {
        this(parameterIndex, Types.TINYINT, value);
    }

    public ByteParameterSetter(int parameterIndex, int sqlType, byte value) {
        super(parameterIndex, sqlType);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setByte(getParameterIndex(), value);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
