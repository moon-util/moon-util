package com.moon.processor.file;

import com.moon.processor.manager.Importable;
import com.moon.processor.utils.String2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author benshaoye
 */
public enum Formatter2 {
    ;
    private final static Object[] OBJECTS = {};

    public static String toParamsDeclared(Importable importer, Map<String, String> parameters) {
        return toParamsDeclared(importer, parameters, false);
    }

    public static String toParamsDeclared(Importable importer, Map<String, String> parameters, final boolean simplify) {
        parameters = parameters == null ? Collections.emptyMap() : parameters;
        List<String> list = new ArrayList<>(parameters.size());
        parameters.forEach((name, type) -> {
            String imported = importer.onImported(type);
            if (simplify) {
                list.add(String2.format("{}", trimToSimplify(imported)));
            } else {
                list.add(String2.format("{} {}", imported, name));
            }
        });
        return String.join(", ", list);
    }

    public static String trimToSimplify(String fulledClassname) {
        int index = fulledClassname.indexOf('<');
        if (index < 0) {
            return fulledClassname;
        } else {
            return fulledClassname.substring(0, index);
        }
    }

    public static boolean isOverLength(int length) { return length > 80; }

    public static String toFormatted(String pattern, Object... values) {
        values = values == null ? OBJECTS : values;
        Object[] mapped = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            Object type = values[i];
            if (type instanceof Class<?>) {
                mapped[i] = ((Class<?>) type).getCanonicalName();
            } else if (type == null) {
                mapped[i] = "";
            } else {
                String stringify = type.toString();
                /**
                 * 两个方法的区别在这里{@link #toTypedFormatted(Importable, String, Object...)}
                 */
                mapped[i] = String2.isBlank(stringify) ? "" : stringify;
            }
        }
        return String2.format(pattern, mapped);
    }

    public static String onlyColonTail(String script) {
        final int initLastIndex = script.length() - 1;
        int lastIndex = initLastIndex;
        boolean present = false;
        for (int i = initLastIndex; i >= 0; i--) {
            char ch = script.charAt(i);
            if (ch == ';') {
                if (present) {
                    lastIndex--;
                }
                present = true;
            } else if (Character.isWhitespace(ch)) {
                if (present) {
                    break;
                }
            } else {
                break;
            }
        }
        if (present) {
            return script.substring(0, lastIndex + 1);
        } else {
            return script + ';';
        }
    }
}
