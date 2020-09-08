package com.moon.core.lang;

import com.moon.core.enums.Const;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

/**
 * @author moonsky
 */
public final class JoinerUtil {

    /**
     * 经过一定量测试，业务中大多数字符串长度在 9 ~ 18 个字符之间，样本平均值是 14.6
     */
    public static final int DFT_LEN = 16;
    static final String NULL_STR = "null";
    public final static String EMPTY = Const.EMPTY;

    /**
     * 英文逗号：","
     */
    protected static final String DFT_SEP = String.valueOf((char) 44);

    private JoinerUtil() { ThrowUtil.noInstanceError(); }

    /*
     * ---------------------------------------------------------------------------
     * string joiner.
     * ---------------------------------------------------------------------------
     */

    public static StringJoiner of() { return of(DFT_SEP); }

    public static StringJoiner of(CharSequence delimiter) { return StringJoiner.of(delimiter); }

    public static StringJoiner of(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        return StringJoiner.of(delimiter, prefix, suffix);
    }

    /*
     * ---------------------------------------------------------------------------
     * basic type array joiner.
     * ---------------------------------------------------------------------------
     */

    public static String join(boolean[] array) { return join(array, DFT_SEP); }

    public static String join(boolean[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                if (separator == null) {
                    separator = NULL_STR;
                }
                int sepLen = separator.length();
                int newLen = 5 + (5 + sepLen) * (len - 1);

                char[] data = new char[newLen];
                int pos = ParseSupportUtil.addBooleanValue(data, array[0], 0);
                for (int i = 1; i < len; i++) {
                    separator.getChars(0, sepLen, data, pos);
                    pos = ParseSupportUtil.addBooleanValue(data, array[i], pos + sepLen);
                }
                return String.valueOf(data, 0, pos);
            }
            return EMPTY;
        }
        return null;
    }

    public static String join(char[] array) { return join(array, DFT_SEP); }

    public static String join(char[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                if (separator == null) {
                    separator = NULL_STR;
                }
                int length = separator.length();
                if (length == 0) {
                    return new String(array);
                }
                int size = len + (len - 1) * length;
                char[] ret = new char[size];
                int descBegin = 0;
                ret[descBegin] = array[descBegin];
                for (int i = 1; i < len; i++) {
                    separator.getChars(0, length, ret, ++descBegin);
                    ret[descBegin += length] = array[i];
                }
                return new String(ret);
            }
            return EMPTY;
        }
        return null;
    }

    public static String join(byte[] array) { return join(array, DFT_SEP); }

    public static String join(byte[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + 5) * len);
                for (int i = 0; i < len; i++) {
                    sb.append(separator).append(array[i]);
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    public static String join(short[] array) { return join(array, DFT_SEP); }

    public static String join(short[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + 5) * len);
                for (int i = 0; i < len; i++) {
                    sb.append(separator).append(array[i]);
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    public static String join(int[] array) { return join(array, DFT_SEP); }

    public static String join(int[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + 5) * len);
                for (int i = 0; i < len; i++) {
                    sb.append(separator).append(array[i]);
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    public static String join(long[] array) { return join(array, DFT_SEP); }

    public static String join(long[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + 5) * len);
                for (int i = 0; i < len; i++) {
                    sb.append(separator).append(array[i]);
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    public static String join(float[] array) { return join(array, DFT_SEP); }

    public static String join(float[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + 5) * len);
                for (int i = 0; i < len; i++) {
                    sb.append(separator).append(array[i]);
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    public static String join(double[] array) { return join(array, DFT_SEP); }

    public static String join(double[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + 5) * len);
                for (int i = 0; i < len; i++) {
                    sb.append(separator).append(array[i]);
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    /*
     * ---------------------------------------------------------------------------
     * Object array joiner.
     * ---------------------------------------------------------------------------
     */

    public static <T> String join(T[] array) { return join(array, DFT_SEP); }

    public static <T> String join(T[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + 5) * len);
                for (int i = 0; i < len; i++) {
                    sb.append(separator).append(array[i]);
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    public static <T> String joinRequireNonNull(T[] array) { return joinRequireNonNull(array, DFT_SEP); }

    public static <T> String joinRequireNonNull(T[] array, String separator) {
        requireNonNull(array);
        int len = array.length;
        if (len > 0) {
            int sepLen = separatorLength(requireNonNull(separator));
            StringBuilder sb = new StringBuilder((sepLen + 5) * len);
            for (int i = 0; i < len; i++) {
                sb.append(separator).append(requireNonNull(array[i]));
            }
            return sb.substring(sepLen);
        }
        return EMPTY;
    }

    public static <T> String joinSkipNulls(T[] array) { return joinSkipNulls(array, DFT_SEP); }

    public static <T> String joinSkipNulls(T[] array, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + 5) * len);
                int mark = 0;
                for (int i = 0; i < len; i++) {
                    if (array[i] != null) {
                        sb.append(separator).append(array[i]);
                        mark = 1;
                    }
                }
                return mark == 1 ? sb.substring(sepLen) : EMPTY;
            }
            return EMPTY;
        }
        return null;
    }

    public static <T> String joinUseForNull(T[] array, T forNull) { return joinUseForNull(array, forNull, DFT_SEP); }

    public static <T> String joinUseForNull(T[] array, T forNull, String separator) {
        if (array != null) {
            int len = array.length;
            if (len > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + 5) * len);
                for (int i = 0; i < len; i++) {
                    if (array[i] == null) {
                        sb.append(separator).append(forNull);
                    } else {
                        sb.append(separator).append(array[i]);
                    }
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    /*
     * ---------------------------------------------------------------------------
     * collection joiner.
     * ---------------------------------------------------------------------------
     */

    public static <T> String join(Collection<T> collection) { return join(collection, DFT_SEP); }

    public static <T> String join(Collection<T> collection, String separator) {
        if (collection != null) {
            int size = collection.size();
            if (size > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + DFT_LEN) * size);
                for (T item : collection) {
                    sb.append(separator).append(item);
                }
                return sb.substring(sepLen);
            }
            return EMPTY;
        }
        return null;
    }

    public static <T> String joinRequireNonNull(Collection<T> collection) {
        return joinRequireNonNull(collection, DFT_SEP);
    }

    public static <T> String joinRequireNonNull(Collection<T> collection, String separator) {
        Iterable<T> iterable = collection;
        return joinRequireNonNull(iterable, separator);
    }

    public static <T> String joinSkipNulls(Collection<T> collection) { return joinSkipNulls(collection, DFT_SEP); }

    public static <T> String joinSkipNulls(Collection<T> collection, String separator) {
        if (collection != null) {
            int size = collection.size();
            if (size > 0) {
                int sepLen = separatorLength(requireNonNull(separator));
                StringBuilder sb = new StringBuilder((sepLen + DFT_LEN) * size);
                int mark = 0;
                for (T item : collection) {
                    if (item != null) {
                        sb.append(separator).append(item);
                        mark = 1;
                    }
                }
                return mark == 1 ? sb.substring(sepLen) : EMPTY;
            }
            return EMPTY;
        }
        return null;
    }

    /*
     * ---------------------------------------------------------------------------
     * iterable joiner.
     * ---------------------------------------------------------------------------
     */

    public static <T> String join(Iterable<T> iterable) { return join(iterable, DFT_SEP); }

    public static <T> String join(Iterable<T> iterable, String separator) {
        if (iterable != null) {
            StringBuilder joiner = joinerBySeparator(separator);
            for (T item : iterable) {
                joiner.append(separator).append(item);
            }
            return joiner.substring(separatorLength(separator));
        }
        return null;
    }

    public static <T> String joinRequireNonNull(Iterable<T> iterable) { return joinRequireNonNull(iterable, DFT_SEP); }

    public static <T> String joinRequireNonNull(Iterable<T> iterable, String separator) {
        requireNonNull(iterable);
        StringBuilder joiner = joinerBySeparator(separator);
        for (T item : iterable) {
            joiner.append(separator).append(requireNonNull(item));
        }
        return joiner.substring(separatorLength(separator));
    }

    public static <T> String joinSkipNulls(Iterable<T> iterable) { return joinSkipNulls(iterable, DFT_SEP); }

    public static <T> String joinSkipNulls(Iterable<T> iterable, String separator) {
        if (iterable != null) {
            StringBuilder joiner = joinerBySeparator(separator);
            int mark = 0;
            for (T item : iterable) {
                if (item != null) {
                    joiner.append(separator).append(item);
                    mark = 1;
                }
            }
            return mark == 1 ? joiner.substring(separatorLength(separator)) : EMPTY;
        }
        return null;
    }

    public static <T> String joinUseForNull(Iterable<T> iterable, String forNull) {
        return joinUseForNull(iterable, forNull, DFT_SEP);
    }

    public static <T> String joinUseForNull(Iterable<T> iterable, String forNull, String separator) {
        if (iterable != null) {
            StringBuilder joiner = joinerBySeparator(separator);
            for (T item : iterable) {
                joiner.append(separator).append(item == null ? forNull : item);
            }
            return joiner.substring(separatorLength(separator));
        }
        return null;
    }

    /*
     * ---------------------------------------------------------------------------
     * iterator joiner.
     * ---------------------------------------------------------------------------
     */

    public static <T> String join(Iterator<T> iterator) { return join(iterator, DFT_SEP); }

    public static <T> String join(Iterator<T> iterator, String separator) {
        if (iterator != null) {
            StringBuilder joiner = joinerBySeparator(separator);
            while (iterator.hasNext()) {
                joiner.append(separator).append(iterator.next());
            }
            return joiner.substring(separatorLength(separator));
        }
        return null;
    }

    public static <T> String joinRequireNonNull(Iterator<T> iterator) { return joinRequireNonNull(iterator, DFT_SEP); }

    public static <T> String joinRequireNonNull(Iterator<T> iterator, String separator) {
        requireNonNull(iterator);
        StringBuilder joiner = joinerBySeparator(separator);
        while (iterator.hasNext()) {
            joiner.append(separator).append(requireNonNull(iterator.next()));
        }
        return joiner.substring(separatorLength(separator));
    }

    public static <T> String joinSkipNulls(Iterator<T> iterator) { return joinSkipNulls(iterator, DFT_SEP); }

    public static <T> String joinSkipNulls(Iterator<T> iterator, String separator) {
        if (iterator != null) {
            StringBuilder joiner = joinerBySeparator(separator);
            while (iterator.hasNext()) {
                T item = iterator.next();
                if (item != null) {
                    joiner.append(separator).append(item);
                }
            }
            return joiner.substring(separatorLength(separator));
        }
        return null;
    }

    public static <T> String joinUseForNull(Iterator<T> iterator, String forNull) {
        return joinUseForNull(iterator, forNull, DFT_SEP);
    }

    public static <T> String joinUseForNull(Iterator<T> iterator, String forNull, String separator) {
        if (iterator != null) {
            StringBuilder joiner = joinerBySeparator(separator);
            while (iterator.hasNext()) {
                T item = iterator.next();
                joiner.append(separator).append(item == null ? forNull : item);
            }
            return joiner.substring(separatorLength(separator));
        }
        return null;
    }

    /*
     * ---------------------------------------------------------------------------
     * apply function joiner.
     * ---------------------------------------------------------------------------
     */

    public static <T> String joinWithMapper(Iterable<T> iterable, Function<T, String> func) {
        return joinWithMapper(iterable, func, DFT_SEP);
    }

    public static <T> String joinWithMapper(Iterable<T> iterable, Function<T, String> func, String separator) {
        return iterable == null ? null : joinWithMapper(iterable.iterator(), func, separator);
    }

    public static <T> String joinWithMapper(Iterator<T> iterator, Function<T, String> func) {
        return joinWithMapper(iterator, func, DFT_SEP);
    }

    public static <T> String joinWithMapper(Iterator<T> iterator, Function<T, String> func, String separator) {
        int sepLen = separator.length();
        StringBuilder sb = new StringBuilder((sepLen + DFT_LEN) * 16);
        while (iterator.hasNext()) {
            sb.append(separator).append(func.apply(iterator.next()));
        }
        return sb.substring(sepLen);
    }

    /*
     * ---------------------------------------------------------------------------
     * inner methods
     * ---------------------------------------------------------------------------
     */

    private final static StringBuilder joinerBySeparator(String separator) {
        return new StringBuilder((separatorLength(requireNonNull(separator)) + DFT_LEN) * 16);
    }

    private static int separatorLength(CharSequence cs) { return cs == null ? 4 : cs.length(); }

    static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }
}
