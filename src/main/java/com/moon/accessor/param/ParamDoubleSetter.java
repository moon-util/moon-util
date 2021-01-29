package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class ParamDoubleSetter extends BaseParamSetter implements ParamSetter {

    private final double value;

    public ParamDoubleSetter(int parameterIndex, double value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setDouble(getParameterIndex(), value);
    }
}
