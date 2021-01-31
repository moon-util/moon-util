package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.Date;

/**
 * @author benshaoye
 */
public class OffsetTimeParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final OffsetTime value;

    public OffsetTimeParameterSetter(int parameterIndex, OffsetTime value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setObject(getParameterIndex(), value, Types.TIME_WITH_TIMEZONE);
    }
}
