package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public class ShortParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final short value;

    public ShortParameterSetter(int parameterIndex, short value) {
        this(parameterIndex, Types.SMALLINT, value);
    }

    public ShortParameterSetter(int parameterIndex, int sqlType, short value) {
        super(parameterIndex, sqlType);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setShort(getParameterIndex(), value);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
