package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public abstract class BaseParameterSetter implements ParameterSetter {

    /**
     * 从 1 开始
     */
    private final int parameterIndex;
    /**
     * @see Types
     */
    private final int sqlType;

    public BaseParameterSetter(int parameterIndex, int sqlType) {
        this.parameterIndex = parameterIndex;
        this.sqlType = sqlType;
    }

    public int getParameterIndex() { return parameterIndex; }

    @Override
    public void setNull(PreparedStatement stmt) throws SQLException {
        stmt.setNull(getParameterIndex(), sqlType);
    }
}
