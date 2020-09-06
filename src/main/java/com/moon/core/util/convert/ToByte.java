package com.moon.core.util.convert;

import java.util.*;

import static com.moon.core.util.convert.ToUtil.*;

/**
 * @author benshaoye
 */
enum ToByte implements Converts {
    /**
     * Number -> int
     */
    byNumber(ToUtil.concatArr(Types.PRIMITIVE_NUMBERS, Types.WRAPPER_NUMBERS, Types.EXPAND_NUMBERS)) {
        @Override
        public Object convertTo(Object o) { return ifn(o) ? null : ((Number) o).byteValue(); }
    },
    byBoolean(arr(Boolean.class, boolean.class)) {
        @Override
        public Object convertTo(Object o) { return ifn(o) ? null : ((Boolean) o ? (byte) 1 : (byte) 0); }
    },
    byChar(arr(Character.class, char.class)) {
        @Override
        public Object convertTo(Object o) { return ifn(o) ? null : (byte) ((char) o); }
    },
    byString(Types.STRINGS) {
        @Override
        public Object convertTo(Object o) { return ifn(o) ? null : Byte.parseByte(o.toString()); }
    },
    byOptionalInt(arr(OptionalInt.class)) {
        @Override
        public Object convertTo(Object o) {
            if (o == null) {
                return null;
            }
            OptionalInt optional = (OptionalInt) o;
            return optional.isPresent() ? (byte) optional.getAsInt() : null;
        }
    },
    byOptionalLong(arr(OptionalLong.class)) {
        @Override
        public Object convertTo(Object o) {
            if (o == null) {
                return null;
            }
            OptionalLong optional = (OptionalLong) o;
            return optional.isPresent() ? (byte) optional.getAsLong() : null;
        }
    },
    byOptionalDouble(arr(OptionalDouble.class)) {
        @Override
        public Object convertTo(Object o) {
            if (o == null) {
                return null;
            }
            OptionalDouble optional = (OptionalDouble) o;
            return optional.isPresent() ? (byte) optional.getAsDouble() : null;
        }
    },
    byUtilOptional(arr(Optional.class)) {
        @Override
        public Object convertTo(Object o) {
            if (o == null) {
                return null;
            }
            Optional optional = (Optional) o;
            return optional.isPresent() ? (byte) optional.get() : null;
        }
    },
    byMoonOptional(arr(Optional.class, com.moon.core.util.Optional.class)) {
        @Override
        public Object convertTo(Object o) {
            if (o == null) {
                return null;
            }
            com.moon.core.util.Optional optional = (com.moon.core.util.Optional) o;
            return optional.isPresent() ? (byte) optional.get() : null;
        }
    };

    private final Set<Class<?>> hashFromSupports;
    private final Set<Class<?>> hashToSupports;
    private final ToPrimitive toPrimitive;

    ToByte(Class[] supportsFrom) {
        Class<?>[] supportsTo = arr(Byte.class);
        this.hashToSupports = unmodifiableHashSet(supportsTo);
        this.hashFromSupports = unmodifiableHashSet(supportsFrom);
        toPrimitive = new ToPrimitive(hashFromSupports, byte.class, this, 0L);
    }

    @Override
    public Set<Class<?>> supportsTo() { return hashToSupports; }

    @Override
    public Set<Class<?>> supportsFrom() { return hashFromSupports; }

    @Override
    public boolean supportsTo(Class type) { return type == Byte.class; }
}
