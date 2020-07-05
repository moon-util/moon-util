package com.moon.core.util.convert;

import com.moon.core.json.JSONNumber;
import com.moon.core.json.JSONString;
import com.moon.core.lang.ref.DoubleAccessor;
import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.lang.ref.LongAccessor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.StringJoiner;
import java.util.concurrent.atomic.*;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
@SuppressWarnings("all")
final class Arrs {

    private Arrs() { noInstanceError(); }

    final static Class[] PRIMITIVE_NUMBERS = {
        byte.class, short.class, int.class, long.class, float.class, double.class,
    };

    final static Class[] WRAPPER_NUMBERS = {
        Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class,
    };

    final static Class[] EXPAND_NUMBERS = {
        Number.class,
        BigDecimal.class,
        BigInteger.class,
        AtomicInteger.class,
        AtomicLong.class,
        DoubleAccumulator.class,
        LongAccumulator.class,
        LongAdder.class,
        DoubleAdder.class,
        // from moon util
        JSONNumber.class,
        IntAccessor.class,
        LongAccessor.class,
        DoubleAccessor.class,
    };

    final static Class[] STRINGS = {
        CharSequence.class,
        StringBuilder.class,
        StringBuffer.class,
        String.class,
        StringJoiner.class,
        // from moon util
        JSONString.class,
        com.moon.core.lang.StringJoiner.class,
    };
}
