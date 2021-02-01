package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author benshaoye
 */
public class SqlTimestampParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final Timestamp value;

    public SqlTimestampParameterSetter(int parameterIndex, long value) {
        this(parameterIndex, Types.TIMESTAMP, value);
    }

    public SqlTimestampParameterSetter(int parameterIndex, Date value) {
        this(parameterIndex, Types.TIMESTAMP, value);
    }

    public SqlTimestampParameterSetter(int parameterIndex, LocalDateTime value) {
        this(parameterIndex, Types.TIMESTAMP, value);
    }

    public SqlTimestampParameterSetter(int parameterIndex, int sqlType, long value) {
        super(parameterIndex, sqlType);
        this.value = new Timestamp(value);
    }

    public SqlTimestampParameterSetter(int parameterIndex, int sqlType, Date value) {
        this(parameterIndex, sqlType, value.getTime());
    }

    public SqlTimestampParameterSetter(int parameterIndex, int sqlType, LocalDateTime value) {
        super(parameterIndex, sqlType);
        this.value = Timestamp.valueOf(value);
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setTimestamp(getParameterIndex(), value);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
