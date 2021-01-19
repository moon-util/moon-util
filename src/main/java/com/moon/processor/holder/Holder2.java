package com.moon.processor.holder;

import com.moon.accessor.Session;
import com.moon.accessor.Supported;
import com.moon.accessor.meta.Table;
import com.moon.processor.utils.String2;

/**
 * @author benshaoye
 */
enum Holder2 {
    ;
    public static final Class<Session> SESSION_CLASS = Session.class;
    public static final int SUPPORTED_LEVEL, MAX_LEVEL;
    public static final String TYPES_JOINED;

    static {
        Supported supported = SESSION_CLASS.getAnnotation(Supported.class);
        SUPPORTED_LEVEL = supported.value();
        MAX_LEVEL = supported.max();
        TYPES_JOINED = toTypesJoined(SUPPORTED_LEVEL);
    }

    public static String toTypesJoined(int count) {
        String[] types = new String[count];
        for (int i = 0; i < count; i++) {
            types[i] = "T" + (i + 1);
        }
        return String.join(", ", types);
    }

    public static String toGenericDeclWithJoined(String joined) {
        return String2.format("<{}, R, TB extends {}<R, TB>>", joined);
    }

    public static String toGenericUsingWithJoined(String joined) {
        return String2.format("<{}, R, TB>", joined, Table.class.getCanonicalName());
    }
}
