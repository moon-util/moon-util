package com.moon.mapping;

import static java.lang.Enum.valueOf;

/**
 * @author benshaoye
 */
final class Mappings {

    private final static String NAMESPACE;

    static {
        String packageName = Mappings.class.getPackage().getName();
        NAMESPACE = packageName + ".MoonBeanMappingsGenerated_";
    }

    private Mappings() {}

    @SuppressWarnings("all")
    static <F, T> BeanMapping<F, T> resolve(Class cls1, Class cls2) {
        return resolve(cls1.getName(), cls2.getName());
    }

    @SuppressWarnings("all")
    static <T> MapMapping<T> resolve(Class<T> cls1) {
        return resolve(cls1.getName());
    }

    @SuppressWarnings("all")
    static BeanMapping resolve(String cls1, String cls2) {
        try {
            return (BeanMapping) valueOf(toClass(cls1), toVarName(cls2));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("all")
    static BeanMapping resolve(String cls1) {
        try {
            return (BeanMapping) toClass(cls1).getEnumConstants()[0];
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Class toClass(String cls) throws ClassNotFoundException {
        return Class.forName(NAMESPACE + toVarName(cls));
    }

    private static String toVarName(String classname) {
        return classname.replace('.', '_');
    }
}
