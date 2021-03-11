package com.moon.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * ThreadLocal 管理工具，主要是为所有受管理的{@link ThreadLocal}提供一个执行周期内
 * 提供一个公共清空入口{@link #clearAll()}和{@link ThreadLocalGroup#clear()}
 *
 * @author benshaoye
 */
public enum ThreadLocalUtil {
    ;

    private final static List<ThreadLocalGroup> GROUPS = new ArrayList<>();
    private final static ThreadLocalGroup GROUP;

    static { GROUP = newThreadLocalGroup(); }

    public static ThreadLocalGroup newThreadLocalGroup() {
        ThreadLocalGroup group = new ThreadLocalGroup();
        GROUPS.add(group);
        return group;
    }

    public static <T> ThreadLocal<T> newThreadLocal() { return GROUP.newThreadLocal(); }

    public static <T> ThreadLocal<T> addThreadLocal(ThreadLocal<T> threadLocal) {
        return GROUP.addThreadLocal(threadLocal);
    }

    public static void clearAll() { GROUPS.forEach(ThreadLocalGroup::clear); }
}
