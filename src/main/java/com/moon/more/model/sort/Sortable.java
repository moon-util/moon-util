package com.moon.more.model.sort;

/**
 * @author moonsky
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
