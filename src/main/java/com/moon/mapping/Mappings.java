package com.moon.mapping;

import static java.lang.Enum.valueOf;

/**
 * @author benshaoye
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Mappings {

    private final static String NS;

    static {
        NS = Mappings.class.getPackage().getName() + ".BM_";
    }

    private Mappings() {}

    private static Class toEnumCls(Class<?> cls) { return classAs(NS + under(clsName(cls))); }

    static Class classAs(String classname) {
        try {
            return Class.forName(classname);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(classname, e);
        }
    }

    static BeanMapper resolve(Class cls1, String cls2) {
        try {
            return (BeanMapper) valueOf(toEnumCls(cls1), "TO_" + under(cls2));
        } catch (Throwable e) {
            throw new NoSuchMappingException(clsName(cls1), cls2, e);
        }
    }

    static <F, T> BeanMapper<F, T> resolve(Class cls1, Class cls2) { return resolve(cls1, clsName(cls2)); }

    private static String under(String classname) { return classname.replace('.', '_'); }

    private static String clsName(Class<?> cls) { return cls.getCanonicalName(); }
}
