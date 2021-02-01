package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public class StringParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final String value;

    public StringParameterSetter(int parameterIndex, CharSequence value) {
        this(parameterIndex, Types.VARCHAR, value);
    }

    public StringParameterSetter(int parameterIndex, int sqlType, CharSequence value) {
        super(parameterIndex, sqlType);
        this.value = value.toString();
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setString(getParameterIndex(), value);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
