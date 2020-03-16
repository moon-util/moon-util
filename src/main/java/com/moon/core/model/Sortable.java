package com.moon.core.model;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface Sortable<T> {

    /**
     * 序号
     *
     * @return
     */
    T getSortValue();
}
