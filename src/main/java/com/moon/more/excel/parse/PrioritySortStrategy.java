package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.Priority;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
enum PrioritySortStrategy {
    GROUP(Priority.GROUP),
    ORDER(Priority.ORDER);

    private final Priority priority;

    private static class Cached {

        final static Map<Priority, PrioritySortStrategy> CACHE = new HashMap<>();
    }

    PrioritySortStrategy(Priority priority) {
        this.priority = priority;
        Cached.CACHE.put(priority, this);
    }

    static PrioritySortStrategy getSortStrategy(Priority priority) {
        return Cached.CACHE.getOrDefault(priority, GROUP);
    }
}
