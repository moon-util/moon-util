package com.moon.mapping.processing;

/**
 * @author moonsky
 */
abstract class NamingUtils {

    private NamingUtils() { }

    static String format(String classname) { return classname.replace('.', '_'); }

    static String getBeanMappingEnumName(String classname) {
        return "BeanMapping_" + format(classname);
    }
}
