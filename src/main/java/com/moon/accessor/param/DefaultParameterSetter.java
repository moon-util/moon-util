package com.moon.accessor.param;

import com.moon.accessor.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class DefaultParameterSetter implements ParameterSetter {

    private final int jdbcType;
    private final Object value;
    private final TypeHandler<Object> handler;

    public DefaultParameterSetter(int jdbcType, Object value, TypeHandler<Object> handler) {
        this.jdbcType = jdbcType;
        this.value = value;
        this.handler = handler;
    }

    @Override
    public void setParameter(PreparedStatement stmt, int index) throws SQLException {
        handler.setParameter(stmt, index, value, jdbcType);
    }
}
