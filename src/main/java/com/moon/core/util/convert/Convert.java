package com.moon.core.util.convert;

import com.moon.core.util.SetUtil;

import java.util.Set;
import java.util.function.Function;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class Convert {

    static class Transform<T> {

        final <R> R doConvert(Object value, Function<T, R> converter) {
            return doConvert(value, converter, null);
        }

        final <R> R doConvert(Object value, Function<T, R> converter, R dft) {
            return value == null ? dft : converter.apply((T) value);
        }
    }

    private final static Transform transformer = new Transform();

    private Convert() { noInstanceError(); }

    static void register(Function converter, Class<?> toType, Class<?>... fromTypes) {

    }

    public final static class FromInt {

        private final Set<Class<?>> fromTypes = SetUtil.newHashSet(int.class, Integer.class);

        static {
            register(FromInt::toString, String.class, int.class, Integer.class);
        }

        public static String toString(Object value) {
            return (String) transformer.doConvert(value, Object::toString);
        }
    }

    public enum FromInt1 implements Converter {
        //
        toString(String.class) {
            @Override
            public Object exec(Object fromValue) {
                return intConvert.doConvert(fromValue, Object::toString);
            }
        },
        toByte(Byte.class) {
            @Override
            public Object exec(Object fromValue) {
                return intConvert.doConvert(fromValue, Integer::byteValue);
            }
        },
        toByteValue(byte.class) {
            @Override
            public Object exec(Object fromValue) {
                return intConvert.doConvert(fromValue, Integer::byteValue, (byte) 0);
            }
        },
        toShort(Short.class) {
            @Override
            public Object exec(Object fromValue) {
                return intConvert.doConvert(fromValue, Integer::shortValue);
            }
        },
        toShortValue(short.class) {
            @Override
            public Object exec(Object fromValue) {
                return intConvert.doConvert(fromValue, Integer::shortValue, (short) 0);
            }
        },
        toLong(Long.class) {
            @Override
            public Object exec(Object fromValue) {
                return intConvert.doConvert(fromValue, Integer::longValue);
            }
        },
        toLongValue(long.class) {
            @Override
            public Object exec(Object fromValue) {
                return intConvert.doConvert(fromValue, Integer::longValue, 0L);
            }
        },
        toFloat(Float.class) {
            @Override
            public Object exec(Object fromValue) {
                return intConvert.doConvert(fromValue, Integer::floatValue);
            }
        },
        toFloatValue(float.class) {
            @Override
            public Object exec(Object fromValue) {
                return intConvert.doConvert(fromValue, Integer::floatValue, 0F);
            }
        },
        toDouble(Double.class) {
            @Override
            public Object exec(Object fromValue) {
                return intConvert.doConvert(fromValue, Integer::doubleValue);
            }
        },
        toDoubleValue(double.class) {
            @Override
            public Object exec(Object fromValue) {
                return intConvert.doConvert(fromValue, Integer::doubleValue, 0D);
            }
        },
        ;

        private final Set<Class<?>> fromTypes;
        private final Set<Class<?>> toTypes;

        FromInt1(Class<?>... toTypes) {
            this.fromTypes = SetUtil.newHashSet(int.class, Integer.class);
            this.toTypes = SetUtil.newHashSet(toTypes);
        }

        final static Transform<Integer> intConvert = transformer;

        @Override
        public Set<Class<?>> getSupportFromTypes() { return SetUtil.newSet(fromTypes); }

        @Override
        public Set<Class<?>> getSupportToTypes() { return SetUtil.newSet(toTypes); }
    }
}
