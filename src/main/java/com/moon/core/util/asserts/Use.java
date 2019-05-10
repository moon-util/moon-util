package com.moon.core.util.asserts;

import com.moon.core.enums.ArraysEnum;
import com.moon.core.util.Optionally;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.moon.core.lang.StringUtil.format;
import static com.moon.core.lang.ThrowUtil.doThrow;
import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
class Use {
    private Use() { noInstanceError(); }

    final static void err(String error) { throw new IllegalArgumentException(error); }

    final static void err(String template, Object... objects) { doThrow(format(template, objects)); }

    final static boolean blank(CharSequence cs) { return cs == null || cs.length() == 0 || cs.toString().trim().length() == 0;}

    final static boolean emptyS(CharSequence cs) { return cs == null || cs.length() == 0; }

    final static boolean emptyC(Collection c) { return c == null || c.isEmpty(); }

    final static boolean emptyM(Map m) { return m == null || m.isEmpty(); }

    final static boolean emptyO(Object value) {
        if (value == null) { return true; }
        if (value instanceof CharSequence) { return emptyS((CharSequence) value); }
        if (value instanceof Collection) { return emptyC((Collection) value); }
        if (value instanceof Map) { return emptyM((Map) value); }
        if (value instanceof Optional) { return !((Optional) value).isPresent(); }
        if (value instanceof Optionally) { return ((Optionally) value).isAbsent(); }
        if ((value.getClass()).isArray()) { return ArraysEnum.getOrObjects(value).length(value) == 0; }
        if (value instanceof Class && ((Class) value).isEnum()) {
            return ((Class) value).getEnumConstants().length == 0;
        }
        return false;
    }

    final static boolean hasNull(Object[] objects) {
        for (int i = 0, len = objects.length; i < len; i++) {
            if (objects[i] == null) { return true; }
        }
        return false;
    }

    final static boolean hasNull(Iterable collect) {
        for (Object item : collect) {
            if (item == null) { return true; }
        }
        return false;
    }

    final static boolean hasNullVal(Map map) {
        Set<Map.Entry> set = map.entrySet();
        for (Map.Entry entry : set) {
            if (entry.getValue() == null) { return true; }
        }
        return false;
    }
}
