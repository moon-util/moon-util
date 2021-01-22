package com.moon.accessor.util;

/**
 * @author benshaoye
 */
public enum String2 {
    ;

    public static String format(String template, Object... values) {
        if (values != null && values.length > 0) {
            StringBuilder builder = new StringBuilder(template.length() + values.length * 16);
            int holderPreviousIndex = 0, thisIndex;
            for (int i = 0, len = values.length; i < len; i++) {
                thisIndex = template.indexOf("{}", holderPreviousIndex);
                if (thisIndex < 0) {
                    return builder.toString();
                }
                builder.append(template, holderPreviousIndex, thisIndex);
                builder.append(values[i]);
                holderPreviousIndex = thisIndex + 2;
            }
            return builder.toString();
        }
        return template;
    }
}
