package com.moon.core.util;

import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.WeakAccessor;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * 通用类型转换器
 *
 * @author benshaoye
 * @date 2018/9/11
 */
public final class TypeUtil {
    /**
     * can not getSheet a instance of TypeUtil.class
     */
    private TypeUtil() { ThrowUtil.noInstanceError(); }

    /**
     * default converter CACHE or create new one
     */
    private final static WeakAccessor<TypeConverter> accessor = WeakAccessor.of(UnmodifiableTypeConverter::new);

    /**
     * return a default type converter
     *
     * @return
     */
    public final static TypeConverter cast() { return accessor.get(); }

    /**
     * get a default type converter
     *
     * @return
     */
    public final static TypeConverter of() { return of(GenericTypeConverter::new); }

    /**
     * get a default type converter
     *
     * @param supplier
     * @return
     */
    public final static TypeConverter of(Supplier<TypeConverter> supplier) { return supplier.get(); }

    /**
     * o is instance of clazz
     *
     * @param o
     * @param clazz
     * @return
     */
    public final static boolean instanceOf(Object o, Class clazz) { return clazz.isInstance(o); }

    /**
     * can not modify converter or increment new converter
     */
    private final static class UnmodifiableTypeConverter extends GenericTypeConverter {

        final static String ERROR = "Can't add new converter or modify on default converter";

        UnmodifiableTypeConverter() { super(); }

        @Override
        public <C> TypeConverter register(Class<C> toType, BiFunction<Object, Class<C>, ? extends C> func) {
            throw new UnsupportedOperationException(ERROR);
        }

        @Override
        public <C> TypeConverter registerIfAbsent(Class<C> toType, BiFunction<Object, Class<C>, ? extends C> func) {
            throw new UnsupportedOperationException(ERROR);
        }
    }
}
