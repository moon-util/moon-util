package com.moon.core.lang;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class RuntimeUtil {

    private RuntimeUtil() { noInstanceError(); }

    public static Runtime getRuntime() {
        return Runtime.getRuntime();
    }

    /**
     * 返回最大内存
     */
    public final long getMaxMemory() { return getRuntime().maxMemory(); }
}
