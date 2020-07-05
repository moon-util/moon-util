package com.moon.core.util.convert;

import com.moon.core.json.JSONBoolean;
import com.moon.core.lang.ArrayUtil;
import com.moon.core.util.SetUtil;
import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;
import com.moon.core.util.converter.Converter;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.BooleanSupplier;

import static com.moon.core.util.convert.ConvertUtil.arr;
import static com.moon.core.util.convert.ConvertUtil.ifn;

enum ToBoolean implements Converts {
    /**
     * byte -> boolean
     */
    byByteVal(arr(byte.class)) {
        @Override
        public Boolean convert(Object value) { return (Byte) value != 0; }
    },
    byByte(arr(Byte.class)) {
        @Override
        public Boolean convert(Object value) { return ifn(value) ? null : (Byte) value != 0; }
    },
    /**
     * short -> boolean
     */
    byShortVal(arr(short.class)) {
        @Override
        public Boolean convert(Object value) { return (Short) value != 0; }
    },
    byShort(arr(Short.class)) {
        @Override
        public Boolean convert(Object value) { return ifn(value) ? null : (Short) value != 0; }
    },
    /**
     * int -> boolean
     */
    byIntVal(arr(int.class)) {
        @Override
        public Boolean convert(Object value) { return (Integer) value != 0; }
    },
    byInteger(arr(Integer.class)) {
        @Override
        public Boolean convert(Object value) { return ifn(value) ? null : (Integer) value != 0; }
    },
    /**
     * long -> boolean
     */
    byLongVal(arr(long.class)) {
        @Override
        public Boolean convert(Object value) { return (Long) value != 0; }
    },
    byLong(arr(Long.class)) {
        @Override
        public Boolean convert(Object value) { return ifn(value) ? null : (Long) value != 0; }
    },
    /**
     * float -> boolean
     */
    byFloatVal(arr(float.class)) {
        @Override
        public Boolean convert(Object value) { return (Float) value != 0; }
    },
    byFloat(arr(Float.class), arr(Boolean.class)) {
        @Override
        public Boolean convert(Object value) { return ifn(value) ? null : (Float) value != 0; }
    },
    /**
     * double -> boolean
     */
    byDoubleVal(arr(double.class)) {
        @Override
        public Boolean convert(Object value) { return (Double) value != 0; }
    },
    byDouble(arr(Double.class)) {
        @Override
        public Boolean convert(Object value) { return ifn(value) ? null : (Double) value != 0; }
    },
    /**
     * AtomicBoolean -> boolean
     */
    byAtomicBoolean(arr(AtomicBoolean.class)) {
        @Override
        public Boolean convert(Object value) { return ifn(value) ? null : ((AtomicBoolean) value).get(); }
    },
    /**
     * Number -> boolean
     */
    byNumber(Arrs.EXPAND_NUMBERS, Number.class) {
        @Override
        public Boolean convert(Object value) { return ifn(value) ? null : ((Number) value).doubleValue() != 0; }
    },
    /**
     * String -> boolean
     */
    byCharSequence(Arrs.STRINGS, CharSequence.class) {
        @Override
        public Boolean convert(Object value) { return ifn(value) ? null : Boolean.valueOf(value.toString()); }
    },
    /**
     * Object -> boolean
     */
    byBooleanSupplier(arr(BooleanSupplier.class), BooleanSupplier.class) {
        @Override
        public Boolean convert(Object value) {
            return ((BooleanSupplier) value).getAsBoolean();
        }
    },
    byOptional(arr(Optional.class)) {
        @Override
        public Object convert(Object value) {
            // TODO Optional 返回的值可能不是 Boolean
            return ((Optional) value).get();
        }
    },
    byJSONBoolean(arr(JSONBoolean.class)) {
        @Override
        public Boolean convert(Object value) { return ifn(value) ? null : ((JSONBoolean) value).getBoolean(); }
    },
    byObject(arr(Object.class)) {
        @Override
        public Boolean convert(Object value) { return !ifn(value); }
    },
    ;

    private final Set<Class<?>> hashFromSupports;
    private final Set<Class<?>> hashToSupports;

    ToBoolean(Class[] hashFromSupports, Class... matchFromSupports) {
        this.hashFromSupports = unmodifiableHashSet(hashFromSupports);
        Class<?>[] supportsTo = arr(boolean.class, Boolean.class);
        this.hashToSupports = unmodifiableHashSet(supportsTo);
        Cached.cacheHashs(hashFromSupports, supportsTo, this);
        Cached.cacheMatches(matchFromSupports, supportsTo, this);
    }

    static Set<Class<?>> unmodifiableHashSet(Class<?>... classes) {
        return Collections.unmodifiableSet(SetUtil.newHashSet(classes));
    }

    /**
     * 支持的源数据类型
     *
     * @return 支持的源数据类型
     */
    @Override
    public Set<Class<?>> supportsFrom() { return hashFromSupports; }

    /**
     * 支持的目标数据类型
     *
     * @return 支持的目标数据类型
     */
    @Override
    public Set<Class<?>> supportsTo() { return hashToSupports; }

    /**
     * 执行数据转换
     *
     * @param value 源数据
     *
     * @return 转换后的 Boolean 数据
     */
    @Override
    public Object convert(Object value) { return value; }

    /**
     * 是否支持从{@link Object}转换为目标数据类型
     *
     * @return 一般认为，如果 fromSupports 里有 Object 就支持
     */
    boolean supportFromObj() { return hashFromSupports.contains(Object.class); }

    /**
     * 查找转换器，从 fromType 到 toType 的转换器
     *
     * @param fromType 数据自身类型
     * @param toType   转换目标数据类型
     *
     * @return 匹配转换类型的类型转换器或 null
     */
    public static Converter find(Class fromType, Class toType) {
        Converter converter = Cached.hashCached.get(toType, fromType);
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

        private final static Table<Class, Class, Converter> hashCached = new TableImpl();

        private final static Table<Class, Class, LinkedList<MatchConverter>> matchCached = new TableImpl();

        private static void cacheMatches(Class[] hashFromSupports, Class[] hashToSupports, Converter converter) {
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

        private static void cacheHashs(Class[] hashFromSupports, Class[] hashToSupports, Converter converter) {
            for (Class hashToSupport : hashToSupports) {
                for (Class hashFromSupport : hashFromSupports) {
                    Cached.hashCached.put(hashToSupport, hashFromSupport, converter);
                }
            }
        }
    }

    private static class ObjectCached {

        private final static Map<Class, Converter> converterMap = new HashMap<>();

        static {
            for (ToBoolean value : ToBoolean.values()) {
                if (value.supportFromObj()) {
                    for (Class<?> toType : value.supportsTo()) {
                        converterMap.put(toType, value);
                    }
                }
            }
        }
    }

    static class MatchConverter implements Converter, Comparable<MatchConverter> {

        final Class supportedType;
        final Converter originConverter;

        MatchConverter(Class supportedType, Converter originConverter) {
            this.originConverter = originConverter;
            this.supportedType = supportedType;
        }

        boolean supports(Class thatType) { return supportedType.isAssignableFrom(thatType); }

        @Override
        public int compareTo(MatchConverter o) {
            return compare(this.supportedType, o.supportedType);
        }

        @Override
        public Object convert(Object o) {
            return originConverter.convert(o);
        }
    }

    private static int compare(Class o1, Class o2) {
        boolean forward = o1.isAssignableFrom(o2);
        boolean backward = o2.isAssignableFrom(o1);
        return forward ? (backward ? 0 : 1) : -1;
    }
}
