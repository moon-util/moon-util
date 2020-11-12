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

    static boolean isNotBlank(String str) {
        if (str == null) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    static String format(boolean appendIfOver, String template, Object... values) {
        if (values != null) {
            for (Object value : values) {
                if (template.contains("{}")) {
                    template = template.replaceFirst("\\{\\}", value == null ? "null" : value.toString());
                } else if (appendIfOver) {
                    template += ", " + value;
                }
            }
        }
        return template;
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

    static boolean isPrimitiveChar(String type) { return "char".equals(type); }

    static boolean isPrimitiveBoolean(String type) { return "boolean".equals(type); }

    static boolean isPrimitiveNumber(String type) {
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
                return true;
            case "boolean":
            case "char":
            default:
                return false;
        }
    }

    static boolean isWrappedNumber(String type) {
        if (type == null) {
            return false;
        }
        switch (type) {
            case "java.lang.Byte":
            case "java.lang.Short":
            case "java.lang.Integer":
            case "java.lang.Long":
            case "java.lang.Float":
            case "java.lang.Double":
                return true;
            default:
                return false;
        }
    }

    static String toPrimitiveType(String type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case "Integer":
            case "java.lang.Integer":
                return "int";
            case "Character":
            case "java.lang.Character":
                return "char";
            case "Byte":
            case "java.lang.Byte":
            case "Short":
            case "java.lang.Short":
            case "Long":
            case "java.lang.Long":
            case "Float":
            case "java.lang.Float":
            case "Double":
            case "java.lang.Double":
            case "Boolean":
            case "java.lang.Boolean":
                int dotIdx = type.lastIndexOf('.');
                return (dotIdx < 0 ? type : type.substring(dotIdx + 1)).toLowerCase();
            default:
                return type;
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
