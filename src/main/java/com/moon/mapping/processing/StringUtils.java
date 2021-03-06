package com.moon.mapping.processing;

import java.beans.Introspector;
import java.util.Arrays;

/**
 * @author moonsky
 */
abstract class StringUtils {

    private StringUtils() { }

    static String underscore(String classname) { return classname.replace('.', '_'); }

    static String toMappingClassname(String classname) {
        return "BM_" + underscore(classname);
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

    static boolean isEmpty(String str) { return str == null || str.length() == 0; }

    static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return strLen == 0;
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
        return strLen != 0;
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

    static int count(String source, String search) {
        if (source == null) {
            return 0;
        }
        int startIdx = 0, count = 0, searchLen = search.length();
        do {
            int idx = source.indexOf(search, startIdx);
            if (idx < 0) {
                return count;
            }
            startIdx = idx + searchLen;
            count++;
        } while (true);
    }

    static String[] getClassnamesOrEmpty(Class<?>... classes) {
        return classes == null ? new String[0] : Arrays.stream(classes)
            .map(Class::getCanonicalName)
            .toArray(String[]::new);
    }

    static String strWrapped(String str) { return '"' + str + '"'; }

    static String bracketed(String str) { return "(" + str + ")"; }

    static boolean isPrimitiveLt(String thisPrimitiveType, String thatPrimitiveType) {
        String all = "byte,short,int,long,float,double";
        int thisIdx = all.indexOf(thisPrimitiveType);
        int thatIdx = all.indexOf(thatPrimitiveType);
        return thisIdx < thatIdx;
    }

    static boolean isPrimitiveGt(String thisPrimitiveType, String thatPrimitiveType) {
        String all = "byte,short,int,long,float,double";
        int thisIdx = all.indexOf(thisPrimitiveType);
        int thatIdx = all.indexOf(thatPrimitiveType);
        return thisIdx > thatIdx;
    }

    static boolean isPrimitiveNotGt(String thisType, String thatType) {
        String all = "byte,short,int,long,float,double";
        int thisIdx = all.indexOf(thisType);
        int thatIdx = all.indexOf(thatType);
        return thisIdx <= thatIdx;
    }

    static boolean isPrimitiveNotLt(String thisType, String thatType) {
        String all = "byte,short,int,long,float,double";
        int thisIdx = all.indexOf(thisType);
        int thatIdx = all.indexOf(thatType);
        return thisIdx >= thatIdx;
    }

    static boolean isPrimitive(String type) {
        return isPrimitiveNumber(type) || "char".equals(type) || "boolean".equals(type);
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
                return Integer.class.getCanonicalName();
            case "char":
                return Character.class.getCanonicalName();
            case "byte":
            case "short":
            case "long":
            case "float":
            case "double":
            case "boolean":
                return ("java.lang." + capitalize(type));
            default:
                return type;
        }
    }
}
