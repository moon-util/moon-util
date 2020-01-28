package com.moon.core.lang;

import com.moon.core.exception.NumberException;
import com.moon.core.util.DetectUtil;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static java.lang.String.format;

/**
 * @author benshaoye
 */
public final class DoubleUtil {
    private DoubleUtil() {
        noInstanceError();
    }


    public static double requireEq(double value, double expect) {
        if (value == expect) {
            return value;
        }
        throw new NumberException(format("Expected: %f, Actual: %f", expect, value));
    }


    public static double requireEq(double value, double expect, String errorMsg) {
        if (value == expect) {
            return value;
        }
        throw new NumberException(errorMsg);
    }

    public static double requireGt(double value, double expect) {
        if (value > expect) {
            return value;
        }
        throw new NumberException(format("Expected great than %f, Actual: %f", expect, value));
    }

    public static double requireGt(double value, double expect, String errorMsg) {
        if (value > expect) {
            return value;
        }
        throw new NumberException(errorMsg);
    }

    public static double requireLt(double value, double expect) {
        if (value < expect) {
            return value;
        }
        throw new NumberException(format("Expected less than %f, Actual: %f", expect, value));
    }

    public static double requireLt(double value, double expect, String errorMsg) {
        if (value < expect) {
            return value;
        }
        throw new NumberException(errorMsg);
    }

    public static double requireGtOrEq(double value, double expect) {
        if (value >= expect) {
            return value;
        }
        throw new NumberException(format("Expected not less than %f, Actual: %f", expect, value));
    }

    public static double requireGtOrEq(double value, double expect, String errorMsg) {
        if (value >= expect) {
            return value;
        }
        throw new NumberException(errorMsg);
    }

    public static double requireLtOrEq(double value, double expect) {
        if (value <= expect) {
            return value;
        }
        throw new NumberException(format("Expected not great than %f, Actual: %f", expect, value));
    }

    public static double requireLtOrEq(double value, double expect, String errorMsg) {
        if (value <= expect) {
            return value;
        }
        throw new NumberException(errorMsg);
    }

    /**
     * 要求期望值在指定范围里，不包含范围边界
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static double requireInRange(double value, double min, double max) {
        requireGt(value, min);
        requireLt(value, max);
        return value;
    }

    public static double requireInRange(double value, double min, double max, String errorMsg) {
        requireGt(value, min, errorMsg);
        requireLt(value, max, errorMsg);
        return value;
    }

    /**
     * 要求期望值在指定范围里，包含范围边界
     *
     * @param value
     * @param min
     * @param max
     * @return
     */
    public static double requireBetween(double value, double min, double max) {
        requireGtOrEq(value, min);
        requireLtOrEq(value, max);
        return value;
    }

    public static double requireBetween(double value, double min, double max, String errorMsg) {
        requireGtOrEq(value, min, errorMsg);
        requireLtOrEq(value, max, errorMsg);
        return value;
    }

    public static boolean isDouble(Object obj) {
        return obj != null && obj.getClass() == Double.class;
    }

    public static boolean matchDouble(CharSequence obj) {
        return DetectUtil.isDouble(String.valueOf(obj));
    }

    public static Double toDouble(double value) {
        return Double.valueOf(value);
    }

    public static Double toDouble(Byte value) {
        return value == null ? null : value.doubleValue();
    }

    public static Double toDouble(Short value) {
        return value == null ? null : value.doubleValue();
    }

    public static Double toDouble(Integer value) {
        return value == null ? null : value.doubleValue();
    }

    public static Double toDouble(Long value) {
        return value == null ? null : value.doubleValue();
    }

    public static Double toDouble(Float value) {
        return value == null ? null : value.doubleValue();
    }

    public static Double toDouble(Boolean value) {
        return value == null ? null : Double.valueOf((value.booleanValue() ? 1 : 0));
    }

    public static Double toDouble(Character value) {
        return value == null ? null : Double.valueOf(value.charValue());
    }

    /**
     * 目前基本数据 Util 内类似的方法均使用了<strong>极大的容忍度</strong>
     * * 对于普通的转换均能得到预期结果；
     * 对于复杂对象（数组或集合，但不包括自定义对象）的转换需要熟悉方法内部逻辑；
     * * 如果对象 o 是一个集合或数组，当 o 只有一项时，返回这一项并且深度递归
     * * 否则返回这个集合或数组的尺寸（size 或 length）
     * <p>
     * Object value = null;  // ================================ null
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
     * Collection value = new ArrayList(){{increment(1)}};  // ======= 1（只有一项时）
     * Collection value = new HashSet(){{increment(1)}};  // ========= 1（只有一项时）
     * Collection value = new TreeSet(){{increment(1)}};  // ========= 1（只有一项时）
     * Collection value = new LinkedList(){{increment(1)}};  // ====== 1（只有一项时）
     * Map value = new HashMap(){{put("key", 1)}};  // =============== 1（只有一项时）
     * <p>
     * int[] value = {1, 2, 3, 4};  // ======================================== 4（大于一项时，返回 size）
     * String[] value = {"1", "1", "1", "1"};  // ============================= 4（大于一项时，返回 size）
     * Collection value = new ArrayList(){{increment(1);increment(1);increment(1);}};  // ====== 3（大于一项时，返回 size）
     * Map value = new HashMap(){{put("key", 1);put("name", 2);}};  // ======== 2（大于一项时，返回 size）
     * <p>
     * Double result = DoubleUtil.toDouble(value);
     *
     * @param value
     * @return
     * @see IntUtil#toIntValue(Object)
     * @see #toDoubleValue(Object)
     */
    public static Double toDouble(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof CharSequence) {
            return Double.parseDouble(value.toString().trim());
        }
        if (value instanceof Boolean) {
            return Double.valueOf((((Boolean) value).booleanValue() ? 1 : 0));
        }
        try {
            return toDouble(SupportUtil.onlyOneItemOrSize(value));
        } catch (Exception e) {
            throw new IllegalArgumentException(format("Can not cast to double of: %s", value), e);
        }
    }

    /**
     * @param value
     * @return
     * @see IntUtil#toIntValue(Object)
     * @see #toDouble(Object)
     */
    public static double toDoubleValue(Object value) {
        Double result = toDouble(value);
        return result == null ? 0 : result.doubleValue();
    }

    public static double max(double... values) {
        int len = values.length;
        double ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] > ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static double min(double... values) {
        int len = values.length;
        double ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] < ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static double avg(double... values) {
        int len = values.length;
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum += values[i];
        }
        return sum / len;
    }

    public static double sum(double... values) {
        int len = values.length;
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum += values[i];
        }
        return sum;
    }

    public static double multiply(double... values) {
        int len = values.length;
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum *= values[i];
        }
        return sum;
    }

    public static Double avg(Double[] values) {
        Double ret = Double.valueOf(0);
        int len = values.length;
        for (int i = 0; i < len; i++) {
            ret += values[i];
        }
        return ret / len;
    }

    public static Double avgIgnoreNull(Double... values) {
        int ret = 0;
        Double temp;
        double count = 0;
        int len = values.length;
        for (int i = 0; i < len; i++) {
            temp = values[i];
            if (temp != null) {
                ret += temp;
                count++;
            }
        }
        return ret / count;
    }

    public static Double sum(Double[] values) {
        Double ret = Double.valueOf(0);
        int len = values.length;
        for (int i = 0; i < len; i++) {
            ret += values[i];
        }
        return ret;
    }

    public static Double sumIgnoreNull(Double... values) {
        Double ret = Double.valueOf(0);
        Double temp;
        int len = values.length;
        for (int i = 0; i < len; i++) {
            temp = values[i];
            if (temp != null) {
                ret += temp;
            }
        }
        return ret;
    }

    public static Double multiply(Double[] values) {
        Double ret = Double.valueOf(1);
        int len = values.length;
        for (int i = 0; i < len; i++) {
            ret *= values[i];
        }
        return ret;
    }

    public static Double multiplyIgnoreNull(Double... values) {
        Double ret = Double.valueOf(1);
        int len = values.length;
        Double tmp;
        for (int i = 0; i < len; i++) {
            tmp = values[i];
            if (tmp != null) {
                ret *= tmp;
            }
        }
        return ret;
    }

    public static Double max(Double[] values) {
        int len = values.length;
        Double ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] > ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static Double maxIgnoreNull(Double... values) {
        int len = values.length;
        Double ret = values[0];
        Double tmp;
        for (int i = 1; i < len; i++) {
            tmp = values[i];
            if (tmp != null && tmp > ret) {
                ret = tmp;
            }
        }
        return ret;
    }

    public static Double min(Double[] values) {
        int len = values.length;
        Double ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] < ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static Double minIgnoreNull(Double... values) {
        int len = values.length;
        Double ret = values[0];
        Double tmp;
        for (int i = 1; i < len; i++) {
            tmp = values[i];
            if (tmp != null && tmp < ret) {
                ret = tmp;
            }
        }
        return ret;
    }
}
