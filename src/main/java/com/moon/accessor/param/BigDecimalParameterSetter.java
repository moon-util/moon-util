package com.moon.accessor.param;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public class BigDecimalParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final BigDecimal value;

    public BigDecimalParameterSetter(int parameterIndex, BigDecimal value) {
        this(parameterIndex, Types.NUMERIC, value);
    }

    public BigDecimalParameterSetter(int parameterIndex, int sqlType, BigDecimal value) {
        super(parameterIndex, sqlType);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setBigDecimal(getParameterIndex(), value);
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
