package com.moon.corex.model;

/**
 * @author benshaoye
 */
abstract class TypeUtils {

    private TypeUtils() {}

    public static String getSimpleName(String fullName) {
        int last = fullName.indexOf("<"), idx;
        if (last > 0) {
            idx = fullName.lastIndexOf('.', last);
        } else {
            idx = fullName.lastIndexOf('.');
            last = fullName.length();
        }
        return fullName.substring(Math.max(idx + 1, 0), last);
    }

    public static boolean isPrimitive(String type) {
        if (type == null) {
            return false;
        }
        switch (type) {
            case "char":
            case "byte":
            case "short":
            case "int":
            case "long":
            case "float":
            case "double":
            case "boolean":
                return true;
            default:
                return false;
        }
    }
}
