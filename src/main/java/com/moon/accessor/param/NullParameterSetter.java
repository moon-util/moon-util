package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class NullParameterSetter extends BaseParameterSetter {

    private final int sqlType;

    public NullParameterSetter(int parameterIndex, int sqlType) {
        super(parameterIndex);
        this.sqlType = sqlType;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setNull(getParameterIndex(), sqlType);
    }
}
