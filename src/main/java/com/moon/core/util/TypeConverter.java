package com.moon.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.BiFunction;

/**
 * type converter
 *
 * @author benshaoye
 * @date 2018/9/11
 */
@FunctionalInterface
public interface TypeConverter {
    /**
     * register type converter
     *
     * @param toType
     * @param func
     * @param <C>
     * @return
     */
    default <C> TypeConverter register(Class<C> toType, BiFunction<Object, Class<C>, C> func) {
        throw new UnsupportedOperationException();
    }

    /**
     * register type converter if absent
     *
     * @param toType
     * @param func
     * @param <C>
     * @return
     */
    default <C> TypeConverter registerIfAbsent(Class<C> toType, BiFunction<Object, Class<C>, C> func) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to type
     *
     * @param value
     * @param type
     * @param <T>
     * @return
     */
    <T> T toType(Object value, Class<T> type);

    /**
     * value to boolean value
     *
     * @param value
     * @return
     */
    default boolean toBooleanValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to boolean
     *
     * @param value
     * @return
     */
    default Boolean toBoolean(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to char value
     *
     * @param value
     * @return
     */
    default char toCharValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to character
     *
     * @param value
     * @return
     */
    default Character toCharacter(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to byte value
     *
     * @param value
     * @return
     */
    default byte toByteValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to byte
     *
     * @param value
     * @return
     */
    default Byte toByte(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to short value
     *
     * @param value
     * @return
     */
    default short toShortValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to short
     *
     * @param value
     * @return
     */
    default Short toShort(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to int value
     *
     * @param value
     * @return
     */
    default int toIntValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to integer
     *
     * @param value
     * @return
     */
    default Integer toInteger(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to long value
     *
     * @param value
     * @return
     */
    default long toLongValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to long
     *
     * @param value
     * @return
     */
    default Long toLong(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to float value
     *
     * @param value
     * @return
     */
    default float toFloatValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to float
     *
     * @param value
     * @return
     */
    default Float toFloat(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to double value
     *
     * @param value
     * @return
     */
    default double toDoubleValue(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to double
     *
     * @param value
     * @return
     */
    default Double toDouble(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to big integer
     *
     * @param value
     * @return
     */
    default BigInteger toBigInteger(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to big decimal
     *
     * @param value
     * @return
     */
    default BigDecimal toBigDecimal(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to date
     *
     * @param value
     * @return
     */
    default Date toDate(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to sql date
     *
     * @param value
     * @return
     */
    default java.sql.Date toSqlDate(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to timestamp
     *
     * @param value
     * @return
     */
    default Timestamp toTimestamp(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to sql time
     *
     * @param value
     * @return
     */
    default Time toTime(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to calendar
     *
     * @param value
     * @return
     */
    default Calendar toCalendar(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to string
     *
     * @param value
     * @return
     */
    default String toString(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to stringBuilder
     *
     * @param value
     * @return
     */
    default StringBuilder toStringBuilder(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to stringBuffer
     *
     * @param value
     * @return
     */
    default StringBuffer toStringBuffer(Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to enum
     *
     * @param value
     * @param clazz
     * @param <T>
     * @return
     */
    default <T extends Enum<T>> T toEnum(Object value, Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to java bean
     *
     * @param map
     * @param clazz
     * @param <T>
     * @return
     */
    default <T> T toBean(Map map, Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to array
     *
     * @param value
     * @param arrayType
     * @return
     */
    default <T> T toArray(Object value, Class<T> arrayType) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to componentType array
     *
     * @param value
     * @param componentType
     * @return
     */
    default <T> T[] toTypeArray(Object value, Class<T> componentType) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to map
     *
     * @param value
     * @param mapClass
     * @param <T>
     * @return
     */
    default <T extends Map> T toMap(Object value, Class<T> mapClass) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to data
     *
     * @param value
     * @param listType
     * @param <T>
     * @return
     */
    default <T extends List> T toList(Object value, Class<T> listType) {
        throw new UnsupportedOperationException();
    }

    /**
     * value to collection
     *
     * @param value
     * @param collectionType
     * @param <T>
     * @return
     */
    default <T extends Collection> T toCollection(Object value, Class<T> collectionType) {
        throw new UnsupportedOperationException();
    }
}
