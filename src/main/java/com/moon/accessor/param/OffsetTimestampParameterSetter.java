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
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setObject(getParameterIndex(), value, Types.TIMESTAMP_WITH_TIMEZONE);
    }
}
