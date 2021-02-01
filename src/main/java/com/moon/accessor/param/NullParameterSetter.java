package com.moon.accessor.param;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class NullParameterSetter extends BaseParameterSetter {

    public NullParameterSetter(int parameterIndex, int sqlType) {
        super(parameterIndex, sqlType);
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException { setNull(stmt); }

    @Override
    public String toString() { return "null"; }
}
