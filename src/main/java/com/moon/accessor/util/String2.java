package com.moon.accessor.util;

/**
 * @author benshaoye
 */
public class String2 {

    private final static char BACKSLASH = '\\';

    protected String2() { throw new AssertionError("No com.moon.accessor.util.String2 instances for you!"); }

    public static boolean isBlank(CharSequence string) {
        if (string != null) {
            for (int i = 0, len = string.length(); i < len; i++) {
                if (!Character.isWhitespace(string.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence string) { return !isBlank(string); }

    /**
     * 格式化字符串，将模板中的占位符（{}）按顺序替换为指定值的字符串形式，剩余的值
     * <p>
     * 直接追加到最终字符串末尾
     *
     * @param template 字符串模板
     * @param values   有序的待替换值
     *
     * @return 替换完成后的字符串
     */
    public static String format(String template, Object... values) {
        if (template == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return template;
        }
        int startIdx = 0, idx = 0;
        final int valueLen = values.length, tempLen = template.length();
        StringBuilder builder = new StringBuilder(tempLen);
        for (int at, nextStartAt; ; ) {
            at = template.indexOf("{}", startIdx);
            if (at >= 0) {
                nextStartAt = at + 2;
                builder.append(template, startIdx, at);
                builder.append(values[idx++]);
                if (idx >= valueLen) {
                    return builder.append(template, nextStartAt, tempLen).toString();
                }
                startIdx = nextStartAt;
            } else {
                for (int i = idx; i < valueLen; i++) {
                    builder.append(values[i]);
                }
                return builder.toString();
            }
        }
    }

    /**
     * 将字符串中的某些字符转义，如在 java 字符串中的双引号‘"’前加反斜线成‘\"’
     *
     * @param content    待转义字符串
     * @param targetChar 将要转义的字符
     *
     * @return 转义完成的字符串
     */
    public static String withEscaped(String content, char targetChar) {
        if (content == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        int backslashCnt = 0;
        char[] chars = content.toCharArray();
        for (char ch : chars) {
            if (ch == BACKSLASH) {
                backslashCnt++;
            } else {
                if ((ch == targetChar) && backslashCnt % 2 == 0) {
                    // 暂时 builder.append(BACKSLASH).append(BACKSLASH); 处理
                    builder.append(BACKSLASH);
                }
                backslashCnt = 0;
            }
            builder.append(ch);
        }
        return builder.toString();
    }
}
