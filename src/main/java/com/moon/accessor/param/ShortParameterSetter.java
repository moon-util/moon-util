package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class ShortParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final short value;

    public ShortParameterSetter(int parameterIndex, short value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setShort(getParameterIndex(), value);
    }
}
