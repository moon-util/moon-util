package com.moon.core.util;

/**
 * @author benshaoye
 */
public interface Optionally {
    /**
     * 是否存在
     *
     * @return
     */
    boolean isPresent();

    /**
     * 是否缺失
     *
     * @return
     */
    default boolean isAbsent() { return !isPresent(); }
}
