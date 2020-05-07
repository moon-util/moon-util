package com.moon.more.excel.parse;

import com.moon.core.lang.StringJoiner;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import com.moon.more.excel.annotation.TableIndexer;
import sun.util.BuddhistCalendar;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author benshaoye
 */
abstract class AbstractSupporter {

    protected static <T extends Annotation> T obtain(AnnotatedElement e, Class<T> type) {
        return e.getAnnotation(type);
    }

    protected static TableIndexer obtainIndexer(AnnotatedElement e) { return obtain(e, TableIndexer.class); }

    protected static TableColumnFlatten obtainFlatten(AnnotatedElement e) {
        return obtain(e, TableColumnFlatten.class);
    }

    protected AbstractSupporter() {}

    /*
     * tests
     */

    protected static boolean isBasic(Class type) {
        return isBasicPrimitive(type) || (isBasicSupports(type) && isBasicMatchers(type));
    }

    protected static boolean isBasicPrimitive(Class type) { return type.isPrimitive(); }

    protected static boolean isBasicDatetime(Class type) { return DATETIME.contains(type); }

    protected static boolean isBasicStrings(Class type) { return STRINGS.contains(type); }

    protected static boolean isBasicNumbers(Class type) { return NUMBERS.contains(type); }

    protected static boolean isBasicSupports(Class type) { return SUPPORTS.contains(type); }

    protected static boolean isBasicMatchers(Class type) {
        for (Class matcher : MATCHERS) {
            if (matcher.isAssignableFrom(type)) {
                return true;
            }
        }
        return isBasicPrimitive(type);
    }

    /* map */
    protected static boolean isMap(Class type) { return is(Map.class, type); }

    /* Iterable */
    protected static boolean isIterable(Class type) { return is(Iterable.class, type); }

    /* Iterator */
    protected static boolean isIterator(Class type) { return is(Iterator.class, type); }

    /* 数组 */
    protected static boolean isArray(Class type) { return type.isArray(); }

    /* 列表集合 */
    protected static boolean isCollect(Class type) { return isIterable(type) || isIterator(type); }

    /* 集合 */
    protected static boolean isSetColumn(Class type) { return isCollect(type) || isMap(type) || isArray(type); }

    private static boolean is(Class superCls, Class target) { return superCls.isAssignableFrom(target); }

    /*
     * types
     */

    private final static Collection<Class> NUMBERS = new HashSet<>();
    private final static Collection<Class> STRINGS = new HashSet<>();
    private final static Collection<Class> DATETIME = new HashSet<>();

    private final static Collection<Class> SUPPORTS = new HashSet<>();

    private final static Collection<Class> MATCHERS = new ArrayList<>();

    static {
        MATCHERS.add(CharSequence.class);
        MATCHERS.add(Number.class);
        MATCHERS.add(Date.class);
    }

    static {
        /*
         * ~~ STRINGS
         */

        STRINGS.add(String.class);
        STRINGS.add(StringBuffer.class);
        STRINGS.add(StringBuilder.class);
        STRINGS.add(StringJoiner.class);
        STRINGS.add(java.util.StringJoiner.class);

        /*
         * ~~ NUMBERS
         */

        NUMBERS.add(byte.class);
        NUMBERS.add(short.class);
        NUMBERS.add(int.class);
        NUMBERS.add(long.class);
        NUMBERS.add(float.class);
        NUMBERS.add(double.class);
        NUMBERS.add(Byte.class);
        NUMBERS.add(Short.class);
        NUMBERS.add(Integer.class);
        NUMBERS.add(Long.class);
        NUMBERS.add(Float.class);
        NUMBERS.add(Double.class);

        NUMBERS.add(BigDecimal.class);
        NUMBERS.add(BigInteger.class);
        NUMBERS.add(AtomicInteger.class);
        NUMBERS.add(AtomicLong.class);
        NUMBERS.add(LongAdder.class);
        NUMBERS.add(DoubleAdder.class);

        /*
         * ~~ DATETIME
         */

        DATETIME.add(Date.class);
        DATETIME.add(Time.class);
        DATETIME.add(Timestamp.class);
        DATETIME.add(java.sql.Date.class);

        DATETIME.add(Calendar.class);
        DATETIME.add(BuddhistCalendar.class);
        DATETIME.add(GregorianCalendar.class);

        DATETIME.add(LocalDate.class);
        DATETIME.add(LocalTime.class);
        DATETIME.add(LocalDateTime.class);
        DATETIME.add(OffsetDateTime.class);
        DATETIME.add(OffsetTime.class);
        DATETIME.add(Period.class);
        DATETIME.add(Instant.class);

        /*
         * ~~ SUPPORTS
         */

        SUPPORTS.addAll(STRINGS);
        SUPPORTS.addAll(NUMBERS);
        SUPPORTS.addAll(DATETIME);

        SUPPORTS.add(boolean.class);
        SUPPORTS.add(char.class);

        SUPPORTS.add(Boolean.class);
        SUPPORTS.add(Character.class);
    }

    static Class getActual(Type genericPropType, Class propType) {
        if (propType.isArray()) {
            Class type = propType.getComponentType();
            if (isSetColumn(type)) {
                throw new UnsupportedOperationException("不支持集合数组: " + type.toString());
            }
            return type;
        }
        if (isIterable(propType)) {
            return toActualCls(getActual(genericPropType));
        }
        if (isMap(propType)) {
            return toActualCls(getActual(genericPropType, 1));
        }
        if (isIterator(propType)) {
            return toActualCls(getActual(genericPropType));
        }
        return propType;
    }

    private static Class toActualCls(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Type rawType = pType.getRawType();
            if (rawType instanceof Class) {
                return getActual(type, (Class) rawType);
            }
        } else if (type == null) {
            return null;
        }
        throw new UnsupportedOperationException(type.toString());
    }

    private  static Type getActual(Type type) { return getActual(type, 0); }

    private static Type getActual(Type type, int index) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            return pType.getActualTypeArguments()[index];
        }
        return null;
    }
}
