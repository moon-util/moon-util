package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class ParamFloatSetter extends BaseParamSetter implements ParamSetter {

    private final float value;

    public ParamFloatSetter(int parameterIndex, float value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setFloat(getParameterIndex(), value);
    }
}
