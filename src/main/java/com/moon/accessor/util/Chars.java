package com.moon.accessor.util;

import com.moon.accessor.util.Indexer;

/**
 * @author benshaoye
 */
public enum Chars {
    ;

    private final static int NOT_FOUND = -1;
    private final static char[] CHARS = {};

    /**
     * 返回从 indexer 位置开始的第一个非空白字符位置
     *
     * @param chars
     * @param indexer
     *
     * @return
     */
    public static int skipWhitespace(char[] chars, Indexer indexer) {
        for (int i = indexer.get(), len = chars.length; i < len; i++) {
            if (!Character.isWhitespace(chars[i])) {
                indexer.set(i);
                return i;
            }
        }
        return NOT_FOUND;
    }

    /**
     * 返回被 end 字符包裹的字符串，如:
     *
     * <pre>
     * parseStr("3`456`", 2, '`') => "456";  这里的 2 实际上是 index 为 2 的 Indexer
     *
     * 解析完成后 indexer 指向 end 的下一个位置
     * </pre>
     *
     * @param chars
     * @param indexer
     * @param endChar
     *
     * @return
     *
     * @see Indexer
     */
    public static String parseStr(char[] chars, Indexer indexer, char endChar) {
        char[] value = CHARS;
        int index = 0, pr = -1, es = '\\', i = indexer.get();
        for (char ch; (ch = chars[i++]) != endChar || pr == es; pr = ch) {
            if (ch == endChar && pr == es) {
                value[index - 1] = ch;
            } else {
                value = setChar(value, index++, ch);
            }
        }
        indexer.set(i);
        return new String(chars, 0, index);
    }

    public static char[] setChar(char[] chars, int index, char ch) {
        chars = chars == null ? new char[8] : chars;
        int len = chars.length;
        if (index >= len) {
            char[] newArr = new char[Math.max(len << 1, 8)];
            System.arraycopy(chars, 0, newArr, 0, len);
            chars = newArr;
        }
        chars[index] = ch;
        return chars;
    }
}
