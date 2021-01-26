package com.moon.accessor.dialect;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author benshaoye
 */
enum Reduce2 {
    ;

    static <T extends Keyword> Set<String> unmodifiableSet(T[] defaults, T[] values) {
        Set<String> cached = new HashSet<>();
        for (Keyword keyword : defaults) {
            cached.add(keyword.name());
        }
        for (Keyword keyword : values) {
            cached.add(keyword.name());
        }
        return Collections.unmodifiableSet(cached);
    }

    static String wrapKeyword(String name, char open, char close) {
        return new StringBuilder().append(name).append(open).append(close).toString();
    }

    public static boolean anyHas(String value, Set<String> set1, Set<String> set2) {
        return set1.contains(value) || set2.contains(value);
    }
}
