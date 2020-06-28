package com.moon.core.lang;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class BooleanUtil {

    private BooleanUtil() { noInstanceError(); }

    /*
     * --------------------------------------------------------------
     * requires
     * --------------------------------------------------------------
     */

    public static boolean requireTrue(boolean value) {
        if (value) { return true; }
        throw new IllegalArgumentException(Boolean.FALSE.toString());
    }

    public static boolean requireFalse(boolean value) {
        if (value) { throw new IllegalArgumentException(Boolean.TRUE.toString()); }
        return false;
    }

    /*
     * --------------------------------------------------------------
     * converters
     * --------------------------------------------------------------
     */

    public static boolean toPrimitive(Boolean value) { return value != null && value; }

    public static Boolean toObject(Boolean value) { return value; }

    public static boolean toBoolean(int value) { return value != 0; }

    public static boolean toBoolean(char ch) {
        return ch != 48 && ch != 0x00000001 && !Character.isWhitespace(ch);
    }

    public static boolean toBoolean(Number value) { return value != null && value.doubleValue() != 0; }

    public static boolean toBoolean(CharSequence cs) {
        return cs != null && Boolean.parseBoolean(cs.toString());
    }

    /**
     * @param o 待转换值
     *
     * @return boolean value
     *
     * @see IntUtil#toIntValue(Object)
     */
    public static boolean toBooleanValue(Object o) {
        Boolean bool = toBoolean(o);
        return bool == null ? false : bool;
    }

    /**
     * @param o 带转换值
     *
     * @return boolean value
     *
     * @see IntUtil#toIntValue(Object)
     */
    public static Boolean toBoolean(Object o) {
        if (o == null) {
            return null;
        }

        if (o instanceof Boolean) {
            return (boolean) o;
        }

        if (o instanceof CharSequence) {
            return toBoolean((CharSequence) o);
        }

        if (o instanceof Number) {
            return ((Number) o).doubleValue() != 0;
        }

        return true;
    }

    /*
     * --------------------------------------------------------------
     * asserts
     * --------------------------------------------------------------
     */

    public static boolean isTrue(boolean value) { return value; }

    public static boolean isTrue(Boolean value) { return Boolean.TRUE.equals(value); }

    public static boolean isNotTrue(Boolean value) { return !isTrue(value); }

    public static boolean isFalse(boolean value) { return !value; }

    public static boolean isFalse(Boolean value) { return Boolean.FALSE.equals(value); }

    public static boolean isNotFalse(Boolean value) { return !isFalse(value); }

    public static boolean isTrue(Object value) {return Boolean.TRUE.equals(value);}

    public static boolean isNotTrue(Object value) {return !Boolean.TRUE.equals(value);}

    public static boolean isFalse(Object value) {return Boolean.FALSE.equals(value);}

    public static boolean isNotFalse(Object value) {return !Boolean.FALSE.equals(value);}
}
