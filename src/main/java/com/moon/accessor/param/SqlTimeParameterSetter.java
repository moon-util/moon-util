package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author benshaoye
 */
public class SqlTimeParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final Time value;

    public SqlTimeParameterSetter(int parameterIndex, Date value) {
        this(parameterIndex, Types.TIME, value);
    }

    public SqlTimeParameterSetter(int parameterIndex, LocalTime value) {
        this(parameterIndex, Types.TIME, value);
    }

    public SqlTimeParameterSetter(int parameterIndex, int sqlType, Date value) {
        super(parameterIndex, sqlType);
        this.value = new Time(value.getTime());
    }

    public SqlTimeParameterSetter(int parameterIndex, int sqlType, LocalTime value) {
        super(parameterIndex, sqlType);
        this.value = Time.valueOf(value);
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setTime(getParameterIndex(), value);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
