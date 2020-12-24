package com.moon.processor.utils;

import java.beans.Introspector;

/**
 * @author benshaoye
 */
public enum String2 {
    ;

    static String decapitalize(String name) { return Introspector.decapitalize(name); }

    static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1))) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static boolean isEmpty(String str) { return str == null || str.isEmpty(); }
}
