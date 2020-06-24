package com.moon.core.lang;

import com.moon.core.exception.NumberException;
import com.moon.core.util.DetectUtil;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static java.lang.String.format;

/**
 * @author benshaoye
 */
public final class IntUtil {

    private IntUtil() {
        noInstanceError();
    }

    public static int requireEq(int value, int expect) {
        if (value == expect) {
            return value;
        }
        throw new NumberException(format("Expected: %d, Actual: %d", expect, value));
    }

    public static int requireEq(int value, int expect, String errorMsg) {
        if (value == expect) {
            return value;
        }
        throw new NumberException(errorMsg);
    }

    public static int requireGt(int value, int expect) {
        if (value > expect) {
            return value;
        }
        throw new NumberException(format("Expected great than %d, Actual: %d", expect, value));
    }

    public static int requireGt(int value, int expect, String errorMsg) {
        if (value > expect) {
            return value;
        }
        throw new NumberException(errorMsg);
    }

    public static int requireLt(int value, int expect) {
        if (value < expect) {
            return value;
        }
        throw new NumberException(format("Expected less than %d, Actual: %d", expect, value));
    }

    public static int requireLt(int value, int expect, String errorMsg) {
        if (value < expect) {
            return value;
        }
        throw new NumberException(errorMsg);
    }

    public static int requireGtOrEq(int value, int expect) {
        if (value >= expect) {
            return value;
        }
        throw new NumberException(format("Expected not less than %d, Actual: %d", expect, value));
    }

    public static int requireGtOrEq(int value, int expect, String errorMsg) {
        if (value >= expect) {
            return value;
        }
        throw new NumberException(errorMsg);
    }

    public static int requireLtOrEq(int value, int expect) {
        if (value <= expect) {
            return value;
        }
        throw new NumberException(format("Expected not great than %d, Actual: %d", expect, value));
    }

    public static int requireLtOrEq(int value, int expect, String errorMsg) {
        if (value <= expect) {
            return value;
        }
        throw new NumberException(errorMsg);
    }

    /**
     * 要求期望值在指定范围里，不包含范围边界
     *
     * @param value 待测值
     * @param min   最小值
     * @param max   最大值
     *
     * @return 是否在范围内
     */
    public static int requireInRange(int value, int min, int max) {
        requireGt(value, min);
        requireLt(value, max);
        return value;
    }

    public static int requireInRange(int value, int min, int max, String errorMsg) {
        requireGt(value, min, errorMsg);
        requireLt(value, max, errorMsg);
        return value;
    }

    /**
     * 要求期望值在指定范围里，包含范围边界
     *
     * @param value 待测值
     * @param min   最小值
     * @param max   最大值
     *
     * @return 是否在范围内
     */
    public static int requireBetween(int value, int min, int max) {
        requireGtOrEq(value, min);
        requireLtOrEq(value, max);
        return value;
    }

    public static int requireBetween(int value, int min, int max, String errorMsg) {
        requireGtOrEq(value, min, errorMsg);
        requireLtOrEq(value, max, errorMsg);
        return value;
    }


    public static boolean isInt(Object o) { return o instanceof Integer; }

    public static boolean matchInt(CharSequence o) { return DetectUtil.isInteger(String.valueOf(o)); }

    /*
     * -------------------------------------------------------------------------------------------
     * converter
     * -------------------------------------------------------------------------------------------
     */

    public static int toIntValue(Boolean bool) { return toIntValue(bool != null && bool); }

    public static int toIntValue(long value) { return (int) value; }

    public static int toIntValue(float value) { return (int) value; }

    public static int toIntValue(double value) { return (int) value; }

    public static int toIntValue(boolean value) { return value ? 1 : 0; }

    public static int toIntValue(CharSequence cs) {
        if (cs == null) {
            return 0;
        } else {
            String str = cs.toString().trim();
            return str.length() > 0 ? Integer.parseInt(str) : 0;
        }
    }

    /*
     * -------------------------------------------------------------------------------------------
     * defaulter
     * -------------------------------------------------------------------------------------------
     */

    /**
     * convert a CharSequence to int, if is an invalid CharSequence will return defaultVal
     *
     * @param cs         数字字符串
     * @param defaultVal 默认值
     *
     * @return 数值
     */
    public static int defaultIfInvalid(CharSequence cs, int defaultVal) {
        try {
            return toIntValue(cs);
        } catch (Exception e) {
            return defaultVal;
        }
    }

    /**
     * convert a CharSequence to int, if is an invalid CharSequence will return 0
     *
     * @param cs 数字字符串
     *
     * @return 数字
     */
    public static int zeroIfInvalid(CharSequence cs) {
        return defaultIfInvalid(cs, 0);
    }

    /**
     * convert a CharSequence to int, if is an invalid CharSequence will return 1
     *
     * @param cs 数字字符串
     *
     * @return 数字
     */
    public static int oneIfInvalid(CharSequence cs) {
        return defaultIfInvalid(cs, 1);
    }

    /*
     * -------------------------------------------------------------------------------------------
     * calculator
     * -------------------------------------------------------------------------------------------
     */

    public static int max(int... values) {
        int len = values.length;
        int ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] > ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static int min(int... values) {
        int len = values.length;
        int ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] < ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static int avg(int... values) {
        int ret = 0;
        int len = values.length;
        for (int i = 0; i < len; ret += values[i++]) {
        }
        return ret / len;
    }

    public static int sum(int... values) {
        int ret = 0;
        int len = values.length;
        for (int i = 0; i < len; i++) {
            ret += values[i];
        }
        return ret;
    }

    public static int multiply(int... values) {
        int ret = 1;
        int le = values.length;
        for (int i = 0; i < le; i++) {
            ret *= values[i];
        }
        return ret;
    }

    /**
     * 目前基本数据 Util 内类似的方法均使用了<strong>极大的容忍度</strong>
     * * 对于普通的转换均能得到预期结果；
     * 对于复杂对象（数组或集合，但不包括自定义对象）的转换需要熟悉方法内部逻辑；
     * * 如果对象 o 是一个集合或数组，当 o 只有一项时，返回这一项并且深度递归
     * * 否则返回这个集合或数组的尺寸（size 或 length）
     * <p>
     * boolean value = true;  // =============================== 1
     * boolean value = false;  // ============================== 0
     * char value = 'a';  // =================================== 97
     * byte value = 1;  // ===================================== 1
     * int value = 1;  // ====================================== 1
     * short value = 1;  // ==================================== 1
     * long value = 1L;  // ==================================== 1
     * float value = 1F;  // =================================== 1
     * double value = 1F;  // ================================== 1
     * String value = "1";  // ================================= 1
     * StringBuffer value = new StringBuffer("1");  // ========= 1
     * StringBuilder value = new StringBuilder("1");  // ======= 1
     * String value = "  1   ";  // ============================ 1
     * StringBuffer value = new StringBuffer("  1   ");  // ==== 1
     * StringBuilder value = new StringBuilder("  1   ");  // == 1
     * BigDecimal value = new BigDecimal("1");  // ============= 1
     * BigInteger value = new BigInteger("1");  // ============= 1
     * Collection value = new ArrayList(){{put(1)}};  // ======= 1（只有一项时）
     * Collection value = new HashSet(){{put(1)}};  // ========= 1（只有一项时）
     * Collection value = new TreeSet(){{put(1)}};  // ========= 1（只有一项时）
     * Collection value = new LinkedList(){{put(1)}};  // ====== 1（只有一项时）
     * Map value = new HashMap(){{put("key", 1)}};  // ========= 1（只有一项时）
     * <p>
     * int[] value = {1, 2, 3, 4};  // ======================================== 4（大于一项时，返回 size）
     * String[] value = {"1", "1", "1", "1"};  // ============================= 4（大于一项时，返回 size）
     * Collection value = new ArrayList(){{put(1);put(1);put(1);}};  // ======= 3（大于一项时，返回 size）
     * Map value = new HashMap(){{put("key", 1);put("name", 2);}};  // ======== 2（大于一项时，返回 size）
     * <p>
     * int result = IntUtil.toIntValue(value);
     *
     * @param o 待转换值
     *
     * @return 转换后的值
     *
     * @see BooleanUtil#toBoolean(Object)
     * @see BooleanUtil#toBooleanValue(Object)
     * @see CharUtil#toCharValue(Object)
     * @see CharacterUtil#toCharacter(Object)
     * @see ByteUtil#toByteValue(Object)
     * @see ByteUtil#toByte(Object)
     * @see ShortUtil#toShort(Object)
     * @see ShortUtil#toShortValue(Object)
     * @see IntegerUtil#toInteger(Object)
     * @see LongUtil#toLongValue(Object)
     * @see LongUtil#toLong(Object)
     * @see FloatUtil#toFloat(Object)
     * @see FloatUtil#toFloatValue(Object)
     * @see DoubleUtil#toDoubleValue(Object)
     * @see DoubleUtil#toDouble(Object)
     */
    public static int toIntValue(Object o) {
        if (o == null) {
            return 0;
        }
        if (o instanceof Integer) {
            return (Integer) o;
        }
        if (o instanceof Number) {
            return ((Number) o).intValue();
        }
        if (o instanceof CharSequence) {
            return toIntValue((CharSequence) o);
        }
        if (o instanceof Character) {
            return ((Character) o).charValue();
        }
        if (o instanceof Boolean) {
            return (boolean) o ? 1 : 0;
        }
        try {
            return toIntValue(SupportUtil.onlyOneItemOrSize(o));
        } catch (Exception e) {
            throw new IllegalArgumentException(format("Can not cast to int of: %s", o), e);
        }
    }
}
