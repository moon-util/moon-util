package com.moon.processor.file;

import com.moon.processor.holder.Importable;
import com.moon.processor.utils.String2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author benshaoye
 */
public enum Formatter2 {
    ;
    private final static Object[] OBJECTS = {};

    private final static int MAX = 80;

    public static boolean isOver(int length, int max) { return length > max; }

    public static boolean isOverLength(int length) { return isOver(length, MAX); }

    public static boolean isOverLength(CharSequence... sequences) { return isOver(MAX, sequences); }

    public static boolean isOver(int max, CharSequence... sequences) {
        int sum = 0;
        for (CharSequence sequence : sequences) {
            if ((sum += String2.length(sequence)) > max) {
                return true;
            }
        }
        return false;
    }

    public static String toParamsDeclared(Importable importer, DeclParams parameters, final boolean simplify) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }
        List<String> list = new ArrayList<>(parameters.size());
        parameters.forEach((name, type) -> {
            if (type.isGeneric()) {
                // 如果是泛型
                if (simplify) {
                    // 返回方法声明上限
                    list.add(importer.onImported(type.getBound()));
                } else {
                    // 返回实际声明
                    list.add(String2.format("{} {}", type.getType(), name));
                }
            } else {
                // 不是泛型
                // java.util.List<java.lang.String> == List<String>
                String imported = importer.onImported(type.getType());
                if (simplify) {
                    // 简化: List<String> == List
                    list.add(String2.format("{}", trimToSimplify(imported)));
                } else {
                    // 返回实际声明
                    list.add(String2.format("{} {}", imported, name));
                }
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

    public static String formatGenericDeclared(Importable importer, String generics, Object... values) {
        return String2.format(generics, Arrays.stream(values).map(value -> {
            if (value instanceof Class<?>) {
                return importer.onImported((Class<?>) value);
            } else {
                return importer.onImported(value.toString());
            }
        }).toArray(Object[]::new));
    }

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
