package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class ParamIntSetter extends BaseParamSetter implements ParamSetter {

    private final int value;

    public ParamIntSetter(int parameterIndex, int value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setInt(getParameterIndex(), value);
    }
}
