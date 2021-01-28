package com.moon.accessor.meta;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author benshaoye
 */
public enum Fields {
    ;

    public static TableField<String, ?, ?> ofString() {
        return of(String.class);
    }

    public static TableField<Integer, ?, ?> ofInt() {
        return of(Integer.class);
    }

    public static TableField<Long, ?, ?> ofLong() {
        return of(Long.class);
    }

    public static TableField<Double, ?, ?> ofDouble() {
        return of(Double.class);
    }

    public static TableField<BigDecimal, ?, ?> ofBigDecimal() {
        return of(BigDecimal.class);
    }

    public static TableField<BigInteger, ?, ?> ofBigInteger() {
        return of(BigInteger.class);
    }

    public static TableField<Date, ?, ?> ofDate() {
        return of(Date.class);
    }

    public static TableField<java.sql.Date, ?, ?> ofSqlDate() {
        return of(java.sql.Date.class);
    }

    public static TableField<Time, ?, ?> ofTime() {
        return of(Time.class);
    }

    public static TableField<Timestamp, ?, ?> ofTimestamp() {
        return of(Timestamp.class);
    }

    public static <T> TableField<T, ?, ?> of(Class<T> targetClass) {
        return null;
    }
}
