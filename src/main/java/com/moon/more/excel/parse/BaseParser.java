package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.DataColumn;
import com.moon.more.excel.annotation.DataColumnFlatten;
import com.moon.more.excel.annotation.DataIndexer;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * @author benshaoye
 */
abstract class BaseParser {

    private final static String COL_NAME = "@" + DataColumn.class.getSimpleName();

    private final static String FLAT_NAME = "@" + DataColumnFlatten.class.getSimpleName();

    private final static String NOT_ALLOWED = COL_NAME + " & " + FLAT_NAME + " 不能用于同一字段: {}。 \n\t[ " +

        COL_NAME + " ]用于普通数据字段（int、double、String、BiGDecimal）；\n\t[ " +

        FLAT_NAME + " ]用于复合数据字段，如集合、数组、实体等。";

    protected static String getNotAllowed(String prop) { return NOT_ALLOWED.replace("{}", prop); }

    protected static <T extends Annotation> T obtain(AnnotatedElement e, Class<T> type) {
        return e.getAnnotation(type);
    }

    protected static DataIndexer obtainIndexer(AnnotatedElement e) { return obtain(e, DataIndexer.class); }

    protected static DataColumnFlatten obtainFlatten(AnnotatedElement e) { return obtain(e, DataColumnFlatten.class); }

    protected BaseParser() {}

    /*
     * tests
     */

    protected static boolean isBasic(Class type) { return isBasicSupports(type) && isBasicMatchers(type); }

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
        return false;
    }

    protected static boolean isMap(Class type) { return is(Map.class, type); }

    protected static boolean isIterable(Class type) { return is(Iterable.class, type); }

    protected static boolean isIterator(Class type) { return is(Iterator.class, type); }

    protected static boolean isArray(Class type) { return type.isArray(); }

    protected static boolean isCollect(Class type) { return isIterable(type) || isIterator(type); }

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

        /*
         * ~~ DATETIME
         */

        DATETIME.add(Date.class);
        DATETIME.add(Time.class);
        DATETIME.add(Timestamp.class);
        DATETIME.add(java.sql.Date.class);

        DATETIME.add(Calendar.class);

        DATETIME.add(LocalDate.class);
        DATETIME.add(LocalTime.class);
        DATETIME.add(LocalDateTime.class);

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
            if (isIterable(type) || isMap(type) || isIterator(type) || type.isArray()) {
                throw new UnsupportedOperationException(type.toString());
            }
            return type;
        }
        if (isIterable(propType)) {
            return toActualCls(ParamUtil.getActual(genericPropType));
        }
        if (isMap(propType)) {
            return toActualCls(ParamUtil.getActual(genericPropType, 1));
        }
        if (isIterator(propType)) {
            return toActualCls(ParamUtil.getActual(genericPropType));
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
}
