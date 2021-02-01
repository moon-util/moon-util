package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;

/**
 * @author benshaoye
 */
public class InstantParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final Instant value;

    public InstantParameterSetter(int parameterIndex, Instant value) {
        this(parameterIndex, Types.TIMESTAMP, value);
    }

    public InstantParameterSetter(int parameterIndex, int sqlType, Instant value) {
        super(parameterIndex, sqlType);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setTimestamp(getParameterIndex(), Timestamp.from(value));
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
