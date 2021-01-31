package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

/**
 * @author benshaoye
 */
public class SqlDateParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final java.sql.Date value;

    public SqlDateParameterSetter(int parameterIndex, Date value) {
        super(parameterIndex);
        this.value = new java.sql.Date(value.getTime());
    }

    public SqlDateParameterSetter(int parameterIndex, LocalDate value) {
        super(parameterIndex);
        this.value = java.sql.Date.valueOf(value);
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setDate(getParameterIndex(), value);
    }
}
