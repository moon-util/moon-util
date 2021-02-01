package com.moon.accessor.param;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author benshaoye
 */
public class BigIntegerParameterSetter extends BaseParameterSetter implements ParameterSetter {

    private final BigInteger value;

    public BigIntegerParameterSetter(int parameterIndex, BigInteger value) {
        this(parameterIndex, Types.BIGINT, value);
    }

    public BigIntegerParameterSetter(int parameterIndex, int sqlType, BigInteger value) {
        super(parameterIndex, sqlType);
        this.value = value;
    }

    @Override
    public void setParameter(PreparedStatement stmt) throws SQLException {
        stmt.setBigDecimal(getParameterIndex(), new BigDecimal(value));
    }

    @Override
    public String toString() { return String.valueOf(value); }
}
