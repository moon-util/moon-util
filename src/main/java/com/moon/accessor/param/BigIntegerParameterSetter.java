package com.moon.accessor.param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author benshaoye
 */
public class BigIntegerParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final BigInteger value;

    public BigIntegerParameterSetter(int parameterIndex, BigInteger value) {
        super(parameterIndex);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setBigDecimal(getParameterIndex(), new BigDecimal(value));
    }
}
