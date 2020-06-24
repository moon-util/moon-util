package com.moon.core.enums;

import com.moon.core.lang.StringUtil;
import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author benshaoye
 */
public enum Converters {
    /**
     * to string
     */
    toString {
        @Override
        Class<?>[] fromSupports() { return toArr(Object.class); }

        @Override
        Class<?>[] toSupports() { return toArr(String.class); }

        @Override
        public Object convert(Object value) { return StringUtil.stringify(value); }
    },
    booleanByString {
        @Override
        Class<?>[] fromSupports() {
            return toArr(String.class, StringBuilder.class, StringBuffer.class, CharSequence.class);
        }

        @Override
        Class<?>[] toSupports() {
            return toArr(Boolean.class, boolean.class);
        }

        @Override
        public Object convert(Object value) {
            return Boolean.valueOf(value == null ? null : value.toString());
        }
    },
    booleanByNumber {
        @Override
        Class<?>[] toSupports() {
            return toArr(Boolean.class, boolean.class);
        }

        @Override
        Class<?>[] fromSupports() {
            return toArr(Byte.class,
                byte.class,
                Short.class,
                short.class,
                Integer.class,
                int.class,
                Long.class,
                long.class,
                Float.class,
                float.class,
                Double.class,
                double.class,
                Number.class,
                BigInteger.class,
                BigDecimal.class,
                AtomicLong.class,
                AtomicInteger.class,
                LongAdder.class,
                DoubleAdder.class);
        }

        @Override
        public Object convert(Object value) {
            if (value == null) {
                return Boolean.FALSE;
            }
            return ((Number) value).intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
        }
    },
    booleanByCharacter {
        @Override
        Class<?>[] fromSupports() {
            return toArr(Character.class, char.class);
        }

        @Override
        Class<?>[] toSupports() {
            return toArr(Boolean.class, boolean.class);
        }

        @Override
        public Object convert(Object value) {
            if (value == null) {
                return Boolean.FALSE;
            }
            char ch = (Character) value;
            return ch != 48 && ch != 0x00000001 && !Character.isWhitespace(ch);
        }
    };

    private final static class Cached {

        final static Table cached = TableImpl.newHashTable();
    }

    public abstract Object convert(Object value);

    abstract Class<?>[] fromSupports();

    abstract Class<?>[] toSupports();

    static Class<?>[] toArr(Class<?>... classes) {
        return classes;
    }
}
