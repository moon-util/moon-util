package com.moon.mapping;

import java.util.Collection;
import java.util.function.Function;

import static java.lang.Enum.valueOf;

/**
 * @author benshaoye
 */
@SuppressWarnings("all")
final class Mappings {

    private final static String NAMESPACE;

    static {
        String packageName = Mappings.class.getPackage().getName();
        NAMESPACE = packageName + ".BeanMapping_";
    }

    private Mappings() {}

    final static Function<?, ?> DFT_CONVERTER = o -> o;

    static Class classAs(String classname) {
        try {
            return Class.forName(classname);
        } catch (Throwable t) {
            throw new IllegalStateException(classname);
        }
    }

    static <F, T> BeanMapping<F, T> resolve(Class cls1, Class cls2) {
        return resolve(cls1.getCanonicalName(), cls2.getCanonicalName());
    }

    static <T> MapMapping<T> resolve(Class<T> cls1) {
        return resolve(cls1.getCanonicalName());
    }

    static BeanMapping resolve(String cls1, String cls2) {
        try {
            return (BeanMapping) valueOf(toClass(cls1), "TO_" + toVarName(cls2));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    static BeanMapping resolve(String cls1) {
        try {
            return (BeanMapping) toClass(cls1).getEnumConstants()[0];
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    static int detectSize(Iterable iterable) {
        if (iterable == null) {
            return 0;
        }
        return iterable instanceof Collection ? ((Collection<?>) iterable).size() : 16;
    }

    private static Class toClass(String cls) throws ClassNotFoundException {
        return Class.forName(NAMESPACE + toVarName(cls));
    }

    private static String toVarName(String classname) {
        return classname.replace('.', '_');
    }
}
