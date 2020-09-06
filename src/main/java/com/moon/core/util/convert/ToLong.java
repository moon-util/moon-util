package com.moon.core.util.convert;

import java.util.*;

import static com.moon.core.util.convert.ToUtil.*;

/**
 * @author benshaoye
 */
enum ToLong implements Converts {
    /**
     * Number -> int
     */
    byNumber(ToUtil.concatArr(Types.PRIMITIVE_NUMBERS, Types.WRAPPER_NUMBERS, Types.EXPAND_NUMBERS)) {
        @Override
        public Object convertTo(Object o) { return ifn(o) ? null : ((Number) o).longValue(); }
    },
    byBoolean(arr(Boolean.class, boolean.class)) {
        @Override
        public Object convertTo(Object o) { return ifn(o) ? null : ((Boolean) o ? 1L : 0L); }
    },
    byChar(arr(Character.class, char.class)) {
        @Override
        public Object convertTo(Object o) { return ifn(o) ? null : (long) ((char) o); }
    },
    byString(Types.STRINGS) {
        @Override
        public Object convertTo(Object o) { return ifn(o) ? null : Long.parseLong(o.toString()); }
    },
    byOptionalInt(arr(OptionalInt.class)) {
        @Override
        public Object convertTo(Object o) {
            if (o == null) {
                return null;
            }
            OptionalInt optional = (OptionalInt) o;
            return optional.isPresent() ? (long) optional.getAsInt() : null;
        }
    },
    byOptionalLong(arr(OptionalLong.class)) {
        @Override
        public Object convertTo(Object o) {
            if (o == null) {
                return null;
            }
            OptionalLong optional = (OptionalLong) o;
            return optional.isPresent() ? optional.getAsLong() : null;
        }
    },
    byOptionalDouble(arr(OptionalDouble.class)) {
        @Override
        public Object convertTo(Object o) {
            if (o == null) {
                return null;
            }
            OptionalDouble optional = (OptionalDouble) o;
            return optional.isPresent() ? (long) optional.getAsDouble() : null;
        }
    },
    byOptional(arr(Optional.class, com.moon.core.util.Optional.class)) {
        @Override
        public Object convertTo(Object o) {
            if (o == null) {
                return null;
            }
            if (o instanceof Optional) {
                Optional optional = (Optional) o;
                return optional.isPresent() ? (long) optional.get() : null;
            } else {
                com.moon.core.util.Optional optional = (com.moon.core.util.Optional) o;
                return optional.isPresent() ? (long) optional.get() : null;
            }
        }
    };

    private final Set<Class<?>> hashFromSupports;
    private final Set<Class<?>> hashToSupports;
    private final ToPrimitive toPrimitive;

    ToLong(Class[] supportsFrom) {
        Class<?>[] supportsTo = arr(Long.class);
        this.hashToSupports = unmodifiableHashSet(supportsTo);
        this.hashFromSupports = unmodifiableHashSet(supportsFrom);
        toPrimitive = new ToPrimitive(hashFromSupports, long.class, this, 0L);
    }

    @Override
    public Set<Class<?>> supportsTo() { return hashToSupports; }

    @Override
    public Set<Class<?>> supportsFrom() { return hashFromSupports; }

    @Override
    public boolean supportsTo(Class type) { return type == Long.class; }
}
