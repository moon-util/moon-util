package com.moon.core.lang;

import com.moon.core.exception.NumberException;
import com.moon.core.util.DateUtil;
import com.moon.core.util.DetectUtil;

import java.util.Calendar;
import java.util.Date;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static java.lang.String.format;

/**
 * @author benshaoye
 */
public final class LongUtil {
    private LongUtil() {
        noInstanceError();
    }

    public static long requireEq(long value, long expect) {
        if (value == expect) {
            return value;
        }
        throw new NumberException(format("Expected: %d, Actual: %d", expect, value));
    }

    public static long requireEq(long value, long expect, String errorMsg) {
        if (value == expect) {
            return value;
        }
        throw new NumberException(errorMsg);
    }

    public static long requireGt(long value, long expect) {
        if (value > expect) {
            return value;
        }
        throw new NumberException(format("Expected great than %d, Actual: %d", expect, value));
    }

    public static long requireGt(long value, long expect, String errorMsg) {
        if (value > expect) {
            return value;
        }
        throw new NumberException(errorMsg);
    }

    public static long requireLt(long value, long expect) {
        if (value < expect) {
            return value;
        }
        throw new NumberException(format("Expected less than %d, Actual: %d", expect, value));
    }

    public static long requireLt(long value, long expect, String errorMsg) {
        if (value < expect) {
            return value;
        }
        throw new NumberException(errorMsg);
    }

    public static long requireGtOrEq(long value, long expect) {
        if (value >= expect) {
            return value;
        }
        throw new NumberException(format("Expected not less than %d, Actual: %d", expect, value));
    }

    public static long requireGtOrEq(long value, long expect, String errorMsg) {
        if (value >= expect) {
            return value;
        }
        throw new NumberException(errorMsg);
    }

    public static long requireLtOrEq(long value, long expect) {
        if (value <= expect) {
            return value;
        }
        throw new NumberException(format("Expected not great than %d, Actual: %d", expect, value));
    }

    public static long requireLtOrEq(long value, long expect, String errorMsg) {
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
    public static long requireInRange(long value, long min, long max) {
        requireGt(value, min);
        requireLt(value, max);
        return value;
    }

    public static long requireInRange(long value, long min, long max, String errorMsg) {
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
    public static long requireBetween(long value, long min, long max) {
        requireGtOrEq(value, min);
        requireLtOrEq(value, max);
        return value;
    }

    public static long requireBetween(long value, long min, long max, String errorMsg) {
        requireGtOrEq(value, min, errorMsg);
        requireLtOrEq(value, max, errorMsg);
        return value;
    }

    public static long oneIfInvalid(CharSequence cs) { return defaultIfInvalid(cs, 1); }

    public static long zeroIfInvalid(CharSequence cs) { return defaultIfInvalid(cs, 0); }

    /**
     * convert a CharSequence to int, if is an invalid CharSequence will return defaultVal
     *
     * @param cs
     * @param defaultVal
     * @return
     */
    public static long defaultIfInvalid(CharSequence cs, long defaultVal) {
        try {
            return Long.parseLong(cs.toString());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public static boolean isLong(Object o) { return o != null && o.getClass() == Long.class; }

    public static boolean matchLong(Object o) { return DetectUtil.isNumber(String.valueOf(o)); }

    public static Long toLong(Boolean bool) { return bool == null ? null : Long.valueOf((bool ? 1 : 0)); }

    public static Long toLong(Character value) { return value == null ? null : Long.valueOf(value); }

    public static Long toLong(Byte value) { return value == null ? null : value.longValue(); }

    public static Long toLong(Short value) { return value == null ? null : value.longValue(); }

    public static Long toLong(Long value) { return value == null ? null : value.longValue(); }

    public static Long toLong(Float value) { return value == null ? null : value.longValue(); }

    public static Long toLong(Double value) { return value == null ? null : value.longValue(); }

    public static Long toLong(CharSequence cs) { return cs == null ? null : Long.parseLong(cs.toString()); }

    /**
     * 目前基本数据 Util 内类似的方法均使用了<strong>极大的容忍度</strong>
     * * 对于普通的转换均能得到预期结果；
     * 对于复杂对象（数组或集合，但不包括自定义对象）的转换需要熟悉方法内部逻辑；
     * * 如果对象 o 是一个集合或数组，当 o 只有一项时，返回这一项并且深度递归
     * * 否则返回这个集合或数组的尺寸（size 或 length）
     * <p>
     * Object value = null;  // ===============================> null
     * boolean value = true;  // ==============================> 1
     * boolean value = false;  // =============================> 0
     * char value = 'a';  // ==================================> 97
     * byte value = 1;  // ====================================> 1
     * int value = 1;  // =====================================> 1
     * short value = 1;  // ===================================> 1
     * long value = 1L;  // ===================================> 1
     * float value = 1F;  // ==================================> 1
     * double value = 1F;  // =================================> 1
     * String value = "1";  // ================================> 1
     * StringBuffer value = new StringBuffer("1");  // ========> 1
     * StringBuilder value = new StringBuilder("1");  // ======> 1
     * String value = "  1   ";  // ===========================> 1
     * StringBuffer value = new StringBuffer("  1   ");  // ===> 1
     * StringBuilder value = new StringBuilder("  1   ");  // => 1
     * BigDecimal value = new BigDecimal("1");  // ============> 1
     * BigInteger value = new BigInteger("1");  // ============> 1
     * Collection value = new ArrayList(){{increment(1)}};  // ======> 1（只有一项时）
     * Collection value = new HashSet(){{increment(1)}};  // ========> 1（只有一项时）
     * Collection value = new TreeSet(){{increment(1)}};  // ========> 1（只有一项时）
     * Collection value = new LinkedList(){{increment(1)}};  // =====> 1（只有一项时）
     * Map value = new HashMap(){{put("key", 1)}};  // ========> 1（只有一项时）
     * <p>
     * int[] value = {1, 2, 3, 4};  // =======================================> 4（大于一项时，返回 size）
     * String[] value = {"1", "1", "1", "1"};  // ============================> 4（大于一项时，返回 size）
     * Collection value = new ArrayList(){{increment(1);increment(1);increment(1);}};  // ======> 3（大于一项时，返回 size）
     * Map value = new HashMap(){{put("key", 1);put("name", 2);}};  // =======> 2（大于一项时，返回 size）
     * <p>
     * Long result = LongUtil.toLong(value);
     *
     * @param object
     * @return
     * @see IntUtil#toIntValue(Object)
     */
    public static Long toLong(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Long) {
            return (Long) object;
        }
        if (object instanceof Number) {
            return ((Number) object).longValue();
        }
        if (object instanceof CharSequence) {
            try {
                return Long.parseLong(object.toString().trim());
            } catch (NumberFormatException e) {
                try {
                    return DateUtil.toCalendar(object).getTimeInMillis();
                } catch (IllegalArgumentException ae) {
                    ae.initCause(e);
                    throw ae;
                }
            }
        }
        if (object instanceof Boolean) {
            return Long.valueOf((boolean) object ? 1 : 0);
        }
        if (object instanceof Date) {
            return ((Date) object).getTime();
        }
        if (object instanceof Calendar) {
            return ((Calendar) object).getTimeInMillis();
        }
        try {
            Object firstItem = SupportUtil.onlyOneItemOrSize(object);
            return toLong(firstItem);
        } catch (Exception e) {
            throw new IllegalArgumentException(format("Can not cast to int of: %s", String.valueOf(object)), e);
        }
    }

    /**
     * @param value
     * @return
     * @see IntUtil#toIntValue(Object)
     * @see #toLongValue(Object)
     */
    public static long toLongValue(Object value) {
        Long result = toLong(value);
        return result == null ? 0 : result.longValue();
    }

    public static long avg(long... values) {
        long ret = 0;
        int len = values.length;
        for (int i = 0; i < len; ret += values[i++]) {
        }
        return ret / len;
    }

    public static Long avg(Long... values) {
        long ret = 0;
        int len = values.length;
        for (int i = 0; i < len; i++) {
            ret += values[i];
        }
        return ret / len;
    }

    public static Long avgIgnoreNull(Long... values) {
        long ret = 0;
        Long temp;
        int count = 0;
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

    public static long sum(long... values) {
        long ret = 0;
        int len = values.length;
        for (int i = 0; i < len; i++) {
            ret += values[i];
        }
        return ret;
    }

    public static Long sum(Long[] values) {
        long ret = 0;
        int len = values.length;
        for (int i = 0; i < len; i++) {
            ret += values[i];
        }
        return ret;
    }

    public static Long sumIgnoreNull(Long... values) {
        long ret = 0;
        Long temp;
        int len = values.length;
        for (int i = 0; i < len; i++) {
            temp = values[i];
            if (temp != null) {
                ret += temp;
            }
        }
        return ret;
    }

    public static Long multiply(Long[] values) {
        long ret = 1;
        int len = values.length;
        for (int i = 0; i < len; i++) {
            ret *= values[i];
        }
        return ret;
    }

    public static long multiply(long... values) {
        long ret = 1;
        int le = values.length;
        for (int i = 0; i < le; i++) {
            ret *= values[i];
        }
        return ret;
    }

    public static Long multiplyIgnoreNull(Long... values) {
        long ret = 1;
        int len = values.length;
        Long tmp;
        for (int i = 0; i < len; i++) {
            tmp = values[i];
            if (tmp != null) {
                ret *= tmp;
            }
        }
        return ret;
    }

    public static long max(long... values) {
        int len = values.length;
        long ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] > ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static Long max(Long[] values) {
        int len = values.length;
        long ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] > ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static Long maxIgnoreNull(Long... values) {
        int len = values.length;
        long ret = values[0];
        Long tmp;
        for (int i = 1; i < len; i++) {
            tmp = values[i];
            if (tmp != null && tmp > ret) {
                ret = tmp;
            }
        }
        return ret;
    }

    public static Long min(long... values) {
        int len = values.length;
        long ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] < ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static Long min(Long[] values) {
        int len = values.length;
        long ret = values[0];
        for (int i = 1; i < len; i++) {
            if (values[i] < ret) {
                ret = values[i];
            }
        }
        return ret;
    }

    public static Long minIgnoreNull(Long... values) {
        int len = values.length;
        long ret = values[0];
        Long tmp;
        for (int i = 1; i < len; i++) {
            tmp = values[i];
            if (tmp != null && tmp < ret) {
                ret = tmp;
            }
        }
        return ret;
    }
}
