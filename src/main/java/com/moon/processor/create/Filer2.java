package com.moon.processor.create;

import javax.lang.model.element.Modifier;
import java.util.*;

/**
 * @author benshaoye
 */
enum Filer2 {
    ;

    private final static int INDENT_UNIT = 4;

    private final static Map<Integer, String> INDENT_MAP = new HashMap<>();

    static { INDENT_MAP.put(0, ""); }

    public static final Set<Modifier> FOR_INTERFACE = new HashSet<>();

    static { FOR_INTERFACE.add(Modifier.PUBLIC); }

    public static String indent(int count) {
        String indent = INDENT_MAP.get(count);
        if (indent != null) {
            return indent;
        }
        int spaceCount = count * INDENT_UNIT;
        char[] chars = new char[spaceCount];
        Arrays.fill(chars, ' ');
        indent = new String(chars);
        INDENT_MAP.put(count, indent);
        return indent;
    }
}
