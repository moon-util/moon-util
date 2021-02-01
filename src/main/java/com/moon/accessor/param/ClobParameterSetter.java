package com.moon.accessor.param;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public class ClobParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final Clob value;

    public ClobParameterSetter(int parameterIndex, Clob value) {
        this(parameterIndex, Types.CLOB, value);
    }

    public ClobParameterSetter(int parameterIndex, int sqlType, Clob value) {
        super(parameterIndex, sqlType);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setClob(getParameterIndex(), value);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
