package com.moon.core.util.convert;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import static com.moon.core.util.convert.ConvertUtil.arr;
import static com.moon.core.util.convert.ConvertUtil.ifn;
import static com.moon.core.util.convert.ToBoolean.unmodifiableHashSet;

/**
 * @author benshaoye
 */
enum ToInt implements Converts {
    /**
     * Number -> int
     */
    byNumber(ConvertUtil.concat(Arrs.WRAPPER_NUMBERS, Arrs.EXPAND_NUMBERS)) {
        @Override
        public Object convert(Object o) { return ifn(o) ? 0 : ((Number) o).intValue(); }
    },
    /**
     * primitive number -> int
     */
    byNumberVal(Arrs.PRIMITIVE_NUMBERS) {
        @Override
        public Object convert(Object o) { return ((Number) o).intValue(); }
    },
    byBoolean(arr(Boolean.class, boolean.class)) {
        @Override
        public Object convert(Object o) { return ifn(o) ? 0 : ((Boolean) o ? 1 : 0); }
    },
    byChar(arr(Character.class, char.class)) {
        @Override
        public Object convert(Object o) { return ifn(o) ? 0 : (int) ((char) o); }
    },
    byString(Arrs.STRINGS) {
        @Override
        public Object convert(Object o) {
            return ifn(o) ? 0 : Integer.parseInt(o.toString());
        }
    },
    byOptionalInt(arr(OptionalInt.class)) {
        @Override
        public Object convert(Object o) {
            return ifn(o) ? 0 : ((OptionalInt) o).orElse(0);
        }
    },
    byOptional(arr(Optional.class)) {
        @Override
        public Object convert(Object o) {
            return ifn(o) ? 0 : ((Optional) o).orElse(0);
        }
    };

    private final Set<Class<?>> hashFromSupports;
    private final Set<Class<?>> hashToSupports;

    ToInt(Class[] supportsFrom) {
        this.hashFromSupports = unmodifiableHashSet(supportsFrom);
        Class<?>[] supportsTo = arr(int.class);
        this.hashToSupports = unmodifiableHashSet(supportsTo);
    }

    @Override
    public Set<Class<?>> supportsTo() { return hashToSupports; }

    @Override
    public Set<Class<?>> supportsFrom() { return hashFromSupports; }

    @Override
    public boolean supportsTo(Class type) { return type == int.class; }
}
