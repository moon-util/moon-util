package com.moon.core.lang;

import com.moon.core.enums.ArraysEnum;
import com.moon.core.lang.ref.IntAccessor;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

import static com.moon.core.lang.ThrowUtil.noInstanceError;
import static java.lang.Character.isWhitespace;

/**
 * 本类不推荐任何形式调用,随时可能会改
 *
 * @author benshaoye
 * @date 2018/9/11
 */
public final class SupportUtil {

    private final static char DOT = '.';

    private SupportUtil() {
        noInstanceError("这个类不推荐任何形式调用");
    }

    public static int addTrueValue(char[] data, int posBegin) {
        data[posBegin++] = 't';
        data[posBegin++] = 'r';
        data[posBegin++] = 'u';
        data[posBegin++] = 'e';
        return posBegin;
    }

    public static int addFalseValue(char[] data, int posBegin) {
        data[posBegin++] = 'f';
        data[posBegin++] = 'a';
        data[posBegin++] = 'l';
        data[posBegin++] = 's';
        data[posBegin++] = 'e';
        return posBegin;
    }

    public static int addBooleanValue(char[] data, boolean bool, int posBegin) {
        return bool ? addTrueValue(data, posBegin) : addFalseValue(data, posBegin);
    }

    public static int addNullValue(char[] data, int posBegin) {
        data[posBegin++] = 'n';
        data[posBegin++] = 'u';
        data[posBegin++] = 'l';
        data[posBegin++] = 'l';
        return posBegin;
    }

    public static int addUndefinedValue(char[] data, int posBegin) {
        data[posBegin++] = 'u';
        data[posBegin++] = 'n';
        data[posBegin++] = 'd';
        data[posBegin++] = 'e';
        data[posBegin++] = 'f';
        data[posBegin++] = 'i';
        data[posBegin++] = 'n';
        data[posBegin++] = 'e';
        data[posBegin++] = 'd';
        return posBegin;
    }

    public final static String parseVar(char[] chars, IntAccessor indexer, int len, int current) {
        char curr = (char) current;
        char[] value = {curr};
        int index = value.length, i = indexer.get();
        for (; i < len && (isVar(curr = chars[i]) || isNum(curr)); i++) {
            value = setChar(value, index++, curr);
        }
        setIndexer(indexer, i, len);
        return toStr(value, index);
    }

    public final static Number parseNum(char[] chars, IntAccessor indexer, int len, int current) {
        char curr = (char) current;
        char[] value = {curr};
        int index = value.length, i = indexer.get();
        final Number number;
        for (; i < len && isNum(curr = chars[i]); i++) {
            value = setChar(value, index++, curr);
        }
        if (curr == DOT) {
            final int cacheIndex = index, cacheI = i + 1;
            value = setChar(value, index++, curr);
            for (++i; i < len && isNum(curr = chars[i]); i++) {
                value = setChar(value, index++, curr);
            }
            if (cacheI == i && isVar(curr)) {
                i--;
                number = Integer.parseInt(toStr(value, cacheIndex));
            } else {
                number = Double.parseDouble(toStr(value, index));
            }
        } else {
            number = Integer.parseInt(toStr(value, index));
        }
        setIndexer(indexer, i, len);
        return number;
    }

    public final static String parseStr(char[] chars, IntAccessor indexer, int endChar) {
        char[] value = ArraysEnum.CHARS.empty();
        int index = 0, pr = -1, es = '\\', i = indexer.get();
        for (char ch; (ch = chars[i++]) != endChar || pr == es; pr = ch) {
            if (ch == endChar && pr == es) {
                value[index - 1] = ch;
            } else {
                value = setChar(value, index++, ch);
            }
        }
        indexer.set(i);
        return toStr(value, index);
    }

    public final static void setIndexer(IntAccessor indexer, int index, int len) {
        indexer.set(index < len ? index : len);
    }

    public final static String toStr(char[] chars, int len) {
        return new String(chars, 0, len);
    }

    public final static boolean isNum(int value) {
        return value > 47 && value < 58;
    }

    public final static boolean isVar(int value) {
        return CharUtil.isLetter(value) || value == '$'
            || value == '_' || CharUtil.isChinese(value);
    }

    public final static int skipWhitespaces(char[] chars, IntAccessor indexer, final int len) {
        int index = indexer.get(), ch = 0;
        while (index < len && isWhitespace(ch = chars[index++])) {
        }
        indexer.set(index);
        return ch;
    }

    public final static <T> T throwErr(char[] chars, IntAccessor indexer) {
        int amount = 12, len = chars.length, index = indexer.get();
        int end = index + amount < len ? index + amount : len;
        int start = index < amount ? 0 : index - amount;
        throw new IllegalArgumentException(
            new StringBuilder((amount + 5) * 2)
                .append(">>>>>")
                .append(chars, start, end - start)
                .append("<<<<<").toString()
        );
    }

    /**
     * 当数组或集合只有一项时，返回第一项，否则返回数组长度或集合 size
     * 其他自定义对象或集合均抛出异常
     *
     * @param o
     * @return
     */
    public static Object onlyOneItemOrSize(Object o) {
        if (o == null) {
            return null;
        }
        int size;
        if (o.getClass().isArray()) {
            return ((size = Array.getLength(o)) == 1) ? Array.get(o, 0) : size;
        }
        if (o instanceof Collection) {
            return (size = ((Collection) o).size()) == 1 ? ((Collection) o).iterator().next() : size;
        }
        if (o instanceof Map) {
            return (size = ((Map) o).size()) == 1 ? ((Map) o).values().iterator().next() : size;
        }
        if (o instanceof Iterable) {
            return ((Iterable) o).iterator().next();
        }
        throw new IllegalArgumentException();
    }

    public static <T> T matchOne(Collection<T> collection, Predicate<? super T> test) {
        if (collection != null) {
            for (T t : collection) {
                if (test.test(t)) {
                    return t;
                }
            }
        }
        return null;
    }

    public static char[] setChar(char[] chars, int index, char ch) {
        chars = defaultChars(chars);
        int len = chars.length;
        if (index >= len) {
            char[] newArr = new char[Math.max(len << 1, 8)];
            System.arraycopy(chars, 0, newArr, 0, len);
            chars = newArr;
        }
        chars[index] = ch;
        return chars;
    }

    public static <T> T[] setArrItem(T[] arr, int index, T item) {
        int len = arr.length;
        if (index >= len) {
            Class type = arr.getClass().getComponentType();
            T[] newArray = (T[]) Array.newInstance(type, len << 1);
            System.arraycopy(arr, 0, newArray, 0, len);
            arr = newArray;
        }
        arr[index] = item;
        return arr;
    }

    public static char[] safeGetChars(char[] chars, int index, String str) {
        chars = defaultChars(chars);
        int strLen = str.length();
        int charsL = chars.length;
        int endIdx = strLen + index;
        if (strLen + index > charsL) {
            char[] newArr = new char[endIdx + 8];
            System.arraycopy(chars, 0, newArr, 0, charsL);
            chars = newArr;
        }
        str.getChars(0, strLen, chars, index);
        return chars;
    }

    public static char[] ensureToLength(char[] chars, int newLength, int presentLength) {
        if (chars == null) {
            return new char[newLength];
        }
        if (chars.length < newLength) {
            char[] newArr = new char[newLength];
            System.arraycopy(chars, 0, newArr, 0, presentLength);
            return newArr;
        }
        return chars;
    }

    public static char[] defaultChars(char[] chars) {
        return chars == null ? new char[8] : chars;
    }
}
