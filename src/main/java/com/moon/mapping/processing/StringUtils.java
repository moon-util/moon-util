package com.moon.mapping.processing;

import java.beans.Introspector;

/**
 * @author moonsky
 */
abstract class StringUtils {

    private StringUtils() { }

    static String concat(Object... values) {
        StringBuilder str = new StringBuilder();
        for (Object value : values) {
            str.append(value);
        }
        return str.toString();
    }

    static String underscore(String classname) { return classname.replace('.', '_'); }

    static String toMappingClassname(String classname) {
        return "BeanMapping_" + underscore(classname);
    }

    static String toTargetClassname(String classname) {
        return "TO_" + underscore(classname);
    }

    static String decapitalize(String name) {
        return Introspector.decapitalize(name);
    }

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

    static boolean isPrimitive(String type) {
        if (type == null) {
            return false;
        }
        switch (type) {
            case "byte":
            case "short":
            case "int":
            case "long":
            case "float":
            case "double":
            case "boolean":
            case "char":
                return true;
            default:
                return false;
        }
    }

    static String toWrappedType(String type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case "int":
                return "Integer";
            case "char":
                return "Character";
            case "byte":
            case "short":
            case "long":
            case "float":
            case "double":
            case "boolean":
                return capitalize(type);
            default:
                return type;
        }
    }
}
