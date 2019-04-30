package com.moon.core.util.require;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
final class AssertUtil {

    final static String MARK = "{}";

    private AssertUtil() {
        noInstanceError();
    }

    final static String format(String template, boolean assertion, Object value) {
        StringBuilder builder = defaultBuilder(assertion, 50);
        int index = template.indexOf(MARK), prevIndex;
        if (index >= 0) {
            builder.append(template, 0, index).append(assertion);
            index = template.indexOf(MARK, prevIndex = index + 2);
            if (index >= 0) {
                builder.append(template, prevIndex, index).append(value);
                builder.append(template, index + 2, template.length());
            } else {
                builder.append(value);
            }
        } else {
            builder.append(assertion).append(value);
        }
        builder.append("\033[038m");
        return builder.toString();
    }

    final static String format(String template, boolean assertion, Object value1, Object value2) {
        StringBuilder builder = defaultBuilder(assertion, 100);
        int index = template.indexOf(MARK), prevIndex;
        if (index >= 0) {
            builder.append(template, 0, index).append(assertion);
            index = template.indexOf(MARK, prevIndex = index + 2);
            if (index >= 0) {
                builder.append(template, prevIndex, index).append(value1);
                index = template.indexOf(MARK, prevIndex = index + 2);
                if (index >= 0) {
                    builder.append(template, prevIndex, index).append(value2);
                    builder.append(template, index + 2, template.length());
                } else {
                    builder.append(value2);
                }
            } else {
                builder.append(value1).append(value2);
            }
        } else {
            builder.append(assertion).append(value1).append(value2);
        }
        builder.append("\033[038m");
        return builder.toString();
    }

    static StringBuilder defaultBuilder(boolean assertion, int len) {
        return new StringBuilder(len).append(assertion ? "\033[032m" : "\033[031m");
    }
}
