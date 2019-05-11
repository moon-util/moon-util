package com.moon.core.enums;

import com.moon.core.lang.*;
import com.moon.core.math.BigDecimalUtil;
import com.moon.core.math.BigIntegerUtil;
import com.moon.core.time.TimeUtil;
import com.moon.core.util.DateUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.BiFunction;

import static java.util.Objects.requireNonNull;

/**
 * @author benshaoye
 * @date 2018/9/11
 */
public enum Caster implements EnumDescriptor,
    BiFunction<Object, Class, Object> {

    toBooleanValue(boolean.class) {
        @Override
        public Object convert(Object o) { return BooleanUtil.toBooleanValue(o); }
    },
    toBoolean(Boolean.class) {
        @Override
        public Object convert(Object o) { return BooleanUtil.toBoolean(o); }
    },
    toCharValue(char.class) {
        @Override
        public Object convert(Object o) { return CharUtil.toCharValue(o); }
    },
    toCharacter(Character.class) {
        @Override
        public Object convert(Object o) { return CharacterUtil.toCharacter(o); }
    },
    toByteValue(byte.class) {
        @Override
        public Object convert(Object o) { return ByteUtil.toByteValue(o); }
    },
    toByte(Byte.class) {
        @Override
        public Object convert(Object o) { return ByteUtil.toByte(o); }
    },
    toShortValue(short.class) {
        @Override
        public Object convert(Object o) { return ShortUtil.toShortValue(o); }
    },
    toShort(Short.class) {
        @Override
        public Object convert(Object o) { return ShortUtil.toShort(o); }
    },
    toIntValue(int.class) {
        @Override
        public Object convert(Object o) { return IntUtil.toIntValue(o); }
    },
    toInteger(Integer.class) {
        @Override
        public Object convert(Object o) { return IntegerUtil.toInteger(o); }
    },
    toLongValue(long.class) {
        @Override
        public Object convert(Object o) { return LongUtil.toLongValue(o); }
    },
    toLong(Long.class) {
        @Override
        public Object convert(Object o) { return LongUtil.toLong(o); }
    },
    toFloatValue(float.class) {
        @Override
        public Object convert(Object o) { return FloatUtil.toFloatValue(o); }
    },
    toFloat(Float.class) {
        @Override
        public Object convert(Object o) { return FloatUtil.toFloat(o); }
    },
    toDoubleValue(double.class) {
        @Override
        public Object convert(Object o) { return DoubleUtil.toDoubleValue(o); }
    },
    toDouble(Double.class) {
        @Override
        public Object convert(Object o) { return DoubleUtil.toDouble(o); }
    },
    toBigInteger(BigInteger.class) {
        @Override
        public Object convert(Object o) { return BigIntegerUtil.toBigInteger(o); }
    },
    toBigDecimal(BigDecimal.class) {
        @Override
        public Object convert(Object o) { return BigDecimalUtil.toBigDecimal(o); }
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
        public Object convert(Object o) { return DateUtil.toDate(o); }
    },
    toSqlTime(Time.class) {
        @Override
        public Object convert(Object o) { return DateUtil.toSqlTime(o); }
    },
    toCalendar(Calendar.class) {
        @Override
        public Object convert(Object o) { return DateUtil.toCalendar(o); }
    },
    toLocalDate(LocalDate.class) {
        @Override
        public Object convert(Object o) { return TimeUtil.toDate(o); }
    },
    toLocalTime(LocalTime.class) {
        @Override
        public Object convert(Object o) { return TimeUtil.toTime(o); }
    },
    toLocalDateTime(LocalDateTime.class) {
        @Override
        public Object convert(Object o) { return TimeUtil.toDateTime(o); }
    },
    toTimestamp(Timestamp.class) {
        @Override
        public Object convert(Object o) { return DateUtil.toTimestamp(o); }
    },
    toSqlDate(java.sql.Date.class) {
        @Override
        public Object convert(Object o) { return DateUtil.toSqlDate(o); }
    },
    toOptional(Optional.class) {
        @Override
        public Object convert(Object o) { return o instanceof Optional ? o : Optional.ofNullable(o); }
    },
    toEnum(Enum.class) {
        @Override
        public Object convert(Object o) { return ThrowUtil.doThrow("Unsupported."); }

        @Override
        public Object apply(Object o, Class type) { return EnumUtil.toEnum(o, type); }
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
