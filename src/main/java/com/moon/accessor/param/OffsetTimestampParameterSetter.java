package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;

/**
 * @author benshaoye
 */
public class OffsetTimestampParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final OffsetDateTime value;

    public OffsetTimestampParameterSetter(int parameterIndex, OffsetDateTime value) {
        this(parameterIndex, Types.TIMESTAMP_WITH_TIMEZONE, value);
    }

    public OffsetTimestampParameterSetter(int parameterIndex, int sqlType, OffsetDateTime value) {
        super(parameterIndex, sqlType);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setObject(getParameterIndex(), value, Types.TIMESTAMP_WITH_TIMEZONE);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
