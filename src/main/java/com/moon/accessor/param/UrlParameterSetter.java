package com.moon.accessor.param;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public class UrlParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final URL value;

    public UrlParameterSetter(int parameterIndex, URL value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setObject(getParameterIndex(), value, Types.DATALINK);
    }
}
