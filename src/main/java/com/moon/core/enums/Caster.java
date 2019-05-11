package com.moon.core.enums;

import com.moon.core.time.TimeUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiFunction;

import static com.moon.core.lang.BooleanUtil.toBoolean;
import static com.moon.core.lang.BooleanUtil.toBooleanValue;
import static com.moon.core.lang.ByteUtil.toByte;
import static com.moon.core.lang.ByteUtil.toByteValue;
import static com.moon.core.lang.CharUtil.toCharValue;
import static com.moon.core.lang.CharacterUtil.toCharacter;
import static com.moon.core.lang.DoubleUtil.toDouble;
import static com.moon.core.lang.DoubleUtil.toDoubleValue;
import static com.moon.core.lang.EnumUtil.toEnum;
import static com.moon.core.lang.FloatUtil.toFloat;
import static com.moon.core.lang.FloatUtil.toFloatValue;
import static com.moon.core.lang.IntUtil.toIntValue;
import static com.moon.core.lang.IntegerUtil.toInteger;
import static com.moon.core.lang.LongUtil.toLong;
import static com.moon.core.lang.LongUtil.toLongValue;
import static com.moon.core.lang.ShortUtil.toShort;
import static com.moon.core.lang.ShortUtil.toShortValue;
import static com.moon.core.lang.ThrowUtil.doThrow;
import static com.moon.core.math.BigDecimalUtil.toBigDecimal;
import static com.moon.core.math.BigIntegerUtil.toBigInteger;
import static com.moon.core.time.TimeUtil.toDateTime;
import static com.moon.core.time.TimeUtil.toTime;
import static com.moon.core.util.DateUtil.*;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public enum Caster implements EnumDescriptor,
    BiFunction<Object, Class, Object> {

    toBooleanValue(boolean.class) {
        @Override
        public Object convert(Object o) { return toBooleanValue(o); }
    },
    toBoolean(Boolean.class) {
        @Override
        public Object convert(Object o) { return toBoolean(o); }
    },
    toCharValue(char.class) {
        @Override
        public Object convert(Object o) { return toCharValue(o); }
    },
    toCharacter(Character.class) {
        @Override
        public Object convert(Object o) { return toCharacter(o); }
    },
    toByteValue(byte.class) {
        @Override
        public Object convert(Object o) { return toByteValue(o); }
    },
    toByte(Byte.class) {
        @Override
        public Object convert(Object o) { return toByte(o); }
    },
    toShortValue(short.class) {
        @Override
        public Object convert(Object o) { return toShortValue(o); }
    },
    toShort(Short.class) {
        @Override
        public Object convert(Object o) { return toShort(o); }
    },
    toIntValue(int.class) {
        @Override
        public Object convert(Object o) { return toIntValue(o); }
    },
    toInteger(Integer.class) {
        @Override
        public Object convert(Object o) { return toInteger(o); }
    },
    toLongValue(long.class) {
        @Override
        public Object convert(Object o) { return toLongValue(o); }
    },
    toLong(Long.class) {
        @Override
        public Object convert(Object o) { return toLong(o); }
    },
    toFloatValue(float.class) {
        @Override
        public Object convert(Object o) { return toFloatValue(o); }
    },
    toFloat(Float.class) {
        @Override
        public Object convert(Object o) { return toFloat(o); }
    },
    toDoubleValue(double.class) {
        @Override
        public Object convert(Object o) { return toDoubleValue(o); }
    },
    toDouble(Double.class) {
        @Override
        public Object convert(Object o) { return toDouble(o); }
    },
    toBigInteger(BigInteger.class) {
        @Override
        public Object convert(Object o) { return toBigInteger(o); }
    },
    toBigDecimal(BigDecimal.class) {
        @Override
        public Object convert(Object o) { return toBigDecimal(o); }
    },

    toString(String.class) {
        @Override
        public Object convert(Object o) { return String.valueOf(o); }
    },
    toStringBuffer(StringBuffer.class) {
        @Override
        public Object convert(Object o) { return new StringBuffer(String.valueOf(o)); }
    },
    toStringBuilder(StringBuilder.class) {
        @Override
        public Object convert(Object o) { return new StringBuilder(String.valueOf(o)); }
    },
    toDate(Date.class) {
        @Override
        public Object convert(Object o) { return toDate(o); }
    },
    toSqlTime(Time.class) {
        @Override
        public Object convert(Object o) { return toSqlTime(o); }
    },
    toCalendar(Calendar.class) {
        @Override
        public Object convert(Object o) { return toCalendar(o); }
    },
    toLocalDate(LocalDate.class) {
        @Override
        public Object convert(Object o) { return TimeUtil.toDate(o); }
    },
    toLocalTime(LocalTime.class) {
        @Override
        public Object convert(Object o) { return toTime(o); }
    },
    toLocalDateTime(LocalDateTime.class) {
        @Override
        public Object convert(Object o) { return toDateTime(o); }
    },
    toTimestamp(Timestamp.class) {
        @Override
        public Object convert(Object o) { return toTimestamp(o); }
    },
    toSqlDate(java.sql.Date.class) {
        @Override
        public Object convert(Object o) { return toSqlDate(o); }
    },
    toOptional(Optional.class) {
        @Override
        public Object convert(Object o) { return o instanceof Optional ? o : ofNullable(o); }
    },
    toEnum(Enum.class) {
        @Override
        public Object convert(Object o) { return doThrow("Unsupported."); }

        @Override
        public Object apply(Object o, Class type) { return toEnum(o, type); }
    },
    ;

    public final Class TYPE;

    private static class Cached { final static Map<Class, Caster> CACHE = new HashMap();}

    Caster(Class type) { Cached.CACHE.put(this.TYPE = type, this); }

    public abstract <T> T convert(Object o);

    @Override
    public Object apply(Object o, Class aClass) { return convert(o); }

    @Override
    public String getText() { return TYPE.getName(); }

    public static <T> T to(Object o, Class type) { return (T) requireNonNull(Cached.CACHE.get(type)).apply(o, type); }
}
