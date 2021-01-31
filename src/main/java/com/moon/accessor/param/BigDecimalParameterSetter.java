package com.moon.accessor.param;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class BigDecimalParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final BigDecimal value;

    public BigDecimalParameterSetter(int parameterIndex, BigDecimal value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setBigDecimal(getParameterIndex(), value);
    }
}
