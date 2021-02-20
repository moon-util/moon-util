package com.moon.processing.decl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * @author benshaoye
 */
public class VarHelper {

    private final Map<Object, String> memberCached = new HashMap<>();
    private final Map<Object, String> staticCached = new HashMap<>();
    private int member;
    private int index;

    public VarHelper() { }

    public String next() { return next(emptySet()); }

    public String next(Object key) { return next(key, emptySet()); }

    public String next(Set<String> excludesVars) {
        return toNext(this, excludesVars, false);
    }

    public String next(Object key, Set<String> excludesVars) {
        return computeInCached(this, key, excludesVars, false);
    }

    public String nextStatic() { return nextStatic(emptySet()); }

    public String nextStatic(Object key) { return nextStatic(key, emptySet()); }

    public String nextStatic(Set<String> excludesVars) {
        return toNext(this, excludesVars, true);
    }

    public String nextStatic(Object key, Set<String> excludesVars) {
        return computeInCached(this, key, excludesVars, true);
    }

    private static String computeInCached(
        VarHelper helper, Object key, Set<String> excludesVars, boolean wasStatic
    ) {
        Map<Object, String> caches = wasStatic ? helper.staticCached : helper.memberCached;
        String var = caches.get(key);
        if (var == null) {
            var = wasStatic ? helper.nextStatic(excludesVars) : helper.next(excludesVars);
            caches.put(key, var);
        }
        return var;
    }

    private static String toNext(VarHelper helper, Set<String> excludesVars, boolean wasStatic) {
        String var;
        final String prefix = wasStatic ? "S" : "i";
        final int init = wasStatic ? helper.index : helper.member;
        for (int i = init; ; i++) {
            var = prefix + i;
            if (!excludesVars.contains(var)) {
                if (wasStatic) {
                    helper.index = i + 1;
                } else {
                    helper.member = i + 1;
                }
                return var;
            }
        }
    }
}
