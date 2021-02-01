package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetTime;

/**
 * @author benshaoye
 */
public class OffsetTimeParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final OffsetTime value;

    public OffsetTimeParameterSetter(int parameterIndex, OffsetTime value) {
        this(parameterIndex, Types.TIME_WITH_TIMEZONE, value);
    }

    public OffsetTimeParameterSetter(int parameterIndex, int sqlType, OffsetTime value) {
        super(parameterIndex, sqlType);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setObject(getParameterIndex(), value, Types.TIME_WITH_TIMEZONE);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
