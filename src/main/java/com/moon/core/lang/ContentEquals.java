package com.moon.core.lang;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface ContentEquals {

    /**
     * 内容相同
     *
     * @param other
     *
     * @return
     *
     * @see Object#equals(Object)
     */
    boolean contentEquals(Object other);
}
