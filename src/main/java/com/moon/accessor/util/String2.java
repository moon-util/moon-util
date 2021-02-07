package com.moon.accessor.util;

/**
 * @author benshaoye
 */
public enum String2 {
    ;
    private final static char BACKSLASH = '\\';

    public static String format(String template, Object... values) {
        if (values != null && values.length > 0) {
            StringBuilder builder = new StringBuilder(template.length() + values.length * 16);
            int holderPreviousIndex = 0, thisIndex;
            for (Object value : values) {
                thisIndex = template.indexOf("{}", holderPreviousIndex);
                if (thisIndex < 0) {
                    return builder.toString();
                }
                builder.append(template, holderPreviousIndex, thisIndex);
                builder.append(value);
                holderPreviousIndex = thisIndex + 2;
            }
            return builder.toString();
        }
        return template;
    }

    public static String joinQuestionMark(int count, String open, String close) {
        if (count > 0) {
            int iMax = count - 1;
            StringBuilder b = new StringBuilder();
            if (open != null) {
                b.append(open);
            }
            for (int i = 0; ; i++) {
                b.append('?');
                if (i == iMax) {
                    if (close != null) {
                        b.append(close);
                    }
                    return b.toString();
                }
                b.append(", ");
            }
        }
        if (open != null) {
            return close == null ? open : open + close;
        }
        return close;
    }

    public static String doEscape(String content, char targetChar) {
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
                    builder.append(BACKSLASH).append(BACKSLASH);
                }
                backslashCnt = 0;
            }
            builder.append(ch);
        }
        return builder.toString();
    }
}
