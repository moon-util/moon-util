package com.moon.accessor.param;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.chrono.JapaneseDate;

/**
 * @author benshaoye
 */
public class JapaneseDateParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final Date value;

    public JapaneseDateParameterSetter(int parameterIndex, JapaneseDate value) {
        super(parameterIndex);
        this.value = Date.valueOf(LocalDate.ofEpochDay(value.toEpochDay()));
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setDate(getParameterIndex(), value);
    }
}
