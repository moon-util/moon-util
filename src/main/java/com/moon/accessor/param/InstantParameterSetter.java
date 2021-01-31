package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * @author benshaoye
 */
public class InstantParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final Instant value;

    public InstantParameterSetter(int parameterIndex, Instant value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setTimestamp(getParameterIndex(), Timestamp.from(value));
    }
}
