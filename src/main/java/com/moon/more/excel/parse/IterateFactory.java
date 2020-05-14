package com.moon.more.excel.parse;

import com.moon.core.util.IteratorUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author benshaoye
 */
class IterateFactory {

    final static IterateStrategy NONE = NoneIterateStrategy.NONE;

    static IterateStrategy getIterateStrategy(AbstractMark iterateAt) {
        if (iterateAt == null) {
            return IterateStrategy.NONE;
        }
        Class propertyClass = iterateAt.getPropertyType();
        if (propertyClass.isArray()) {
            return ArrayIterateStrategy.getOrObjects(propertyClass);
        } else {
            IterateStrategy[] values = CollectIterateStrategy.getValues();
            for (IterateStrategy value : values) {
                if (value.test(propertyClass)) {
                    return value;
                }
            }
            values = MapIterateStrategy.getValues();
            for (IterateStrategy value : values) {
                if (value.test(propertyClass)) {
                    return value;
                }
            }
            throw new NullPointerException("未知集合类型: " + propertyClass);
            // return NoneIterateStrategy.NONE;
        }
    }

    @SuppressWarnings({"rawtypes"})
    private enum ArrayIterateStrategy implements IterateStrategy {
        /** 数组 */
        ARRAY_OBJECT(Object[].class) {
            @Override
            public Iterator iterator(Object data) { return IteratorUtil.of((Object[]) data); }
        },
        ARRAY_BOOLEAN(boolean[].class) {
            @Override
            public Iterator iterator(Object data) { return IteratorUtil.of((boolean[]) data); }
        },
        ARRAY_CHARS(char[].class) {
            @Override
            public Iterator iterator(Object data) { return IteratorUtil.of((char[]) data); }
        },
        ARRAY_BYTE(byte[].class) {
            @Override
            public Iterator iterator(Object data) { return IteratorUtil.of((byte[]) data); }
        },
        ARRAY_SHORT(short[].class) {
            @Override
            public Iterator iterator(Object data) { return IteratorUtil.of((short[]) data); }
        },
        ARRAY_INT(int[].class) {
            @Override
            public Iterator iterator(Object data) { return IteratorUtil.of((int[]) data); }
        },
        ARRAY_LONG(long[].class) {
            @Override
            public Iterator iterator(Object data) { return IteratorUtil.of((long[]) data); }
        },
        ARRAY_FLOAT(float[].class) {
            @Override
            public Iterator iterator(Object data) { return IteratorUtil.of((float[]) data); }
        },
        ARRAY_DOUBLE(double[].class) {
            @Override
            public Iterator iterator(Object data) { return IteratorUtil.of((double[]) data); }
        },
        ;

        private final Class topClass;

        ArrayIterateStrategy(Class topClass) {
            ArrayIterateStrategy.Cached.CACHE.put(topClass, this);
            this.topClass = topClass;
        }

        @Override
        public Class getTopClass() { return topClass; }

        private final static ArrayIterateStrategy[] VALUES = values();

        public static ArrayIterateStrategy[] getValues() { return VALUES; }

        static class Cached {

            final static Map<Class, ArrayIterateStrategy> CACHE = new HashMap<>();
        }

        public static ArrayIterateStrategy getOrObjects(Class type) {
            return ArrayIterateStrategy.Cached.CACHE.getOrDefault(type, ARRAY_OBJECT);
        }
    }

    @SuppressWarnings({"all"})
    private enum NoneIterateStrategy implements IterateStrategy {
        NONE;

        @Override
        public Iterator iterator(Object data) { return IteratorUtil.ofEmpty(); }

        @Override
        public Class getTopClass() { return null; }

        @Override
        public boolean test(Class propertyClass) { return true; }
    }

    @SuppressWarnings({"rawtypes"})
    private enum MapIterateStrategy implements IterateStrategy {
        /** map */
        MAP(Map.class) {
            @Override
            public Iterator iterator(Object data) { return ((Map) data).values().iterator(); }
        },
        ;

        private final Class topClass;

        MapIterateStrategy(Class topClass) { this.topClass = topClass; }

        @Override
        public Class getTopClass() { return topClass; }

        @Override
        public Iterator iterator(Object data) { return ((Map) data).values().iterator(); }

        private final static MapIterateStrategy[] VALUES = values();

        public static MapIterateStrategy[] getValues() { return VALUES; }
    }

    @SuppressWarnings({"rawtypes"})
    private enum CollectIterateStrategy implements IterateStrategy {
        /** 集合 */
        ITERATE(Iterable.class) {
            @Override
            public Iterator iterator(Object data) { return ((Iterable) data).iterator(); }
        },
        /** 迭代器 */
        ITERATOR(Iterator.class) {
            @Override
            public Iterator iterator(Object data) { return (Iterator) data; }
        },
        ;

        private final Class topClass;

        CollectIterateStrategy(Class topClass) { this.topClass = topClass; }

        @Override
        public Class getTopClass() { return topClass; }

        private final static CollectIterateStrategy[] VALUES = values();

        public static CollectIterateStrategy[] getValues() { return VALUES; }
    }
}
