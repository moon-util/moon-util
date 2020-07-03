package com.moon.core.enums;

import com.moon.core.json.JSONBoolean;
import com.moon.core.json.JSONString;
import com.moon.core.lang.ArrayUtil;
import com.moon.core.util.SetUtil;
import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

enum Converters implements Function {
    /**
     * byte -> boolean
     */
    ByteValue2BooleanValue(arr(byte.class), arr(boolean.class, Boolean.class)) {
        @Override
        public Boolean convert(Object value) { return ((Byte) value).byteValue() != 0; }
    },
    Byte2BooleanValue(arr(Byte.class), arr(boolean.class, Boolean.class)) {
        @Override
        public Boolean convert(Object value) { return inn(value) && ((Byte) value).byteValue() != 0; }
    },
    /**
     * short -> boolean
     */
    ShortValue2BooleanValue(arr(short.class), arr(boolean.class, Boolean.class)) {
        @Override
        public Boolean convert(Object value) { return ((Short) value).shortValue() != 0; }
    },
    Short2BooleanValue(arr(Short.class), arr(boolean.class, Boolean.class)) {
        @Override
        public Boolean convert(Object value) { return inn(value) && ((Short) value).shortValue() != 0; }
    },
    /**
     * int -> boolean
     */
    Int2BooleanValue(arr(int.class), arr(boolean.class, Boolean.class)) {
        @Override
        public Boolean convert(Object value) { return ((Integer) value).intValue() != 0; }
    },
    Integer2BooleanValue(arr(Integer.class), arr(boolean.class, Boolean.class)) {
        @Override
        public Boolean convert(Object value) { return inn(value) && ((Integer) value).intValue() != 0; }
    },
    /**
     * long -> boolean
     */
    LongValue2BooleanValue(arr(long.class), arr(boolean.class, Boolean.class)) {
        @Override
        public Boolean convert(Object value) { return ((Long) value).longValue() != 0; }
    },
    Long2BooleanValue(arr(Long.class), arr(boolean.class, Boolean.class)) {
        @Override
        public Boolean convert(Object value) { return inn(value) && ((Long) value).longValue() != 0; }
    },
    /**
     * float -> boolean
     */
    FloatValue2BooleanValue(arr(float.class), arr(boolean.class, Boolean.class)) {
        @Override
        public Boolean convert(Object value) { return ((Float) value).floatValue() != 0; }
    },
    Float2Boolean(arr(Float.class), arr(Boolean.class)) {
        @Override
        public Boolean convert(Object value) { return inn(value) && ((Float) value).floatValue() != 0; }
    },
    /**
     * double -> boolean
     */
    DoubleValue2BooleanValue(arr(double.class), arr(boolean.class, Boolean.class)) {
        @Override
        public Boolean convert(Object value) { return ((Double) value).doubleValue() != 0; }
    },
    Double2BooleanValue(arr(Double.class), arr(boolean.class, Boolean.class)) {
        @Override
        public Boolean convert(Object value) { return inn(value) && ((Double) value).doubleValue() != 0; }
    },
    /**
     * AtomicBoolean -> boolean
     */
    AtomicBoolean2BooleanValue(arr(AtomicBoolean.class), arr(boolean.class, Boolean.class)) {
        @Override
        public Boolean convert(Object value) { return inn(value) && ((AtomicBoolean) value).get(); }
    },
    /**
     * Number -> boolean
     */
    Number2BooleanValue(Arrs.NUMBERS, arr(boolean.class, Boolean.class), Number.class) {
        @Override
        public Boolean convert(Object value) { return inn(value) && ((Number) value).doubleValue() != 0; }
    },
    /**
     * String -> boolean
     */
    CharSequence2BooleanValue(Arrs.STRINGS, arr(boolean.class, Boolean.class), CharSequence.class) {
        @Override
        public Boolean convert(Object value) { return inn(value) && Boolean.valueOf(value.toString()); }
    },
    /**
     * Object -> boolean
     */
    BooleanSupplier2BooleanValue(arr(BooleanSupplier.class), arr(boolean.class, Boolean.class), BooleanSupplier.class) {
        @Override
        public Boolean convert(Object value) { return value instanceof BooleanSupplier && ((BooleanSupplier) value).getAsBoolean(); }
    },
    JSONBoolean2BooleanValue(arr(JSONBoolean.class), arr(boolean.class, Boolean.class)) {
        @Override
        public Boolean convert(Object value) { return inn(value) && ((JSONBoolean) value).getBoolean(); }
    },
    Object2BooleanValue(arr(Object.class), arr(boolean.class, Boolean.class)) {
        @Override
        public Boolean convert(Object value) { return inn(value); }
    },
    ;

    private final Set<Class> hashFromSupports;
    private final Set<Class> hashToSupports;

    Converters(Class[] hashFromSupports, Class[] hashToSupports, Class... matchFromSupports) {
        this.hashFromSupports = SetUtil.newHashSet(hashFromSupports);
        this.hashToSupports = SetUtil.newHashSet(hashToSupports);
        Cached.cacheHashs(hashFromSupports, hashToSupports, this);
        Cached.cacheMatches(matchFromSupports, hashToSupports, this);
    }

    /**
     * 支持的源数据类型
     *
     * @return 支持的源数据类型
     */
    public Class<?>[] fromSupports() { return SetUtil.toArray(hashFromSupports, Class[]::new); }

    /**
     * 支持的目标数据类型
     *
     * @return 支持的目标数据类型
     */
    public Class<?>[] toSupports() { return SetUtil.toArray(hashFromSupports, Class[]::new); }

    /**
     * 执行数据转换
     *
     * @param value 源数据
     *
     * @return 转换后的 Boolean 数据
     */
    public Object convert(Object value) { return value; }

    /**
     * 是否支持从{@link Object}转换为目标数据类型
     *
     * @return 一般认为，如果 fromSupports 里有 Object 就支持
     */
    boolean supportFromObj() { return hashFromSupports.contains(Object.class); }

    /**
     * 执行数据转换
     *
     * @param o 源数据
     *
     * @return 转换后的 Boolean 数据
     */
    @Override
    public Object apply(Object o) { return convert(o); }

    /**
     * 查找转换器，从 fromType 到 toType 的转换器
     *
     * @param fromType 数据自身类型
     * @param toType   转换目标数据类型
     *
     * @return 匹配转换类型的类型转换器或 null
     */
    public static Function find(Class fromType, Class toType) {
        Function converter = Cached.hashCached.get(toType, fromType);
        if (converter == null) {
            // TODO 以兼容的方式查找匹配的转换器
        }
        return converter;
    }

    /*
     |----------|------------------------------------------------------------------------------------------------|
     | supports |                                                                                                |
     |----------|------------------------------------------------------------------------------------------------|
     */

    private static class Cached {

        private final static Table<Class, Class, Function> hashCached = new TableImpl();

        private final static Table<Class, Class, LinkedList<MatchConverter>> matchCached = new TableImpl();

        private static void cacheMatches(Class[] hashFromSupports, Class[] hashToSupports, Function converter) {
            for (Class hashToSupport : hashToSupports) {
                LinkedList<MatchConverter> converters = ArrayUtil.reduce(hashFromSupports, (list, type, idx) -> {
                    list.add(new MatchConverter(type, converter));
                    return list;
                }, new LinkedList());
                if (!converters.isEmpty()) {
                    converters.sort(MatchConverter::compareTo);
                    Class topType = converters.getLast().supportedType;
                    Cached.matchCached.put(hashToSupport, topType, converters);
                }
            }
        }

        private static void cacheHashs(Class[] hashFromSupports, Class[] hashToSupports, Function converter) {
            for (Class hashToSupport : hashToSupports) {
                for (Class hashFromSupport : hashFromSupports) {
                    Cached.hashCached.put(hashToSupport, hashFromSupport, converter);
                }
            }
        }
    }

    private static class ObjectCached {

        private final static Map<Class, Function> converterMap = new HashMap<>();

        static {
            for (Converters value : Converters.values()) {
                if (value.supportFromObj()) {
                    for (Class<?> toType : value.toSupports()) {
                        converterMap.put(toType, value);
                    }
                }
            }
        }
    }

    private static class Arrs {

        final static Class[] NUMBERS = {
            Number.class,
            BigDecimal.class,
            BigInteger.class,
            AtomicInteger.class,
            AtomicLong.class,
            DoubleAccumulator.class,
            LongAccumulator.class,
            LongAdder.class,
            DoubleAdder.class
        };

        final static Class[] STRINGS = {
            CharSequence.class,
            StringBuilder.class,
            StringBuffer.class,
            String.class,
            StringJoiner.class,
            com.moon.core.lang.StringJoiner.class,
            JSONString.class
        };
    }

    static class MatchConverter implements Function, Comparable<MatchConverter> {

        final Class supportedType;
        final Function originConverter;

        MatchConverter(Class supportedType, Function originConverter) {
            this.originConverter = originConverter;
            this.supportedType = supportedType;
        }

        boolean supports(Class thatType) { return supportedType.isAssignableFrom(thatType); }

        @Override
        public Object apply(Object o) { return originConverter.apply(o); }

        @Override
        public int compareTo(MatchConverter o) {
            return compare(this.supportedType, o.supportedType);
        }
    }

    private static int compare(Class o1, Class o2) {
        boolean forward = o1.isAssignableFrom(o2);
        boolean backward = o2.isAssignableFrom(o1);
        return forward ? (backward ? 0 : 1) : -1;
    }

    private static boolean inn(Object value) { return value != null; }

    private static Class[] arr(Class... classes) { return classes; }
}
