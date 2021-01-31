package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZonedDateTime;

/**
 * @author benshaoye
 */
public class ObjectParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final Object value;

    public ObjectParameterSetter(int parameterIndex, Object value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setObject(getParameterIndex(), value);
    }
}
