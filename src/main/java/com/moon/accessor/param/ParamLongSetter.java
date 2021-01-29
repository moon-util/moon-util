package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class ParamLongSetter extends BaseParamSetter implements ParamSetter {

    private final long value;

    public ParamLongSetter(int parameterIndex, long value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setLong(getParameterIndex(), value);
    }
}
