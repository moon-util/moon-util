package com.moon.core.math;

import com.moon.core.lang.SupportUtil;
import com.moon.core.lang.ThrowUtil;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.moon.core.lang.NumberComparator.*;

/**
 * @author benshaoye
 */
public final class BigIntegerUtil {
    private BigIntegerUtil() {
        ThrowUtil.noInstanceError();
    }

    public static BigInteger valueOf(String value) {
        return new BigInteger(value);
    }

    public static BigInteger valueOf(int value) {
        return value == 0 ? BigInteger.ZERO : (value == 1 ? BigInteger.ONE : BigInteger.valueOf(value));
    }

    public static BigInteger valueOf(long value) {
        return value == 0 ? BigInteger.ZERO : (value == 1 ? BigInteger.ONE : BigInteger.valueOf(value));
    }

    public static BigInteger defaultIfInvalid(String numeric, BigInteger defaultValue) {
        try {
            return new BigInteger(numeric);
        } catch (Throwable e) {
            return defaultValue;
        }
    }

    public static BigInteger nullIfInvalid(String numeric) {
        return defaultIfInvalid(numeric, null);
    }

    public static BigInteger zeroIfInvalid(String numeric) {
        return defaultIfInvalid(numeric, BigInteger.ZERO);
    }

    public static BigInteger oneIfInvalid(String numeric) {
        return defaultIfInvalid(numeric, BigInteger.ONE);
    }

    public static BigInteger zeroIfNull(BigInteger number) {
        return number == null ? BigInteger.ZERO : number;
    }

    public static BigInteger oneIfNull(BigInteger number) {
        return number == null ? BigInteger.ONE : number;
    }

    public static BigInteger toBigInteger(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof BigInteger) {
            return (BigInteger) value;
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).toBigInteger();
        } else if (value instanceof Number) {
            return BigInteger.valueOf(((Number) value).longValue());
        } else if (value instanceof CharSequence) {
            return new BigInteger(value.toString());
        }
        try {
            Object firstItem = SupportUtil.onlyOneItemOrSize(value);
            return toBigInteger(firstItem);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Can not cast to BigInteger of: %s", value), e);
        }
    }


    public final static boolean gt(BigInteger value1, BigInteger value2) {
        return GT.compare(value1, value2);
    }

    public final static boolean lt(BigInteger value1, BigInteger value2) {
        return LT.compare(value1, value2);
    }

    public final static boolean ge(BigInteger value1, BigInteger value2) {
        return GE.compare(value1, value2);
    }

    public final static boolean le(BigInteger value1, BigInteger value2) {
        return LE.compare(value1, value2);
    }

    public final static boolean eq(BigInteger value1, BigInteger value2) {
        return EQ.compare(value1, value2);
    }

    public final static boolean ne(BigInteger value1, BigInteger value2) {
        return NE.compare(value1, value2);
    }
}
