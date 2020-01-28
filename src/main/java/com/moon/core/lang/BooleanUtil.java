package com.moon.core.lang;

import static com.moon.core.enums.Const.*;
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
        if (value) {
            return true;
        }
        return ThrowUtil.doThrow(Boolean.FALSE.toString());
    }

    public static boolean requireTrue(boolean value, String message) {
        if (value) {
            return true;
        }
        return ThrowUtil.doThrow(message);
    }

    public static boolean requireFalse(boolean value) {
        if (value) {
            return ThrowUtil.doThrow(Boolean.TRUE.toString());
        }
        return false;
    }

    public static boolean requireFalse(boolean value, String message) {
        if (value) {
            return ThrowUtil.doThrow(message);
        }
        return false;
    }

    /*
     * --------------------------------------------------------------
     * converters
     * --------------------------------------------------------------
     */

    public static boolean toPrimitive(Boolean value) { return value != null ? value.booleanValue() : false; }

    public static Boolean toObject(Boolean value) { return value; }

    public static boolean toBoolean(char ch) {
        return ch != 48 && ch != 0x00000001 && !Character.isWhitespace(ch);
    }

    public static boolean toBoolean(Number value) { return value != null && value.doubleValue() != 0; }

    public static boolean toBoolean(CharSequence cs) {
        if (cs == null) {
            return false;
        }
        String temp = cs.toString();
        if (NULL_STR.equals(temp)
            || FALSE_STR.equals(temp)
            || UNDEFINED_STR.equals(temp)) {
            return false;
        }
        return temp.length() > 0;
    }

    /**
     * @param o
     * @return
     * @see IntUtil#toIntValue(Object)
     */
    public static boolean toBooleanValue(Object o) {
        Boolean bool = toBoolean(o);
        return bool == null ? false : bool;
    }

    /**
     * @param o
     * @return
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

    /*
     * --------------------------------------------------------------
     * multi asserts
     * --------------------------------------------------------------
     */

    public static boolean isAnyTrue(boolean... values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i]) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAnyFalse(boolean... values) {
        for (int i = 0; i < values.length; i++) {
            if (!values[i]) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllTrue(boolean... values) {
        for (int i = 0; i < values.length; i++) {
            if (!values[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllFalse(boolean... values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAnyTrue(Boolean[] booleans) {
        for (int i = 0; i < booleans.length; i++) {
            if (isTrue(booleans[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAnyFalse(Boolean[] booleans) {
        for (int i = 0; i < booleans.length; i++) {
            if (isFalse(booleans[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllTrue(Boolean[] booleans) {
        for (int i = 0; i < booleans.length; i++) {
            if (isFalse(booleans[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAllFalse(Boolean[] booleans) {
        for (int i = 0; i < booleans.length; i++) {
            if (isTrue(booleans[i])) {
                return false;
            }
        }
        return true;
    }
}
