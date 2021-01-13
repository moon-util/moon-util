package com.moon.processor.file;

import com.moon.processor.manager.Importer;
import com.moon.processor.utils.String2;

/**
 * @author benshaoye
 */
public enum Formatter2 {
    ;
    private final static Object[] OBJECTS = {};

    public static boolean isOverLength(int length) { return length > 80; }

    public static String toTypedFormatted(Importer importer, String pattern, Object... values) {
        values = values == null ? OBJECTS : values;
        Object[] mapped = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            Object type = values[i];
            if (type instanceof Class<?>) {
                mapped[i] = importer.onImported((Class<?>) type);
            } else if (type == null) {
                mapped[i] = "";
            } else {
                String stringify = type.toString();
                /**
                 * 两个方法的区别在这里{@link #toFormatted(Importer, String, Object...)}
                 */
                mapped[i] = String2.isBlank(stringify) ? "" : importer.onImported(stringify);
            }
        }
        return String2.format(pattern, mapped);
    }

    public static String toFormatted(Importer importer, String pattern, Object... values) {
        values = values == null ? OBJECTS : values;
        Object[] mapped = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            Object type = values[i];
            if (type instanceof Class<?>) {
                mapped[i] = importer.onImported((Class<?>) type);
            } else if (type == null) {
                mapped[i] = "";
            } else {
                String stringify = type.toString();
                /**
                 * 两个方法的区别在这里{@link #toTypedFormatted(Importer, String, Object...)}
                 */
                mapped[i] = String2.isBlank(stringify) ? "" : stringify;
            }
        }
        return String2.format(pattern, mapped);
    }
}
