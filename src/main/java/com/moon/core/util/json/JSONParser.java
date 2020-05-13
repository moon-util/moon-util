package com.moon.core.util.json;

import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.IntAccessor;

import java.util.Objects;

/**
 * @author benshaoye
 */
final class JSONParser {

    final static char QUOTES = '\"';
    final static char LEFT_O = '{';
    final static char LEFT_A = '[';
    final static char RIGHT_O = '}';
    final static char RIGHT_A = ']';
    final static char COLON = ':';
    final static char COMMA = ',';
    final static char DOT = '.';

    private final static char a = 'a';
    private final static char e = 'e';
    private final static char f = 'f';
    private final static char l = 'l';
    private final static char n = 'n';
    private final static char r = 'r';
    private final static char s = 's';
    private final static char t = 't';
    private final static char u = 'u';

    final static char COMMENT_F = '/';
    final static char BACKSLASH = '\\';

    final static int WIN_FIRST_CHAR = 65279;

    final static Boolean TRUE = Boolean.TRUE;
    final static Boolean FALSE = Boolean.FALSE;

    IntAccessor accessor = IntAccessor.of(0);

    private String source;

    private int length;

    private JSON data;

    public JSONParser(String source) {
        this.source = source;
        if (StringUtil.isUndefined(source)) {
            this.data = JSONNull.NULL;
        } else {
            this.length = source.length();
        }
    }

    public JSON toJSON() {
        if (data == null) {
            parse(this.source, this.source.length());
        }
        return data;
    }

    public JSON getData() {
        return toJSON();
    }

    private char startIndexChar() {
        int i = 0;
        int len = this.length;
        char ch;
        do {
            ch = source.charAt(i);
            if (ch != WIN_FIRST_CHAR && ch != 0 && !Character.isWhitespace(ch)) {
                accessor.set(i + 1);
                return ch;
            }
            i++;
        } while (i < len);
        return ThrowUtil.runtime("Not any content of json: " + source);
    }

    /**
     * 返回第一个不为空的字符
     *
     * @param source
     * @param len
     * @param accessor
     * @return
     */
    private char skipWriteSpace(final String source, final int len, IntAccessor accessor) {
        int i = accessor.get();

        char ch;
        for (; i < len; i++) {
            ch = source.charAt(i);
            if (Character.isWhitespace(ch)) {
                continue;
            }
            accessor.set(i + 1);
            return ch;
        }
        return ThrowUtil.runtime(source);
    }

    private void checkRestChars(final String source, IntAccessor accessor) {
        int i = accessor.get();
        int len = length;
        char ch;
        for (; i < len; i++) {
            ch = source.charAt(i);
            if (Character.isWhitespace(ch)) {
                continue;
            }
            ThrowUtil.runtime(
                source.substring(accessor.get()));
        }
    }

    public void parse(final String source, final int len) {
        char curr = startIndexChar();
        Object json = parseNext(source, len, curr);
        if (json == null) {
            data = JSONNull.NULL;
        } else if (json == TRUE) {
            data = JSONBoolean.TRUE;
        } else if (json == FALSE) {
            data = JSONBoolean.FALSE;
        } else if (json instanceof Double) {
            data = new JSONNumber((double) json);
        } else if (json instanceof String) {
            data = new JSONString((String) json);
        } else if (json instanceof JSONArray) {
            data = (JSONArray) json;
        } else if (json instanceof JSONObject) {
            data = (JSONObject) json;
        } else {
            ThrowUtil.runtime(source);
        }
        checkRestChars(source, accessor);
    }

    private Object parseNext(final String source, final int len, char curr) {
        switch (curr) {
            /**
             * 对象
             */
            case LEFT_O:
                return parseObject(source, len, new JSONObject());
            /**
             * 数组
             */
            case LEFT_A:
                return parseArray(source, len, new JSONArray());
            /**
             * 字符串
             */
            case QUOTES:
                return parseString(source, len);
            /**
             * true
             */
            case t:
                return parseBoolean(source, curr);
            /**
             * false
             */
            case f:
                return parseBoolean(source, curr);
            /**
             * null
             */
            case n:
                return parseNull(source, curr);
            default:
                if (Character.isDigit(curr)) {
                    return parseDouble(source, len, curr);
                } else {
                    return ThrowUtil.runtime(source);
                }
        }
    }

    private JSONArray parseArray(final String source, final int len, JSONArray ret) {
        char curr = skipWriteSpace(source, len, accessor);
        if (curr == RIGHT_A) {
            return ret;
        }
        if (curr == COMMA) {
            curr = skipWriteSpace(source, len, accessor);
        }
        ret.add(parseNext(source, len, curr));
        if (skipWriteSpace(source, len, accessor) == RIGHT_A) {
            return ret;
        }
        return parseArray(source, len, ret);
    }

    private JSONObject parseObject(final String source, final int len, JSONObject ret) {
        char curr = skipWriteSpace(source, len, accessor);
        if (curr == QUOTES) {
            String key = parseString(source, len);
            curr = skipWriteSpace(source, len, accessor);
            if (curr == COLON) {
                curr = skipWriteSpace(source, len, accessor);
                Object value = parseNext(source, len, curr);
                curr = skipWriteSpace(source, len, accessor);
                if (curr == COMMA) {
                    ret.put(key, value);
                    return parseObject(source, len, ret);
                } else if (curr == RIGHT_O) {
                    ret.put(key, value);
                    return ret;
                }
            }
        } else if (curr == RIGHT_O) {
            return ret;
        }
        return ThrowUtil.runtime(source.substring(accessor.get()));
    }

    private Object parseNull(final String source, final char curr) {
        int idx = accessor.get();
        if (curr == n
            && source.charAt(idx++) == u
            && source.charAt(idx++) == l
            && source.charAt(idx++) == l) {
            accessor.set(idx);
            return null;
        }
        return ThrowUtil.runtime(source.substring(accessor.get()));
    }

    private Object parseBoolean(final String source, final char curr) {
        int idx = accessor.get();
        if (curr == t) {
            if (source.charAt(idx++) == r
                && source.charAt(idx++) == u
                && source.charAt(idx++) == e) {
                accessor.set(idx);
                return true;
            }
        } else if (curr == f) {
            if (source.charAt(idx++) == a
                && source.charAt(idx++) == l
                && source.charAt(idx++) == s
                && source.charAt(idx++) == e) {
                accessor.set(idx);
                return false;
            }
        }
        return ThrowUtil.runtime(source.substring(accessor.get()));
    }

    /**
     * 通过
     *
     * @param source
     * @param len
     * @return
     */
    private Number parseDouble(final String source, final int len, final char curr) {
        int size = 8, index = 0;
        char[] cache = setChar(new char[size], curr, index++, size), newCache;
        final int start = accessor.get();

        char ch, dot = DOT;
        boolean isDouble = false;
        for (int i = start; i < len; i++) {
            ch = source.charAt(i);
            if (Character.isDigit(ch)) {
                newCache = setChar(cache, ch, index++, size);
                if (newCache != cache) {
                    cache = newCache;
                    size = newCache.length;
                }
                continue;
            }

            if (ch == dot) {
                if (isDouble) {
                    ThrowUtil.runtime(source.substring(start, len));
                }
                isDouble = true;
                newCache = setChar(cache, ch, index++, size);
                if (newCache != cache) {
                    cache = newCache;
                    size = newCache.length;
                }
                continue;
            }

            accessor.set(i);
            String str = toStr(cache, index);
            return isDouble ? Double.valueOf(str) : Long.parseLong(str);
        }
        return ThrowUtil.runtime(source);
    }

    /**
     * 通过
     *
     * @param source
     * @param len
     * @return
     */
    private String parseString(final String source, final int len) {
        int size = 16, index = 0;

        char[] cache = new char[size], newCache;
        final int start = accessor.get();

        char curr, end = QUOTES;
        for (int i = start; i < len; i++) {
            curr = source.charAt(i);
            if (curr == end) {
                accessor.set(i + 1);
                return i == start ? "" : toStr(cache, index);
            } else if (curr == BACKSLASH) {
                newCache = parseEscapeChar(source, i++, curr, cache, index++, size);
                if (newCache != cache) {
                    cache = newCache;
                    size = newCache.length;
                }
            } else {
                newCache = setChar(cache, curr, index++, size);
                if (newCache != cache) {
                    cache = newCache;
                    size = newCache.length;
                }
            }
        }
        return ThrowUtil.runtime(source.substring(start));
    }

    private char[] parseEscapeChar(final String source, int srcIndex, char curr,
                                   char[] chars, int arrIndex, int len) {
        char ch = source.charAt(srcIndex + 1);
        char[][] escapes = JSONCfg.ESCAPES;
        for (int i = 0; i < escapes.length; i++) {
            if (ch == escapes[i][0]) {
                curr = escapes[i][1];
                break;
            }
        }
        return setChar(chars, curr, arrIndex, len);
    }

    private char[] setChar(char[] chars, char ch, int index, int len) {
        if (index < len) {
            chars[index] = ch;
            return chars;
        } else {
            char[] newArr = new char[len << 1];
            System.arraycopy(chars, 0, newArr, 0, len);
            newArr[index] = ch;
            return newArr;
        }
    }

    private String toStr(char[] arr, int end) {
        return new String(arr, 0, end);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(source);
    }

    @Override
    public String toString() {
        return Objects.toString(source);
    }
}
