package com.moon.mapping;

import java.util.Collection;

import static java.lang.Enum.valueOf;

/**
 * @author benshaoye
 */
public class Mappings {

    private final static String NAMESPACE;

    static {
        String packageName = Mappings.class.getPackage().getName();
        NAMESPACE = packageName + ".BeanMapping_";
    }

    private Mappings() {}

    static Class classAs(String classname) {
        try {
            return Class.forName(classname);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(classname, e);
        }
    }

    static BeanMapping resolve(String cls1, String cls2) {
        try {
            return (BeanMapping) valueOf(toClass(cls1), "TO_" + toName(cls2));
        } catch (ClassNotFoundException e) {
            throw new NoSuchMappingException(cls1, cls2);
        }
    }

    static BeanMapping resolve(String cls1) {
        try {
            return (BeanMapping) toClass(cls1).getEnumConstants()[0];
        } catch (ClassNotFoundException e) {
            throw new NoSuchMappingException(cls1, e);
        }
    }

    static <F, T> BeanMapping<F, T> resolve(Class cls1, Class cls2) {
        return resolve(toName(cls1), toName(cls2));
    }

    static <T> MapMapping<T> resolve(Class<T> cls1) { return resolve(toName(cls1)); }

    static int detectSize(Iterable iterable) {
        return iterable instanceof Collection//
            ? ((Collection<?>) iterable).size()//
            : (iterable == null ? 0 : 16);
    }

    private static Class toClass(String cls) throws ClassNotFoundException {
        return Class.forName(NAMESPACE + toName(cls));
    }

    private static String toName(String classname) {
        return classname.replace('.', '_');
    }

    private static String toName(Class<?> cls) { return cls.getCanonicalName(); }
}
