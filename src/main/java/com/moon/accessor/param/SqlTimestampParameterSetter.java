package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author benshaoye
 */
public class SqlTimestampParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final Timestamp value;

    public SqlTimestampParameterSetter(int parameterIndex, long value) {
        super(parameterIndex);
        this.value = new Timestamp(value);
    }

    public SqlTimestampParameterSetter(int parameterIndex, Date value) {
        this(parameterIndex, value.getTime());
    }

    public SqlTimestampParameterSetter(int parameterIndex, LocalDateTime value) {
        super(parameterIndex);
        this.value = Timestamp.valueOf(value);
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setTimestamp(getParameterIndex(), value);
    }
}
