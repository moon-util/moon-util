package com.moon.core.lang;

import com.moon.core.enums.Arrays2;
import com.moon.core.enums.TimeZones;
import com.moon.core.util.DateUtil;
import com.moon.core.util.TestUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

import static com.moon.core.lang.IntUtil.DIGITS;
import static com.moon.core.lang.IntUtil.TEN;
import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static java.lang.String.format;

/**
 * @author moonsky
 */
public final class LongUtil {

    private LongUtil() { noInstanceError(); }

    public static long oneIfInvalid(CharSequence cs) { return defaultIfInvalid(cs, 1); }

    public static long zeroIfInvalid(CharSequence cs) { return defaultIfInvalid(cs, 0); }

    /**
     * convert a CharSequence to int, if is an invalid CharSequence will return defaultVal
     *
     * @param cs
     * @param defaultVal
     *
     * @return
     */
    public static long defaultIfInvalid(CharSequence cs, long defaultVal) {
        try {
            Long value = toLong(cs);
            return value == null ? defaultVal : value;
        } catch (Throwable t) {
            return defaultVal;
        }
    }

    public static boolean isLong(Object o) { return o != null && o.getClass() == Long.class; }

    public static boolean matchLong(Object o) { return TestUtil.isIntegerValue(String.valueOf(o)); }

    public static Long toLong(Boolean bool) { return bool == null ? null : (long) (bool ? 1 : 0); }

    public static Long toLong(Character value) {
        return value == null ? null : (TestUtil.isDigit(value.toString()) ? toLong(value.toString()) : Long.valueOf(
            value));
    }

    public static Long toLong(Number value) { return value == null ? null : value.longValue(); }

    public static Long toLong(CharSequence cs) {
        if (cs == null) {
            return null;
        }
        try {
            return Long.parseLong(cs.toString().trim());
        } catch (NumberFormatException e) {
            try {
                return DateUtil.toCalendar(cs).getTimeInMillis();
            } catch (IllegalArgumentException ae) {
                ae.initCause(e);
                throw ae;
            }
        }
    }

    public static long toLongValue(Date date) { return date.getTime(); }

    public static long toLongValue(Calendar calendar) { return calendar.getTimeInMillis(); }

    public static long toLongValue(Boolean value) {
        return value == null ? 0 : (value ? 1 : 0);
    }

    public static long toLongValue(CharSequence cs) {
        return cs == null ? 0 : toLong(cs);
    }

    public static long toLongValue(Number value) { return value == null ? 0 : toLong(value); }

    public static long toLongValueAtToday(LocalTime time) { return toLong(LocalDateTime.of(LocalDate.now(), time)); }

    public static long toLongValue(LocalDate date) { return toLong(date.atStartOfDay()); }

    public static long toLongValue(LocalDateTime date) {
        return date.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
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
     * Long result = LongUtil.toLong(value);
     *
     * @param object
     *
     * @return
     *
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
            return toLong((Number) object);
        }
        if (object instanceof CharSequence) {
            return toLongValue(object.toString());
        }
        if (object instanceof Boolean) {
            return toLong((Boolean) object);
        }
        if (object instanceof Date) {
            return toLongValue((Date) object);
        }
        if (object instanceof Calendar) {
            return toLongValue((Calendar) object);
        }
        try {
            Object firstItem = SupportUtil.onlyOneItemOrSize(object);
            return toLong(firstItem);
        } catch (Exception e) {
            throw new IllegalArgumentException(format("Can not cast to int of: %s", object), e);
        }
    }

    /**
     * @param value
     *
     * @return
     *
     * @see IntUtil#toIntValue(Object)
     * @see #toLongValue(Object)
     */
    public static long toLongValue(Object value) {
        Long result = toLong(value);
        return result == null ? 0 : result;
    }

    public static long avg(long... values) {
        long ret = 0;
        int len = values.length;
        for (int i = 0; i < len; ret += values[i++]) {
        }
        return ret / len;
    }

    public static long sum(long... values) {
        long ret = 0;
        int len = values.length;
        for (int i = 0; i < len; i++) {
            ret += values[i];
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

    public static Long[] toObjectArr(long... values) {
        return Arrays2.LONGS.toObjects(values);
    }

    public static long[] toPrimitiveArr(long defaultIfNull, Long... values) {
        int length = values == null ? 0 : values.length;
        if (length == 0) { return Arrays2.LONGS.empty(); }
        long[] result = new long[length];
        for (int i = 0; i < length; i++) {
            result[i] = values[i] == null ? defaultIfNull : values[i];
        }
        return result;
    }

    /**
     * 进制转换：支持十进制至 2 ~ 62 进制的转换
     * （Copied from jdk 1.8: {@link Long#toString(long, int)}）
     * <p>
     * {@code Long}仅支持 36 进制转换，这里扩展到 62 进制
     * 与{@link Long#parseLong(String)}、{@link Long#toString(long, int)}不兼容
     *
     * @param value 待转换的十进制长整型数
     * @param radix 进制
     *
     * @return 转换后的字符串
     *
     * @see IntUtil#toString(int, int) 整形进制转换
     */
    @SuppressWarnings("all")
    public static String toString(long value, int radix) {
        if (radix < Character.MIN_RADIX) {
            radix = TEN;
        }
        if (radix > DIGITS.length) {
            radix = DIGITS.length;
        }
        if (radix == TEN) {
            return Long.toString(value);
        }
        int maxLen = radix < TEN ? 65 : 20;

        char[] buf = new char[maxLen];
        int charPos = maxLen - 1;
        boolean negative = (value < 0);

        if (!negative) {
            value = -value;
        }

        while (value <= -radix) {
            buf[charPos--] = DIGITS[(int) (-(value % radix))];
            value = value / radix;
        }
        buf[charPos] = DIGITS[(int) (-value)];

        if (negative) {
            buf[--charPos] = '-';
        }

        return new String(buf, charPos, (maxLen - charPos));
    }

    /**
     * 最大 62 进制字符串解析成十进制整数，和上面的{@link #toString(long, int)}相对应
     * 与{@link Long#parseLong(String, int)}、{@link Long#toString(long, int)}不兼容
     *
     * @param s     待解析字符串
     * @param radix 进制
     *
     * @return 解析后的整数
     *
     * @throws NumberFormatException 进制错误时抛出异常
     */
    @SuppressWarnings("all")
    public static long parseLong(String s, int radix) throws NumberFormatException {
        if (s == null) {
            throw new NumberFormatException("null");
        }
        if (radix < 2) {
            throw new NumberFormatException("radix " + radix + " less than 2");
        }
        if (radix > DIGITS.length) {
            throw new NumberFormatException("radix " + radix + " greater than 62");
        }

        long result = 0;
        boolean negative = false;
        int i = 0, len = s.length();
        long limit = -Long.MAX_VALUE;
        long multmin;
        int digit;

        if (len > 0) {
            char firstChar = s.charAt(0);
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true;
                    limit = Long.MIN_VALUE;
                } else if (firstChar != '+') { throw new NumberFormatException(s); }

                if (len == 1) { // Cannot have lone "+" or "-"
                    throw new NumberFormatException(s);
                }
                i++;
            }
            multmin = limit / radix;
            while (i < len) {
                // Accumulating negatively avoids surprises near MAX_VALUE
                // digit = Character.digit(s.charAt(i++), radix);
                digit = CharUtil.toDigitMaxAs62(s.charAt(i++));
                if (digit < 0) {
                    throw new NumberFormatException(s);
                }
                if (result < multmin) {
                    throw new NumberFormatException(s);
                }
                result *= radix;
                if (result < limit + digit) {
                    throw new NumberFormatException(s);
                }
                result -= digit;
            }
        } else {
            throw new NumberFormatException(s);
        }
        return negative ? result : -result;
    }
}
