package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public class LongParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final long value;

    public LongParameterSetter(int parameterIndex, long value) {
        this(parameterIndex, Types.BIGINT, value);
    }

    public LongParameterSetter(int parameterIndex, int sqlType, long value) {
        super(parameterIndex, sqlType);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setLong(getParameterIndex(), value);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
