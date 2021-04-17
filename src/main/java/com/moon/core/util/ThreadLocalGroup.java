package com.moon.core.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author benshaoye
 */
public class ThreadLocalGroup {

    private final static byte[] PLACEHOLDER = {};
    private final Map<ThreadLocal<?>, Object> threadLocals = new ConcurrentHashMap<>();

    ThreadLocalGroup() {
    }

    public static ThreadLocalGroup of() { return ThreadLocalUtil.newThreadLocalGroup(); }

    public <T> ThreadLocal<T> newThreadLocal() { return addThreadLocal(new ThreadLocal<>()); }

    public <T> ThreadLocal<T> addThreadLocal(ThreadLocal<T> threadLocal) {
        threadLocals.put(threadLocal, PLACEHOLDER);
        return threadLocal;
    }

    public void clear() { threadLocals.forEach((tl, o) -> tl.remove()); }
}
