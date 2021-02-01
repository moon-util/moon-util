package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author benshaoye
 */
public class SqlDateParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final java.sql.Date value;

    public SqlDateParameterSetter(int parameterIndex, Date value) {
        this(parameterIndex, Types.DATE, value);
    }

    public SqlDateParameterSetter(int parameterIndex, LocalDate value) {
        this(parameterIndex, Types.DATE, value);
    }

    public SqlDateParameterSetter(int parameterIndex, int sqlType, Date value) {
        super(parameterIndex, sqlType);
        this.value = new java.sql.Date(value.getTime());
    }

    public SqlDateParameterSetter(int parameterIndex, int sqlType, LocalDate value) {
        super(parameterIndex, sqlType);
        this.value = java.sql.Date.valueOf(value);
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setDate(getParameterIndex(), value);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
