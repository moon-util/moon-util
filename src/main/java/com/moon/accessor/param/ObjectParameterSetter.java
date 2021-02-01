package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public class ObjectParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final Object value;

    public ObjectParameterSetter(int parameterIndex, Object value) {
        this(parameterIndex, Types.JAVA_OBJECT, value);
    }

    public ObjectParameterSetter(int parameterIndex, int sqlType, Object value) {
        super(parameterIndex, sqlType);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setObject(getParameterIndex(), value);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
