package com.moon.core.util.convert;

import java.util.*;

import static com.moon.core.util.convert.ToUtil.*;

/**
 * @author benshaoye
 */
enum ToShort implements Converts {
    /**
     * Number -> int
     */
    byNumber(ToUtil.concatArr(Types.PRIMITIVE_NUMBERS, Types.WRAPPER_NUMBERS, Types.EXPAND_NUMBERS)) {
        @Override
        public Object convertTo(Object o) { return ifn(o) ? null : ((Number) o).shortValue(); }
    },
    byBoolean(arr(Boolean.class, boolean.class)) {
        @Override
        public Object convertTo(Object o) { return ifn(o) ? null : ((Boolean) o ? (short) 1 : (short) 0); }
    },
    byChar(arr(Character.class, char.class)) {
        @Override
        public Object convertTo(Object o) { return ifn(o) ? null : (short) ((char) o); }
    },
    byString(Types.STRINGS) {
        @Override
        public Object convertTo(Object o) { return ifn(o) ? null : Short.parseShort(o.toString()); }
    },
    byOptionalInt(arr(OptionalInt.class)) {
        @Override
        public Object convertTo(Object o) {
            if (o == null) {
                return ZERO;
            }
            OptionalInt optional = (OptionalInt) o;
            return optional.isPresent() ? (short) optional.getAsInt() : ZERO;
        }
    },
    byOptionalLong(arr(OptionalLong.class)) {
        @Override
        public Object convertTo(Object o) {
            if (o == null) {
                return ZERO;
            }
            OptionalLong optional = (OptionalLong) o;
            return optional.isPresent() ? (short) optional.getAsLong() : ZERO;
        }
    },
    byOptionalDouble(arr(OptionalDouble.class)) {
        @Override
        public Object convertTo(Object o) {
            if (o == null) {
                return ZERO;
            }
            OptionalDouble optional = (OptionalDouble) o;
            return optional.isPresent() ? (short) optional.getAsDouble() : ZERO;
        }
    },
    byUtilOptional(arr(Optional.class, com.moon.core.util.Optional.class)) {
        @Override
        public Object convertTo(Object o) {
            if (o == null) {
                return ZERO;
            }
            Optional optional = (Optional) o;
            return optional.isPresent() ? ((Number) optional.get()).shortValue() : ZERO;
        }
    },
    byMoonOptional(arr(Optional.class, com.moon.core.util.Optional.class)) {
        @Override
        public Object convertTo(Object o) {
            com.moon.core.util.Optional optional = (com.moon.core.util.Optional) o;
            return optional.isPresent() ? ((Number) optional.get()).shortValue() : ZERO;
        }
    };

    private final Set<Class<?>> hashFromSupports;
    private final Set<Class<?>> hashToSupports;
    private final ToPrimitive toPrimitive;
    protected final Short ZERO = (short) 0;

    ToShort(Class[] supportsFrom) {
        Class<?>[] supportsTo = arr(Short.class);
        this.hashToSupports = unmodifiableHashSet(supportsTo);
        this.hashFromSupports = unmodifiableHashSet(supportsFrom);
        toPrimitive = new ToPrimitive(hashFromSupports, short.class, this, 0L);
    }

    @Override
    public Set<Class<?>> supportsTo() { return hashToSupports; }

    @Override
    public Set<Class<?>> supportsFrom() { return hashFromSupports; }

    @Override
    public boolean supportsTo(Class type) { return type == Short.class; }
}
